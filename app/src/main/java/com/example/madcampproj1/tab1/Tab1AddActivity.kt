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
import kotlin.random.Random

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
        binding.tab1DummyButton.setOnClickListener {
            binding.tab1NewName.setText(generateRandomName())
            binding.tab1NewPhone.setText(generateRandomPhoneNumber())
        }
    }

    fun generateRandomName(): String {
        val firstNames = listOf(
            "김",
            "이",
            "박",
            "최",
            "정",
            "강",
            "조",
            "윤",
            "장",
            "임",
        )
        val lastNames = listOf(
            "지훈",
            "서연",
            "민준",
            "하은",
            "지우",
            "도윤",
            "하린",
            "유준",
            "시아",
            "준우",
            "유진",
            "은우",
            "수아",
            "지민",
            "지유",
            "예진",
            "지현",
            "예준",
            "하율",
            "서준",
            "유나",
            "시우",
            "하윤",
            "지아",
            "준서",
            "시현",
            "서아",
            "승준",
            "예서",
            "민서",
            "하연",
            "지원",
            "하준",
            "도현",
            "소율",
            "지율",
            "예은",
            "윤서",
            "주원",
            "수민",
            "서율",
            "은서",
            "지안",
            "윤우",
            "시윤",
            "은유",
            "유빈",
            "연우",
            "민재",
            "하림",
            // 추가된 이름들
            "유림",
            "세준",
            "민아",
            "태윤",
            "수현",
            "예린",
            "은채",
            "태현",
            "승희",
            "수진",
            "혜원",
            "주현",
            "재희",
            "소연",
            "우진",
            "현주",
            "예담",
            "주하",
            "서진",
            "지한",
            "윤재",
            "민희",
            "은비",
            "수빈",
            "진아",
            "하영",
            "성훈",
            "하경",
            "영서",
            "재윤",
            "재원",
            "윤지",
            "하진",
            "은재",
            "소민",
            "재민",
            "유리",
            "영민",
            "지성",
            "지환",
            "주은",
            "도은",
            "세아",
            "우리",
            "예지",
            "서우",
            "승민",
            "지수"
        )


        val firstName = firstNames.random()
        val lastName = lastNames.random()

        return "$firstName$lastName"
    }

    fun generateRandomPhoneNumber(): String {
        val number1 = Random.nextInt(0, 10000).toString().padStart(4, '0')
        val number2 = Random.nextInt(0, 10000).toString().padStart(4, '0')

        return "010-$number1-$number2"
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