package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

/**
 * A specific tower
 * @param squareField references the field on which the tower has been built
 * @param type the towers type
 */
class Tower(val squareField: SquareField, var type: TowerTypes) : Comparable<Tower>, GameObject(),
    java.io.Serializable {
    //queue sorting
    override fun compareTo(other: Tower): Int = this.position.y.compareTo(other.position.y)

    //visual scaling and positioning
    override var width = squareField.width
    override var height = 2 * width
    override var position = Vector2D(squareField.position.x, squareField.position.y - width)
    override var speed: Float = 0f

    //weapon positioning
    var weaponOffset = 0f
    var rotationCorrection = 0f
    private var isRotatable = false

    //tower-specific game variables
    var towerLevel: Int = 0
        set(value) {
            field = value
            scaleTowerValues()
        }
    var hasTarget = false
    var target: Enemy? = null
    var targetArray = emptyArray<Enemy>() //for towers that shoot at more than one enemy
    var isShooting = false //flag to detect if tower is shooting. needed value for weapon animation

    //range,dmg,speed base settings
    private var baseSpeed = 120f
    private var baseRange = 2 * width + width / 2
    var finalRange = 0
    var damage = 0

    //special stuff
    var slowAmount = 0
    var slowDuration: Long = 0
    var dotDamage = 0
    var dotInterval: Long = 0

    override fun update() {
        if (isRotatable) {
            if (target != null) {
                distance = target!!.position - position
                val directionTolerance = 80

                if (distance.x > -directionTolerance && distance.x < directionTolerance) { // up / down
                    orientation = if (distance.y < 0) {
                        rotationCorrection = 0f
                        0 // up
                    } else {
                        rotationCorrection = 0f
                        4 // down
                    }
                } else if (distance.y > -directionTolerance && distance.y < directionTolerance) { //left / right
                    orientation = if (distance.x < 0) {
                        rotationCorrection = 0f
                        6 // left
                    } else {
                        rotationCorrection = 0f
                        2 // right
                    }
                } else if (distance.x < -directionTolerance) { // left-diagonals
                    orientation = if (distance.y < 0) {
                        rotationCorrection = -squareField.width / 6f
                        7 // left-up
                    } else {
                        rotationCorrection = -squareField.width / 6f
                        5 // left-down
                    }
                } else if (distance.x > directionTolerance) { //right-diagonals
                    orientation = if (distance.y < 0) {
                        rotationCorrection = -squareField.width / 6f
                        1 // right-up
                    } else {
                        rotationCorrection = -squareField.width / 6f
                        3 // right-down
                    }
                }
            }
        }
    }

    init {
        squareField.tower = this
        scaleTowerValues()
    }

    /**
     * Method to call after tower has been built or upgraded
     * calculates speed, damage and range for this towers level
     */
    private fun scaleTowerValues() {
        when (towerLevel) {
            0 -> weaponOffset = height / 6f
            1 -> weaponOffset = height / 9f
            2 -> weaponOffset = 0f
        }
        when (type) {
            TowerTypes.SINGLE -> {
                isRotatable = true
                finalRange = baseRange + towerLevel * width
                damage = 1 + towerLevel
                actionsPerMinute = baseSpeed + towerLevel * 10
            }
            TowerTypes.SLOW -> {
                finalRange = baseRange + towerLevel * width / 4
                damage = 0
                actionsPerMinute = baseSpeed / 2 + towerLevel * 10
                slowAmount = 2 + towerLevel
                slowDuration = 5000L + towerLevel * 2000L
            }
            TowerTypes.AOE -> {
                finalRange =
                    baseRange + towerLevel * width / 2 //!IMPORTANT: changes made here must also be implemented in BitmapPreloader (bitmap should always be the same size as tower range!)
                damage = 1 + towerLevel * 2
                actionsPerMinute = baseSpeed / 2 + towerLevel * 20
            }
            TowerTypes.MAGIC -> {
                finalRange = baseRange + towerLevel * width
                damage = 5 + towerLevel * 5
                actionsPerMinute = baseSpeed / 4 + towerLevel * 10
                dotDamage = 1 + towerLevel
                dotInterval = (1000 - towerLevel * 200).toLong() //milliseconds
            }
        }
    }
}