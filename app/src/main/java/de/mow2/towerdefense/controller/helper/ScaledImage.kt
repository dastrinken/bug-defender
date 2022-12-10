package de.mow2.towerdefense.controller.helper

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build

/**
 * Takes a Bitmap and resizes its dimensions
 * @param resources resources that hold a reference to the drawable
 * @param width desired width
 * @param height desired height
 * @param bitmapR bitmap resource identifier
 * @param imageQuality
 */
data class ScaledImage(
    val resources: Resources,
    val width: Int,
    val height: Int,
    val bitmapR: Int,
    val imageQuality: String
) {
    private val options = BitmapFactory.Options()
    private val bitmap: Bitmap = BitmapFactory.decodeResource(resources, bitmapR)
    val scaledImage: Bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

    init {
        options.inScaled = false //prevent "rescaling"
        when (imageQuality) {
            "Low" -> options.inPreferredConfig = Bitmap.Config.RGB_565 //low quality bitmaps
            "Avg" -> options.inPreferredConfig = Bitmap.Config.ARGB_8888 //average quality bitmaps
            "High" ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    options.inPreferredConfig =
                        Bitmap.Config.RGBA_F16 //high quality bitmaps, requires min sdk v26
                } else {
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888 //average quality bitmaps
                }
            else -> options.inPreferredConfig = Bitmap.Config.RGB_565 //low quality bitmaps
        }
    }
}

