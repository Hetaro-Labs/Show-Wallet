package com.showtime.wallet.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter


/**
 * QrCode Util
 */
object QRCodeGenerator {

    fun generateQRCode(data: String, width: Int, height: Int): Bitmap? {
        try {
            // 创建QRCodeWriter实例
            val qrCodeWriter = QRCodeWriter()

            // 设置编码参数
            val hints: MutableMap<EncodeHintType, Any?> = HashMap()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8" // 字符编码
            hints[EncodeHintType.MARGIN] = 1 // 边距，最小为0

            // 生成BitMatrix（二维码的矩阵表示）
            val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints)

            // 初始化Bitmap
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = Color.BLACK
                    } else {
                        pixels[y * width + x] = Color.WHITE
                    }
                }
            }
            // 根据像素数组生成Bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }
}