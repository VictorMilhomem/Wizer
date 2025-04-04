package com.github.wizerapp.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

fun generateQRCodeBitmap(text: String, size: Int = 300): Bitmap? {
    return try {
        val barcodeEncoder = BarcodeEncoder()
        barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, size, size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
