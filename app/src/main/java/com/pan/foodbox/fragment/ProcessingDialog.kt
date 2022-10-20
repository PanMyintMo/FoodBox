package com.pan.foodbox.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pan.foodbox.databinding.ProcessingDialogBinding
import com.pan.foodbox.ui.RegisterActivity

class ProcessingDialog : DialogFragment() {
    private var _binding: ProcessingDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ProcessingDialogBinding.inflate(layoutInflater, container, false)
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.btnOk.setOnClickListener {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}