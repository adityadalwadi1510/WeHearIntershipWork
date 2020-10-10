package com.example.wehearintershipwork.ui.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.wehearintershipwork.R
import com.example.wehearintershipwork.databinding.FragmentPhoneBinding
import com.example.wehearintershipwork.util.Status
import com.example.wehearintershipwork.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PhoneFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPhoneBinding? = null
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        setUpPhoneVerificationObserver()

        _binding?.btnGetOtp?.setOnClickListener {
            requireActivity().hideKeyboard()
            clearEditTextFocus()
            validateInputFields()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveStateInstance(outState)
    }
    // remove the focus from editText (cursor)
    private fun clearEditTextFocus() {
        _binding?.phone?.clearFocus()
    }

    private fun showProgressBar(isVisible: Boolean) {
        _binding?.progressBarLayout?.progressBar?.isVisible = isVisible
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun setUpPhoneVerificationObserver(){
        viewModel.phoneVerification.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS->{
                    // phone number is automatically verified
                    // sign in the user
                    showProgressBar(false)
                    navigateToOtp(it.data?.phone!!)
                }
                Status.ERROR->{
                    showProgressBar(false)
                    if (it.message == RegisterViewModel.OTP_SENT_MSG) {
                        navigateToOtp(it.data?.phone!!)
                    } else {
                        showToast(it.message!!)
                    }
                }
                Status.LOADING -> {
                    showProgressBar(true)
                }
            }
        })
    }
    private fun validateInputFields() {
        val phone=_binding?.phone?.text.toString().trim()

        if (phone.isEmpty()) {
            _binding?.phone?.error = getString(R.string.wh_phone_empty_error)
            _binding?.phone?.requestFocus()
            return
        }
        if (phone.length != 10) {
            _binding?.phone?.error = getString(R.string.wh_phone_length_error)
            _binding?.phone?.requestFocus()
            return
        }
        if (!phone.matches("[1-9][0-9]{9}".toRegex())) {
            _binding?.phone?.error = getString(R.string.wh_phone_invalid_error)
            _binding?.phone?.requestFocus()
            return
        }
        sendVerificationCode(phone)

    }
    private fun sendVerificationCode(phone: String) {
        showProgressBar(true)
        viewModel.shouldNavigateToOtp = true

        viewModel.sendVerificationCode(phone, false)
    }
    private fun navigateToOtp(phone: String) {
        if (viewModel.shouldNavigateToOtp) {
            viewModel.shouldNavigateToOtp = false

            val action =
                 PhoneFragmentDirections.actionPhoneFragmentToOtpFragment(
                    phone
                )
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}