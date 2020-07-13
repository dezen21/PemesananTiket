package com.example.pemesanantiket.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream


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

fun generateBarcode(
        context: Context?,
        code : String?,
        callback: (imgUri : String) -> Unit
){
    val multiFormatWriter = MultiFormatWriter();
    try {
        val bitMatrix = multiFormatWriter
                .encode(code, BarcodeFormat.CODE_128, 222,52)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        callback(getImageUri(context!!, bitmap))
    }catch (e : Exception){
        Log.d("TAG", "onCreate: $e")
    }
}

fun getImageUri(context: Context, inImage: Bitmap): String {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media
            .insertImage(context.contentResolver, inImage, "Title", null)
    return Uri.parse(path).toString()
}