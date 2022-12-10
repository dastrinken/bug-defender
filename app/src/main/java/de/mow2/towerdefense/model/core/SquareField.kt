package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.model.gameobjects.actors.Tower
import de.mow2.towerdefense.model.helper.Vector2D

/**
 * Represents a single field on the playground
 * @param position a Vector2D which represents x and y coordinates on screen
 * @param width width of the field
 * @param height height of the field
 * @param mapPos a map representing x and y coordinates based on its position on screen / in 2D-array
 */
class SquareField(
    val position: Vector2D,
    val mapPos: Map<String, Int>,
    val width: Int,
    val height: Int = width
) : java.io.Serializable {

    //var for blocking this field (tower built)
    var isBlocked = false
    var tower: Tower? = null

    /**
     * call when removing a tower, frees itself from blocked state
     */
    fun removeTower() {
        isBlocked = false
        tower = null
    }

    override fun toString(): String {
        return "x: ${mapPos["x"]} y: ${mapPos["y"]}"
    }
}