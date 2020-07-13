package com.example.pemesanantiket.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import javax.security.auth.callback.Callback

fun showPopupDialog(
        context : Context?,
        title: String?,
        message: String?,
        positiveTitle : String?,
        negativeTitle: String?,
        callback: (isOk : Boolean) -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title).setMessage(message)
            .setPositiveButton(positiveTitle) { dialog, which ->
                callback(true)
            }
            .setNegativeButton(negativeTitle){dialog, which ->
                callback(false)
            }
    builder.create().show()
}

fun showToast(
        context: Context?,
        message: String?
){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}