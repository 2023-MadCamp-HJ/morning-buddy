package com.example.madcampproj1.tab1

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
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
    val where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
    val nameParams = arrayOf(phoneContactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    ops.add(
        ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
            .withSelection(where, nameParams)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
            .build()
    )

    // 연락처 번호 변경
    val numberParams = arrayOf(phoneContactID, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
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