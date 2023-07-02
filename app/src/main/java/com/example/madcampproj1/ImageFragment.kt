package com.example.madcampproj1

import android.graphics.drawable.BitmapDrawable
import android.support.media.ExifInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.net.Uri
import android.view.ScaleGestureDetector

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.IOException
import java.lang.Float.max
import java.lang.Float.min
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat

class ImageFragment : Fragment() {

    private lateinit var gestureDetector: GestureDetectorCompat

    companion object {
        private const val ARG_IMAGE_URI = "image_uri"

        fun newInstance(imageUri: Uri): ImageFragment {
            val args = Bundle().apply {
                putParcelable(ARG_IMAGE_URI, imageUri)
            }
            return ImageFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var imageView: ImageView
    private var scaleFactor = 1f
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById<ImageView>(R.id.iv_image)
        val imageUri = arguments?.getParcelable<Uri>(ARG_IMAGE_URI)

        val rotation = if (imageUri != null) {
            try {
                requireContext().contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }
                } ?: 0f
            } catch (e: IOException) {
                e.printStackTrace()
                0f
            }
        } else {
            0f
        }

        Picasso.get()
            .load(imageUri)
            .rotate(rotation)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .fit()
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                    onPalette(Palette.from(bitmap).generate())
                }
                override fun onError(e: Exception?) {
                }
            })

        scaleGestureDetector = ScaleGestureDetector(
            requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                private var lastFocusX = 0f
                private var lastFocusY = 0f

                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    lastFocusX = detector.focusX
                    lastFocusY = detector.focusY
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor
                    val focusX = detector.focusX
                    val focusY = detector.focusY

                    imageView.pivotX = focusX
                    imageView.pivotY = focusY

                    val newScaleX = imageView.scaleX * scaleFactor
                    val newScaleY = imageView.scaleY * scaleFactor

                    imageView.scaleX = min(max(0.8f, newScaleX), 7.0f)
                    imageView.scaleY = min(max(0.8f, newScaleY), 7.0f)

                    lastFocusX = focusX
                    lastFocusY = focusY

                    return true
                }
            }
        )

        gestureDetector = GestureDetectorCompat(requireContext(), DoubleTapGestureListener())
        imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private inner class DoubleTapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val currentScale = imageView.scaleX
            val targetScale = if (currentScale == 1.0f) 2.5f else 1.0f

            val focusX = e.x
            val focusY = e.y

            imageView.animate()
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setDuration(200)
                .setUpdateListener { animation ->
                    val animatedFraction = animation.animatedFraction
                    val scaleFactor = currentScale + (targetScale - currentScale) * animatedFraction

                    val pivotX = focusX - imageView.left
                    val pivotY = focusY - imageView.top

                    imageView.pivotX = pivotX
                    imageView.pivotY = pivotY

                    imageView.scaleX = scaleFactor
                    imageView.scaleY = scaleFactor
                }
                .start()

            scaleFactor = targetScale
            return true
        }
    }

    fun onPalette(palette: Palette?) {
        if (null != palette) {
            val parent = imageView.parent.parent as ViewGroup
//            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY))
            val color = ContextCompat.getColor(imageView.context, R.color.white)
            parent.setBackgroundColor(color)
        }
    }

}