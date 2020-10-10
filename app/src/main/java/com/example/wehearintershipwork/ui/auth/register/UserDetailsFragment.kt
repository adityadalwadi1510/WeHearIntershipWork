package com.example.wehearintershipwork.ui.auth.register

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.wehearintershipwork.R
import com.example.wehearintershipwork.databinding.FragmentUserDetailsBinding
import com.example.wehearintershipwork.model.User
import com.example.wehearintershipwork.ui.auth.AuthActivity
import com.example.wehearintershipwork.util.Status
import com.example.wehearintershipwork.util.arePermissionsGranted
import com.example.wehearintershipwork.util.hideKeyboard
import com.example.wehearintershipwork.util.requestPermissionsCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_phone.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserDetailsFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentUserDetailsBinding? = null
    private val viewModel: RegisterViewModel by activityViewModels()
    private lateinit var easyImage: EasyImage
    private val args: OtpFragmentArgs by navArgs()
    private lateinit var phone: String
    companion object {
        private const val REQUEST_PERMISSON = 1
        private const val CHOOSER_PERMISSIONS_REQUEST_CODE = 7459
        private const val CAMERA_REQUEST_CODE = 7500
        private const val GALLERY_REQUEST_CODE = 7502
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phone = args.phone
        viewModel.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveStateInstance(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setDateToTextView()
        updateProfileInImage()
        easyImage = EasyImage.Builder(requireContext())
            .setChooserTitle("Select Image")
            .allowMultiple(false)
            .setCopyImagesToPublicGalleryFolder(false)
            .build()
        _binding?.dateOfBirth?.setOnClickListener {
            onBirthdateSelectObserver()
        }

        _binding?.selectImage?.setOnClickListener {
            openGallery()
        }

        _binding?.btnSubmit?.setOnClickListener {
            requireActivity().hideKeyboard()
            clearEditTextFocus()
            validateInputFields()
        }
    }


    private fun clearEditTextFocus() {
        _binding?.name?.clearFocus()
    }

    private fun validateInputFields(){
        val dateOfBirth=viewModel.getDateString()
        val profilePicture=viewModel.getProfileImagePath()
        val name=_binding?.name?.text.toString().trim()
        if(name.isEmpty()){
            _binding?.name?.error = getString(R.string.wh_name_empty_error)
            _binding?.name?.requestFocus()
            return
        }else if(dateOfBirth.equals("")){
            _binding?.dateOfBirth?.error = getString(R.string.wh_name_empty_error)
            _binding?.dateOfBirth?.requestFocus()
            return
        }else if (profilePicture.equals("")){
            shoToast("Please Select Profile Picture")
            return
        }
        viewModel.user= User(name,"+91$phone","/profilePicture/+91$phone",dateOfBirth)
        onUploadImageObserver()
    }

    private fun onUploadImageObserver(){
        viewModel.uploadImage("+91$phone").observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it.status){
                Status.SUCCESS->{
                    showProgressBar(false)
                    viewModel.user?.profilePic=it.message.toString()
                    navigateToMainActivity()
                }
                Status.ERROR->{
                    showProgressBar(false)
                    shoToast(it.message!!)
                }
                Status.LOADING -> {
                    showProgressBar(true)
                }
            }
        })
    }

    private fun navigateToMainActivity() {
        val action =
            UserDetailsFragmentDirections.actionUserDetailsFragmentToMainActivity()
        navController.navigate(action)
    }

    private fun onBirthdateSelectObserver() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DAY_OF_MONTH)


        val datePickerdialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, date ->
                viewModel.setDateString(date.toString() + "-" + (monthOfYear + 1).toString() + "-" + year.toString())
                setDateToTextView()
            }, year, month, date
        )
        datePickerdialog.show()
    }


    fun setDateToTextView() {
        if (!viewModel.getDateString().equals(""))
            _binding?.dateOfBirth?.text = "Date Of birth :${viewModel.getDateString()}"
    }

    fun openGallery() {
        val neceesaryPermission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (arePermissionsGranted(neceesaryPermission, requireContext()))
            easyImage.openChooser(this)
        else {
            requestPermissionsCompat(
                neceesaryPermission,
                REQUEST_PERMISSON,
                requireActivity()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            requireActivity(),
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    val imagePath = imageFiles[0].file.path
                    viewModel.setProfileImagePath(imagePath)
                    updateProfileInImage()
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    super.onImagePickerError(error, source)
                }

                override fun onCanceled(source: MediaSource) {
                    super.onCanceled(source)
                }
            })
    }

    fun updateProfileInImage() {
        if (!viewModel.getProfileImagePath().equals(""))
            _binding?.imgProfile?.setImageURI(Uri.parse(viewModel.getProfileImagePath()))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CHOOSER_PERMISSIONS_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openChooser(this)
        } else if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCameraForImage(this)
        } else if (requestCode == GALLERY_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCameraForImage(this)
        }
    }
    private fun shoToast(msg:String){
        Toast.makeText(requireContext(),msg,Toast.LENGTH_SHORT).show()
    }
    private fun showProgressBar(isVisible: Boolean) {
        _binding?.progressBarLayout?.progressBar?.isVisible = isVisible
    }
}