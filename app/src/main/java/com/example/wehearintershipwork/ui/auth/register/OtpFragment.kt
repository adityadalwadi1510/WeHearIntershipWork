package com.example.wehearintershipwork.ui.auth.register

import android.os.Bundle
import android.os.Handler
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
import androidx.navigation.fragment.navArgs
import com.example.wehearintershipwork.R
import com.example.wehearintershipwork.databinding.FragmentOtpBinding
import com.example.wehearintershipwork.util.Status
import com.example.wehearintershipwork.util.hideKeyboard
import com.example.wehearintershipwork.util.toEditable
import com.google.firebase.auth.PhoneAuthCredential
import java.util.concurrent.TimeUnit


class OtpFragment : Fragment() {
    companion object {
        private const val RESEND_WAIT_SECONDS = 30000L
        private const val TICK_INTERVAL_SECONDS = 1000L
    }
    private lateinit var navController: NavController
    private var _binding: FragmentOtpBinding? = null
    private val viewModel: RegisterViewModel by activityViewModels()
    private val args: OtpFragmentArgs by navArgs()

    private lateinit var phone: String

    private val looper = Handler()
    private val countDown: Runnable = Runnable {
        processCountdownTicker()
    }
    private var millisUntilFinished: Long = RESEND_WAIT_SECONDS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phone = args.phone
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        showProgressBar(true)
        processCountdownTicker()
        setUpOtpSentTextView()
        setUpOtpEditText()
        setUpResendOtpTextView()
        setUpPhoneVerificationObserver()

        _binding?.btnVerify?.setOnClickListener {
            submitCode(phone, _binding?.otp?.text.toString())
        }
    }
    private fun showProgressBar(isVisible: Boolean) {
        _binding?.progressBarLayout?.progressBar?.isVisible = isVisible
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun setUpOtpSentTextView() {
        _binding?.otpSentMessage?.text = (getString(R.string.wh_otp_sent_message) + phone)
    }

    private fun setUpOtpEditText() {
        _binding?.otp?.setOtpCompletionListener {
            requireActivity().hideKeyboard()
        }
    }

    private fun setUpResendOtpTextView() {
        _binding?.resendOtp?.setOnClickListener {
            viewModel.sendVerificationCode(phone, true)

            _binding?.resendOtp?.visibility = View.GONE
            _binding?.ticker?.visibility = View.VISIBLE
            _binding?.ticker?.text = String.format(
                getString(R.string.wh_resend_otp_in),
                RESEND_WAIT_SECONDS / 1000
            )
            millisUntilFinished = RESEND_WAIT_SECONDS
            looper.postDelayed(countDown, TICK_INTERVAL_SECONDS)
        }
    }

    private fun setUpPhoneVerificationObserver() {
        viewModel.phoneVerification.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    // phone number is verified
                    // sign in the user
                    showProgressBar(false)

                    it.data?.let { phoneVerification ->
                        if (phoneVerification.isAutoVerified) {
                            val code = phoneVerification.credential?.smsCode
                            _binding?.otp?.text = code?.toEditable()
                            _binding?.btnVerify?.isEnabled = false
                        }
                        signIn(phoneVerification.credential!!)
                    }
                }
                Status.ERROR -> {
                    showProgressBar(false)
                    if (it.message == RegisterViewModel.OTP_SENT_MSG) {
                        showToast(RegisterViewModel.OTP_SENT_MSG)
                    } else {
                        showToast(it.message!!)
                    }
                }
                Status.LOADING -> {
                }
            }
        })
    }
    private fun processCountdownTicker() {
        millisUntilFinished -= TICK_INTERVAL_SECONDS
        if (millisUntilFinished <= 0) {
            _binding?.ticker?.text = ""
            _binding?.ticker?.visibility = View.GONE
            _binding?.resendOtp?.visibility = View.VISIBLE
        } else {
            _binding?.ticker?.text = String.format(
                getString(R.string.wh_resend_otp_in),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
            )
            looper.postDelayed(countDown, TICK_INTERVAL_SECONDS)
        }
    }
    private fun submitCode(phone: String, code: String) {
        showProgressBar(true)
        viewModel.submitVerificationCode(phone, code)
    }

    private fun signIn(credential: PhoneAuthCredential) {
        viewModel.signInWithPhoneAuthCredential(credential).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    showProgressBar(true)
                }
                Status.SUCCESS -> {
                    showProgressBar(false)
                    navigateToPassword()
                }
                Status.ERROR -> {
                    showProgressBar(false)
                    showToast(it.message!!)
                }
            }
        })
    }

    private fun navigateToPassword() {
        val action =
            OtpFragmentDirections.actionOtpFragmentToUserDetailsFragment(
                phone
            )
        navController.navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}