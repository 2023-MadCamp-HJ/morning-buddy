package com.example.madcampproj1.tab1

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.example.madcampproj1.R
import com.example.madcampproj1.databinding.ContactItemBinding
import com.example.madcampproj1.databinding.FragmentTab1Binding
import com.example.madcampproj1.databinding.LineItemBinding
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.Locale

class tab1Fragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentTab1Binding? = null
    private val binding get() = _binding!!
    //   private val CONTACTS_PERMISSION_REQUEST = 1

    private val contactsList: MutableList<Contact> = mutableListOf()
    private val temp: MutableList<Contact> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentTab1Binding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requestPermissionsLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                // 사용자가 모든 권한을 수락했는지 확인합니다.
                val allPermissionsGranted = permissions.all { it.value }
                if (allPermissionsGranted) {

                    loadContacts()
                    fetchContacts()
                } else {
                    // 권한이 거부된 경우 다른 처리를 수행할 수 있습니다.
                    // 예를 들어, 사용자에게 권한 필요성에 대해 알리는 메시지를 보여주는 등
                }
            }

            // 연락처 권한이 없는 경우 권한을 요청합니다.
            val readContactsGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val writeContactsGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            if (!readContactsGranted || !writeContactsGranted) {
                requestPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
                    )
                )
            } else {
                // 연락처 권한이 이미 있는 경우 바로 연락처를 불러옵니다.
                loadContacts()
                fetchContacts()
            }
        }
        binding.refreshButton.setOnClickListener {
            val intent = Intent(activity, Tab1AddActivity::class.java)
            startActivity(intent)
        }
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 호출됩니다.
                // 이 메소드에서는 변경되기 전의 텍스트를 확인할 수 있습니다.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안에 호출됩니다.
                // 이 메소드에서는 텍스트가 어떻게 변경되고 있는지 확인할 수 있습니다.
                loadContacts(s.toString())
                (binding.recyclerView.adapter as? ContactAdapter)?.refresh()
            }

            override fun afterTextChanged(s: Editable) {
                // 텍스트가 변경된 후에 호출됩니다.
                // 이 메소드에서는 최종적으로 어떤 텍스트가 입력되었는지 확인할 수 있습니다.
            }
        })


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
            (binding.recyclerView.adapter as? ContactAdapter)?.refresh()
        }
    }

    @SuppressLint("Range")
    fun loadContacts(stringFilter: String = "") {
        //  val contactsList = ArrayList<String>()

        contactsList.clear()
        val cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id =
                    it.getLong(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(Contact(id, name, number))
            }
        }

        cursor?.close()

        contactsList.sortBy { contact -> contact.name }


        val filteredList = mutableListOf<Contact>()
        if (stringFilter == "") {
            filteredList.addAll(contactsList)
        } else {
            val lowerCaseFilter = stringFilter.toLowerCase(Locale.getDefault())
            for (contact in contactsList) {
                val lowerCaseName = contact.name.toLowerCase(Locale.getDefault())
                val chosungName = getChosung(lowerCaseName)
                if (lowerCaseName.contains(lowerCaseFilter) || chosungName.contains(lowerCaseFilter) || contact.phoneNumber.contains(
                        lowerCaseFilter
                    )
                ) {
                    filteredList.add(contact)
                }
            }
        }

        contactsList.clear()  // contactsList의 모든 요소를 제거
        contactsList.addAll(filteredList)  //
        temp.clear()

        //contactsList.add(Contact(-1, "2", "3"))

        for (i in 0 until contactsList.size) {
            val currentContact = contactsList[i]
            val currentFirstChar = getChosung(currentContact.name)[0]
            if (i == 0) {
                temp.add(Contact(-1, currentFirstChar.toString(), "3"))

                temp.add(currentContact)
                continue
            }
            val previousContact = contactsList[i - 1]

            val previousFirstChar = getChosung(previousContact.name)[0]
            if (currentFirstChar != previousFirstChar) {

                temp.add(Contact(-1, currentFirstChar.toString(), "3"))

            } else {
            }
            temp.add(currentContact)
        }
        contactsList.clear()
        for (i in 0 until temp.size) {
            contactsList.add(temp[i])
        }
    }

    fun getChosung(value: String): String {
        val chosung = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
        val result = StringBuilder()
        for (i in value.indices) {
            val code = value[i].toInt() - 44032
            if (code in 0..11171) {
                result.append(chosung[code / 588]) // 초성 추출
            } else {
                result.append(value[i]) // 한글이 아닌 경우 그대로 출력
            }
        }
        return result.toString()
    }


    fun fetchContacts() {

        //  val contactList = mutableListOf<Contact>()
        val adapter = ContactAdapter(contactsList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        // recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}

fun Char.isHangul(): Boolean {
    val unicodeValue = this.toInt()
    return unicodeValue in 0xAC00..0xD7A3
}

fun extractInitialSound(str: String): String {
    val initialSound = StringBuilder()

    for (char in str) {
        if (char.isLetter() && char.isHangul()) {
            val unicodeValue = char.toInt() - 0xAC00
            val initialIndex = unicodeValue / (21 * 28)
            val initialUnicode = initialIndex + 0x1100
            initialSound.append(initialUnicode.toChar())
        }
    }

    return initialSound.toString()
}

@Parcelize
data class Contact(
    val id: Long, val name: String, val phoneNumber: String
) : Parcelable

class ContactAdapter(private val contactList: List<Contact>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun refresh() {

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        if (contactList[position].id == -1L) {
            return 0
        } else {
            return 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //  val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)

        //contact_item
        if (viewType == 0) {
            val binding =
                LineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LineViewHolder(binding)
        } else {
            val binding =
                ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ContactViewHolder(binding).also { holder ->
                binding.contactBox.setOnClickListener {
                    val position = holder.adapterPosition
                    val intent: Intent = Intent(parent.context, Tab1EditActivity::class.java)
                    intent.putExtra("contactInfo", contactList[position])
                    intent.putExtra("test", "AAA")
                    startActivity(parent.context, intent, null)

                }
            }
        }
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //   holder.bind(contactList[position])
        when (holder) {
            is ContactViewHolder -> holder.bind(contactList[position])
            is LineViewHolder -> holder.bind(contactList[position])
        }
    }

    class ContactViewHolder(val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.name.text = contact.name
            binding.phoneNumber.text = contact.phoneNumber
        }
    }

    class LineViewHolder(val binding: LineItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.indicateText.text = contact.name
        }
    }
}
