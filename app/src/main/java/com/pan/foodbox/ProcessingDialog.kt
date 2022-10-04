package com.pan.foodbox

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pan.foodbox.ui.MainActivity

class ProcessingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val alertDialog = AlertDialog.Builder(it)
            alertDialog.setView(
                requireActivity().layoutInflater.inflate(
                    R.layout.processing_dialog,
                    null
                )
            )
                .setCancelable(false)
            alertDialog.setPositiveButton("submit", DialogInterface.OnClickListener { dialog, it ->
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            })
            alertDialog.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener() { dialog, which ->

                })
            alertDialog.create()
        } ?: throw IllegalStateException("Activity is null !")
    }

}