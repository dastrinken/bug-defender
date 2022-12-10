package de.mow2.towerdefense.controller.helper

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Takes a Bitmap containing all frames of an animation.
 * Call nextFrame() each time to get the current frame to be drawn onto a canvas.
 * @param bitmap A bitmap containing all frames
 * @param width The desired outcome width
 * @param height The desired outcome height
 * @param rowCount Nr. of rows on Bitmap image. Default = 4 for creep states (walking up, down, left, right)
 * @param frameCount Number of Frames contained in given bitmap
 * @param frameDuration Time duration of one frame in milliseconds
 */
class SpriteAnimation(
    private val bitmap: Bitmap,
    val width: Int,
    private val height: Int,
    private val rowCount: Int = 4,
    private val frameCount: Int = 7,
    private val frameDuration: Int = 30,
    rotate: Boolean = false
) {
    private var animationMap =
        HashMap<Int, Array<Bitmap>>() //holds all different animations for this type
    private lateinit var animation: Array<Bitmap>
    private var startFrameTime = System.currentTimeMillis()

    var frameCounter = 0

    lateinit var idleImage: Bitmap

    init {
        cutSpriteSheet(rotate)
    }

    /**
     * Returns the next frame of the animation, based on elapsed time since the last frame was called
     */
    fun nextFrame(orientation: Int): Bitmap {
        animation = animationMap[orientation]!!
        if (System.currentTimeMillis() - startFrameTime >= frameDuration) {
            update()
            startFrameTime = System.currentTimeMillis()
        }
        return animation[frameCounter]
    }

    /**
     * Cuts Sprite sheet into single frames, based on parameter frameCount and rowCount
     */
    private fun cutSpriteSheet(rotate: Boolean) {
        var rotationDegrees = 0f
        val cutW = bitmap.width / frameCount
        val cutH = if (rotate) {
            bitmap.height
        } else {
            bitmap.height / rowCount
        }
        var cutImg: Bitmap
        var scaledImg: Bitmap
        for (i in 0 until rowCount) {
            var addAnimation = arrayOf<Bitmap>()
            for (j in 0 until frameCount) {
                addAnimation = if (rotate) {
                    cutImg = Bitmap.createBitmap(bitmap, cutW * j, 0, cutW, cutH)
                    scaledImg = Bitmap.createScaledBitmap(cutImg, width, height, true)
                    val rotated = rotateBitmap(scaledImg, rotationDegrees)
                    addAnimation.plus(rotated)
                } else {
                    cutImg = Bitmap.createBitmap(bitmap, cutW * j, cutH * i, cutW, cutH)
                    scaledImg = Bitmap.createScaledBitmap(cutImg, width, height, true)
                    addAnimation.plus(scaledImg)
                }
            }
            rotationDegrees += 45f
            animationMap[i] = addAnimation
        }
        idleImage = animationMap[0]!![0]
    }

    /**
     * takes in a bitmap and rotates it with a matrix
     */
    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(rotationDegrees, bitmap.width / 2f, bitmap.height / 2f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun update() {
        if (frameCounter < animation.size - 1) {
            frameCounter++
        } else {
            frameCounter = 0
        }
    }
}