package com.example.madcampproj1.tab1

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.content.ContentResolverCompat
import com.example.madcampproj1.R
import com.example.madcampproj1.databinding.ActivityTab1AddBinding
import com.example.madcampproj1.databinding.ActivityTab1EditBinding

class Tab1AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTab1AddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTab1AddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.tab1SaveButton.setOnClickListener {
            var name = binding.tab1NewName.text.toString()
            var phone = binding.tab1NewPhone.text.toString()
            addContact(this, name, phone)
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tab1CancleButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    fun addContact(context: Context, name: String, phoneNumber: String) {
        val contentResolver: ContentResolver = context.contentResolver

        val rawContactValues = ContentValues().apply {
            putNull(ContactsContract.RawContacts.ACCOUNT_TYPE)
            putNull(ContactsContract.RawContacts.ACCOUNT_NAME)
        }

        val rawContactUri =
            contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, rawContactValues)
        val rawContactId = rawContactUri?.lastPathSegment?.toLongOrNull()

        val contactValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
        }

        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
            put(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
        }

        contentResolver.insert(ContactsContract.Data.CONTENT_URI, contactValues)
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
    }


}