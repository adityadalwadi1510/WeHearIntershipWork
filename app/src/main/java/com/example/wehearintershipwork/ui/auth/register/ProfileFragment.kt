package com.example.wehearintershipwork.ui.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.example.wehearintershipwork.R
import com.example.wehearintershipwork.databinding.FragmentProfileBinding
import com.example.wehearintershipwork.databinding.FragmentUserDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentProfileBinding? = null
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveStateInstance(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user=viewModel.user
        if(user!=null) {
            _binding?.NameProfile?.text = user?.name
            _binding?.dateOfBirthProfile?.text = user?.birthDate
            _binding?.phoneProfile?.text = user?.phoneNo
        }
    }
}