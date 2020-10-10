package com.example.wehearintershipwork.ui.auth.register

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wehearintershipwork.R
import com.example.wehearintershipwork.adapter.ContactListAdapter
import com.example.wehearintershipwork.databinding.FragmentHomeBinding
import com.example.wehearintershipwork.model.ContactUser
import com.example.wehearintershipwork.util.arePermissionsGranted
import com.example.wehearintershipwork.util.requestPermissionsCompat

class HomeFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private var _binding: FragmentHomeBinding? = null
    private lateinit var navController: NavController
    val userContact: ArrayList<ContactUser> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    companion object {
        private const val REQUEST_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initRecyclerView()
        setHasOptionsMenu(true)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                navController.navigate(action)
            }
        }
        return true
    }

    private fun initRecyclerView() {
        userContact.clear()
        _binding?.recyclerContactList?.layoutManager = LinearLayoutManager(context)
        getContactReadPermission()

    }

    private fun getContactReadPermission() {
        val neceesaryPermission = arrayOf(
            Manifest.permission.READ_CONTACTS
        )
        if (arePermissionsGranted(neceesaryPermission, requireContext()))
            getContacts()
        else {
            requestPermissions(neceesaryPermission, REQUEST_PERMISSION)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContacts()
        }


    }

    private fun getContacts() {

        val resolver: ContentResolver = requireContext().contentResolver
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = requireContext().contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            userContact.add(ContactUser(1, name, phoneNumValue))
                            print("Contact" + phoneNumValue);
                        }
                    }
                    cursorPhone.close()
                }
            }
        } else {
            print("No Contact")
        }
        cursor.close()
        userContact.sortBy {
            it.name
        }
        showProgressBar(false)
        val adapter = ContactListAdapter(userContact)
        _binding?.recyclerContactList?.adapter = adapter
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    private fun showProgressBar(isVisible: Boolean) {
        _binding?.progressBarLayout?.progressBar?.isVisible = isVisible
    }
}