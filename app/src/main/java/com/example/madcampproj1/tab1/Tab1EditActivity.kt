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

        //binding.textView.text="gdg"
        binding.buttonTest.setOnClickListener {
            println("Click!")
            var intent = getIntent()
            var test = intent.getStringExtra("test")
            var contact = intent.getParcelableExtra<Contact>("contactInfo")
            // var contact = intent.getSerializableExtra("contactInfo")
            println(contact!!.id)
            println(test.toString())
            // getPhoneNumberFromContactId(this,contact!!.id)
            updatePhoneNumberForContactId(this, contact!!.id, "0101010101")
            // updateContact(this,contact!!.id,"김현수","010")
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

    fun updatePhoneNumberForContactId(context: Context, contactId: Long, newPhoneNumber: String) {
        val contentResolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
        }
        val selection = (
                "${ContactsContract.Data.CONTACT_ID} = ? AND " +
                        "${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'"
                )
        val selectionArgs = arrayOf(contactId.toString())

        val updatedRows = contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            contentValues,
            selection,
            selectionArgs
        )

        if (updatedRows > 0) {
            Log.d("ContactUtils", "Phone number updated for contact id $contactId")
        } else {
            Log.w("ContactUtils", "Failed to update phone number for contact id $contactId")
        }
    }
}

@SuppressLint("Range")
fun updateContact(context: Context, oldName: String, newName: String, newNumber: String) {
    val ops = ArrayList<ContentProviderOperation>()

    // 선택한 연락처 ID 검색
    val selection = ContactsContract.Data.DISPLAY_NAME + "=?"
    val phoneContactID = context.contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        null,
        selection,
        arrayOf(oldName),
        null
    )?.use {
        it.moveToFirst()
        it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
    }

    // 연락처 이름 변경
    val where =
        ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
    val nameParams =
        arrayOf(phoneContactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    ops.add(
        ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
            .withSelection(where, nameParams)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
            .build()
    )

    // 연락처 번호 변경
    val numberParams =
        arrayOf(phoneContactID, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    ops.add(
        ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
            .withSelection(where, numberParams)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
            .build()
    )

    // 연락처 업데이트 적용
    try {
        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
    } catch (e: RemoteException) {
        e.printStackTrace()
    } catch (e: OperationApplicationException) {
        e.printStackTrace()
    }
}