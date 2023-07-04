package com.example.madcampproj1.tab1

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.ContentProviderResult
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.OperationApplicationException
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.util.Log
import com.example.madcampproj1.R
import com.example.madcampproj1.databinding.ActivityMainBinding
import com.example.madcampproj1.databinding.ActivityTab1EditBinding

class Tab1EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTab1EditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTab1EditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var intent = getIntent()
        var test = intent.getStringExtra("test")
        var contact = intent.getParcelableExtra<Contact>("contactInfo")

        //binding.textView.text="gdg"
        binding.newName.setText(contact!!.name)
        binding.newPhoneNumber.setText(contact!!.phoneNumber)



        binding.saveButton.setOnClickListener {
            // getPhoneNumberFromContactId(this,contact!!.id)
            updateContactInfo(
                this,
                contact!!.id,
                binding.newName.text.toString(),
                binding.newPhoneNumber.text.toString()
            )

            onBackPressedDispatcher.onBackPressed()
        }
        binding.cancelButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.deleteButton.setOnClickListener {
            deleteContact(this, contact!!.id)
            onBackPressedDispatcher.onBackPressed()

        }
        binding.callButton.setOnClickListener {
            val phoneNumber = contact.phoneNumber // 원하는 전화번호를 입력하세요
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            if (intent.resolveActivity(this.packageManager) != null) {
                this.startActivity(intent)
            }

        }
        binding.messageButton.setOnClickListener {
            val phoneNumber = contact.phoneNumber // 원하는 전화번호를 입력하세요
            val smsBody = "" // 보낼 메시지를 입력하세요
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:$phoneNumber")
                putExtra("sms_body", smsBody)
            }
            if (intent.resolveActivity(this.packageManager) != null) {
                this.startActivity(intent)
            }

        }
    }

    @SuppressLint("Range")
    fun getPhoneNumberFromContactId(context: Context, contactId: Long): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = (
                "${ContactsContract.Data.CONTACT_ID} = ? AND " +
                        "${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'"
                )
        val selectionArgs = arrayOf(contactId.toString())

        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        var phoneNumber: String? = null

        cursor?.use {
            if (it.moveToFirst()) {
                phoneNumber =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }

        if (phoneNumber == null) {
            Log.w("ContactUtils", "No phone number found for contact id $contactId")
        }
        println(phoneNumber)
        return phoneNumber
    }

    fun updateContactInfo(
        context: Context,
        contactId: Long,
        newName: String,
        newPhoneNumber: String
    ) {
        val contentResolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
        }
        val nameUpdateSelection = (
                "${ContactsContract.Data.CONTACT_ID} = ? AND " +
                        "${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}'"
                )
        val nameUpdateSelectionArgs = arrayOf(contactId.toString())

        val nameUpdateResult = contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            contentValues.apply {
                put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
            },
            nameUpdateSelection,
            nameUpdateSelectionArgs
        )

        val phoneNumberUpdateSelection = (
                "${ContactsContract.Data.CONTACT_ID} = ? AND " +
                        "${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'"
                )
        val phoneNumberUpdateSelectionArgs = arrayOf(contactId.toString())

        val phoneNumberUpdateResult = contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            contentValues.apply {
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
            },
            phoneNumberUpdateSelection,
            phoneNumberUpdateSelectionArgs
        )

        val updatedRows = nameUpdateResult + phoneNumberUpdateResult

        if (updatedRows > 0) {
            Log.d("ContactUtils", "Contact information updated for contact id $contactId")
        } else {
            Log.w("ContactUtils", "Failed to update contact information for contact id $contactId")
        }
    }

    fun deleteContact(context: Context, contactId: Long) {
        val contentResolver: ContentResolver = context.contentResolver

        // RawContacts._ID로 삭제
        val deleteSelection = "${ContactsContract.RawContacts.CONTACT_ID} = ?"
        val deleteSelectionArgs = arrayOf(contactId.toString())

        val deleteResult = contentResolver.delete(
            ContactsContract.RawContacts.CONTENT_URI,
            deleteSelection,
            deleteSelectionArgs
        )

        if (deleteResult > 0) {
            Log.d("ContactUtils", "Contact deleted for contact id $contactId")
        } else {
            Log.w("ContactUtils", "Failed to delete contact for contact id $contactId")
        }
    }

}
