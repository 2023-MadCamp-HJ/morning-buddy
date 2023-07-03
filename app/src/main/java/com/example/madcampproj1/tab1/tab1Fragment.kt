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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.example.madcampproj1.R
import com.example.madcampproj1.databinding.ContactItemBinding
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
 //   private val CONTACTS_PERMISSION_REQUEST = 1

    private val contactsList: MutableList<Contact> = mutableListOf()

    @SuppressLint("Range")
    fun loadContacts() {
        //  val contactsList = ArrayList<String>()

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

//            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, contactsList)
//            listView.adapter = adapter
    }

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
        val view = inflater.inflate(R.layout.fragment_tab1, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val requestPermissionLauncher = registerForActivityResult(
//                ActivityResultContracts.RequestPermission()
//            ) { isGranted ->
//                if (isGranted) {
//                    println("BBBBB")
//                    loadContacts()
//                    fetchContacts()
//                } else {
//                    println("CCCCC")
//                    // 권한이 거부된 경우 다른 처리를 수행할 수 있습니다.
//                    // 예를 들어, 사용자에게 권한 필요성에 대해 알리는 메시지를 보여주는 등
//                }
//            }
//
//            // 연락처 권한이 없는 경우 권한을 요청합니다.
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.READ_CONTACTS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                println("DDDDD")
//                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
//            } else {
//                // 연락처 권한이 이미 있는 경우 바로 연락처를 불러옵니다.
//                println("EEEEEE")
//                loadContacts()
//                fetchContacts()
//            }
//        }
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


        return view
    }

    fun fetchContacts() {

        val contactList = mutableListOf<Contact>()
        val adapter = ContactAdapter(contactsList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        // recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    }

}



@Parcelize
data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String
): Parcelable

class ContactAdapter(private val contactList: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        //  val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)

        //contact_item
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding).also { holder ->
            binding.contactBox.setOnClickListener {
                val position=holder.adapterPosition
                println(contactList[position].id)
                println(contactList[position].name)
                println(contactList[position].phoneNumber)
                val intent:Intent = Intent(parent.context,Tab1EditActivity::class.java)
                intent.putExtra("contactInfo",contactList[position])
                intent.putExtra("test","AAA")
                startActivity(parent.context, intent, null)

            }
        }
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    class ContactViewHolder(val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.name.text = contact.name
            binding.phoneNumber.text = contact.phoneNumber
        }
    }
}
