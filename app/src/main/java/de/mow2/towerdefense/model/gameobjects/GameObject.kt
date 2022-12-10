package de.mow2.towerdefense.model.gameobjects

import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.helper.Vector2D
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * GameObject is the foundation of any moving or static actor in the game
 */
abstract class GameObject {
    //size
    abstract var width: Int
    abstract var height: Int

    //position
    abstract var position: Vector2D
    val positionCenter: Vector2D
        get() {
            //return the center point of specific game object
            return Vector2D(position.x + width / 2, position.y + height / 2)
        }

    //sprite direction
    var orientation: Int = 0

    //variables for movement calculation
    protected var distance = Vector2D(0, 0)
    protected var distanceToTargetAbs: Float = 0f
    private lateinit var direction: Vector2D
    private lateinit var velocity: Vector2D

    /**
     * Pixels per update for movement.
     * Will be multiplied with direction to get a velocity.
     * @see moveTo()
     */
    abstract var speed: Float

    private var updateCycle: Float = 0f

    /**
     * defines the frequency
     */
    var actionsPerMinute: Float = 0f
        set(value) {
            field = value
            if (field != 0f) {
                val actionsPerSecond: Float = field / 60
                //link with target updates per second to convert to updates per action
                updateCycle = GameLoop.targetUPS / actionsPerSecond
            }
        }
    var waitUpdates: Float = 0f

    abstract fun update()

    /**
     * Moves a GameObject to another. Has to be called from a subclass of GameObject.
     * Subclasses have to init movement speed.
     * Default movement speed is 0.
     * @see speed
     */
    fun moveTo(target: Vector2D) {
        //vector between this and the target
        distance = target - position
        //magnitude (length) of the vector
        distanceToTargetAbs = findDistance(position, target)
        //unit vector / normalized vector (distance traveled in one unit of time (with direction))
        direction = distance / distanceToTargetAbs
        //check if target has been reached and set velocity if it hasn't
        velocity = if (distanceToTargetAbs > 0f) {
            //multiply normalized vector with speed
            direction * speed
        } else {
            Vector2D(0, 0)
        }
        //update coordinates
        position += velocity
    }


    private var xDiff: Float = 0f
    private var yDiff: Float = 0f

    /**
     * gets the euclidean distance between two GameObjects
     * @param obj1 GameObject that will provide x- and y-coordinates as a starting point
     * @param obj2 GameObject that will provide x- and y-coordinates as a destination
     * @return Float
     */
    fun findDistance(obj1: GameObject, obj2: GameObject): Float {
        xDiff = (obj2.positionCenter.x - obj1.positionCenter.x).pow(2)
        yDiff = (obj2.positionCenter.y - obj1.positionCenter.y).pow(2)
        return sqrt(xDiff + yDiff)
    }

    /**
     * gets the euclidean distance between two Vector2D objects
     * @param from Vector2D that will provide x- and y-coordinates as a starting point
     * @param to Vector2D that will provide x- and y-coordinates as a destination
     * @return Float
     */
    fun findDistance(from: Vector2D, to: Vector2D): Float {
        xDiff = (to.x - from.x).pow(2)
        yDiff = (to.y - from.y).pow(2)
        return sqrt(xDiff + yDiff)
    }

    /**
     * Cycles between true and false.
     * Enables GameObjects to have a set amount of actions per minute.
     * @see actionsPerMinute
     * @return true || false
     */
    open fun cooldown(): Boolean {
        return if (waitUpdates <= 0f) {
            waitUpdates += updateCycle
            true
        } else {
            waitUpdates--
            false
        }
    }
}
