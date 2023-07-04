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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [tab1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class tab1Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentTab1Binding? = null
    private val binding get() = _binding!!
    //   private val CONTACTS_PERMISSION_REQUEST = 1

    private val contactsList: MutableList<Contact> = mutableListOf()
    private val temp: MutableList<Contact> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // 기존 코드의 일부를 재사용하여 권한 요청을 위한 준비를 합니다.


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    println("BBBBB")
                    loadContacts()
                    fetchContacts()
                } else {
                    println("CCCCC")
                    // 권한이 거부된 경우 다른 처리를 수행할 수 있습니다.
                    // 예를 들어, 사용자에게 권한 필요성에 대해 알리는 메시지를 보여주는 등
                }
            }

            // 연락처 권한이 없는 경우 권한을 요청합니다.
            val readContactsGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val writeContactsGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            if (!readContactsGranted || !writeContactsGranted) {
                println("DDDDD")
                requestPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    )
                )
            } else {
                // 연락처 권한이 이미 있는 경우 바로 연락처를 불러옵니다.
                println("EEEEEE")
                loadContacts()
                fetchContacts()
            }
        }
        println("reload")
        binding.refreshButton.setOnClickListener {
//            println("reload")
//            loadContacts()
//            println("reload")
//            (binding.recyclerView.adapter as? ContactAdapter)?.refresh()
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
                println("s: $s, start: $start, before: $before, count: $count")
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
                requireContext(),
                Manifest.permission.READ_CONTACTS
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
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
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
        println(contactsList.toString())
        println(stringFilter)
        contactsList.sortBy { contact -> contact.name }
        val filteredList = contactsList.filter { contact ->
            stringFilter == "" || contact.name.contains(stringFilter) || contact.phoneNumber.contains(
                stringFilter
            )
        }

        contactsList.clear()  // contactsList의 모든 요소를 제거
        contactsList.addAll(filteredList)  //
        println(contactsList.toString())
        temp.clear()

        //contactsList.add(Contact(-1, "2", "3"))
        if (!contactsList.isEmpty()) {
            temp.add(Contact(-1, contactsList[0].name.first().toString(), "3"))
            temp.add(contactsList[0])
        }
        for (i in 1 until contactsList.size) {
//            val currentContact = contactsList[i]
//            val previousContact = contactsList[i - 1]
//            if (extractInitialSound( previousContact.name).first().toString() != extractInitialSound(currentContact.name).first().toString()) {
//
//                temp.add(Contact(-1, extractInitialSound(currentContact.name).first().toString(), "3"))
//
//            } else {
//                print("AAA")
//            }

            val currentContact = contactsList[i]
            val previousContact = contactsList[i - 1]
            if (previousContact.name.first() != currentContact.name.first()) {

                temp.add(Contact(-1, currentContact.name.first().toString(), "3"))

            } else {
                print("AAA")
            }
            temp.add(currentContact)
        }
        contactsList.clear()
        for (i in 0 until temp.size) {
            contactsList.add(temp[i])
        }
        println(contactsList.toString())
//
//        contactsList = temp.toMutableList()
//        contactsList.clear()
//        print(contactsList.toString())
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
    val id: Long,
    val name: String,
    val phoneNumber: String
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

    class LineViewHolder(val binding: LineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.indicateText.text = contact.name
        }
    }
}
