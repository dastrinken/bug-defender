package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.model.helper.Vector2D

/**
 * Represents the whole playGround consisting of single squareFields which are initialized in a 2D-array
 * @param width the screen width to make everything fit on devices screen and to calculate the height (ratio 1:2)
 */
class PlayGround(width: Int) : java.io.Serializable {
    var squareArray = emptyArray<Array<SquareField>>()
    private val squaresX = GameManager.squaresX
    private val squaresY = GameManager.squaresY
    val squareSize = width / squaresX

    init {
        var posX = 0
        var posY: Int
        for (i in 0 until squaresX) {
            var cols = emptyArray<SquareField>()
            posY = 0
            for (j in 0 until squaresY) {
                val mapPos = mapOf("x" to i, "y" to j)
                cols = cols.plus(SquareField(Vector2D(posX, posY), mapPos, squareSize))
                posY += squareSize
            }
            posX += squareSize
            squareArray = squareArray.plus(cols)
        }
    }
}