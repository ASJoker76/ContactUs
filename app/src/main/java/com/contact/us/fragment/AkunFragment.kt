package com.contact.us.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.contact.us.LoginActivity
import com.contact.us.utils.Extensions.toast
import com.contact.us.utils.FirebaseUtils.firebaseAuth
import com.contant.us.databinding.FragmentAkunBinding

class AkunFragment : Fragment() {

    private lateinit var binding: FragmentAkunBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAkunBinding.inflate(inflater, container, false)
        val root: View = binding!!.getRoot()

        binding.tvLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().toast("signed out")
            requireActivity().finish()
        }

        return root
    }
}