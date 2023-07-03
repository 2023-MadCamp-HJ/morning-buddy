package com.example.madcampproj1

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
import java.io.IOException

class tab2Fragment : Fragment() {
    private val REQUEST_CODE = 1

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPager: ViewPager2
    private lateinit var imageGalleryAdapter: ImageGalleryAdapter
    private lateinit var panoramaRecyclerView: RecyclerView
    private lateinit var panoramaImageGalleryAdapter: PanoramaImageGalleryAdapter

    private var isGalleryViewVisible = true
    private var isPanoramaViewVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab2, container, false)

        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView = view.findViewById(R.id.rv_images)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        panoramaRecyclerView = view.findViewById(R.id.rv_panorama_images)
        panoramaRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewPager = view.findViewById(R.id.view_pager)
        viewPager.visibility = View.GONE

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val images = getImagesFromGallery()
            imageGalleryAdapter = ImageGalleryAdapter(requireContext(), images)
            recyclerView.adapter = imageGalleryAdapter
            // 최신 이미지로 스크롤 이동
            val lastPosition = images.size - 1
            recyclerView.scrollToPosition(lastPosition)

            panoramaImageGalleryAdapter = PanoramaImageGalleryAdapter(requireContext(), images)
            panoramaRecyclerView.adapter = panoramaImageGalleryAdapter
        } else {
            imageGalleryAdapter = ImageGalleryAdapter(requireContext(), emptyArray())
            recyclerView.adapter = imageGalleryAdapter
            panoramaImageGalleryAdapter = PanoramaImageGalleryAdapter(requireContext(), emptyArray())
            panoramaRecyclerView.adapter = panoramaImageGalleryAdapter
        }

        val toggleButton = view.findViewById<Button>(R.id.toggle_button)

        toggleButton.setOnClickListener {
            if (isPanoramaViewVisible) {
                // Switch to gallery view
                recyclerView.visibility = View.VISIBLE
                panoramaRecyclerView.visibility = View.GONE
                isPanoramaViewVisible = false
                toggleButton.text = "Panorama"
            } else if (!isPanoramaViewVisible && isGalleryViewVisible){
                // Switch to panorama view
                recyclerView.visibility = View.GONE
                panoramaRecyclerView.visibility = View.VISIBLE
                isPanoramaViewVisible = true
                toggleButton.text = "Gallery"
            }
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isGalleryViewVisible) {
                    recyclerView.visibility = View.VISIBLE
                    viewPager.visibility = View.GONE
                    isGalleryViewVisible = true
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        return view
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용됨
                val images = getImagesFromGallery()
                imageGalleryAdapter.updateImages(images)
                panoramaImageGalleryAdapter.updateImages(images)
            } else {
                // 권한이 거부됨
                Toast.makeText(requireContext(), "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getImagesFromGallery(): Array<Uri> {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED}"
        requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(contentUri)
            }
        }
        return images.toTypedArray()
    }

    private inner class ImageGalleryAdapter(val context: Context, var images: Array<Uri>)
        : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ImageGalleryAdapter.MyViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val photoView = inflater.inflate(R.layout.item_image, parent, false)

            val width = recyclerView.width / 3
            photoView.layoutParams.height = width

            return MyViewHolder(photoView)
        }

        override fun onBindViewHolder(holder: ImageGalleryAdapter.MyViewHolder, position: Int) {
            val uri = images[position]
            val imageView = holder.photoImageView

            val rotation = try {
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
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

            Picasso.get()
                .load(uri)
                .rotate(rotation)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit()
                .tag(context)
                .into(imageView)
        }

        override fun getItemCount(): Int {
            return images.size
        }

        fun updateImages(images: Array<Uri>) {
            this.images = images
            notifyDataSetChanged()
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            var photoImageView: ImageView = itemView.findViewById(R.id.iv_photo)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    recyclerView.visibility = View.GONE
                    viewPager.visibility = View.VISIBLE
                    isGalleryViewVisible = false

                    viewPager.adapter = ScreenSlidePagerAdapter(requireActivity(), images)

                    viewPager.setCurrentItem(position, false)
                }
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity, private val images: Array<Uri>) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = images.size

        override fun createFragment(position: Int): Fragment {
            return ImageFragment.newInstance(images[position])
        }
    }

    private inner class PanoramaImageGalleryAdapter(private val context: Context, private var images: Array<Uri>
    ) : RecyclerView.Adapter<PanoramaImageGalleryAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val photoView = inflater.inflate(R.layout.item_image, parent, false)
            return MyViewHolder(photoView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val uri = images[position]
            val imageView = holder.photoImageView

            val rotation = try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
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

            Picasso.get()
                .load(uri)
                .rotate(rotation)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit()
                .tag(context)
                .into(imageView)
        }

        override fun getItemCount(): Int {
            return images.size
        }

        fun updateImages(images: Array<Uri>) {
            this.images = images
            notifyDataSetChanged()
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            var photoImageView: ImageView = itemView.findViewById(R.id.iv_photo)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                // Add code here to switch to full-size image view when an image is tapped
            }
        }
    }
}


