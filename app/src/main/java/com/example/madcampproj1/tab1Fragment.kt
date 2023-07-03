package com.example.madcampproj1

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.Manifest
import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import android.widget.ListView


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
    private val CONTACTS_PERMISSION_REQUEST = 1

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
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(Contact(name,number))
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST
            )

        } else {
            // 연락처 권한이 있는 경우 연락처를 가져옵니다.
            loadContacts()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tab1, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        fetchContacts()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment tab1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            tab1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun fetchContacts() {


        val contactList = mutableListOf<Contact>()
//        println(contactsList);
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))
//        contactList.add(Contact("김현수","010-222002"))

        val adapter = ContactAdapter(contactsList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
       // recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)



    }

}
data class Contact(
    val name: String,
    val phoneNumber: String
)
class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name = view.findViewById<TextView>(R.id.name)
    val phoneNumber = view.findViewById<TextView>(R.id.phoneNumber)
}

class ContactAdapter(private val contactList: List<Contact>) : RecyclerView.Adapter<ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.name.text = contactList[position].name
        holder.phoneNumber.text = contactList[position].phoneNumber
    }
}
