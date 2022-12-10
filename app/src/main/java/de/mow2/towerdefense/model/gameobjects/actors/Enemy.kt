package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameController
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D
import de.mow2.towerdefense.model.pathfinding.Astar
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * An instance of this class represents one specific enemy.
 * @param type One value of CreepTypes (e.g. leafbug, firebug...)
 * @param spawnPoint A-star node at which the enemy will spawn
 */
class Enemy(
    val type: EnemyType,
    val controller: GameController,
    private val spawnPoint: Astar.Node = Astar.Node(
        Random.nextInt(0 until GameManager.squaresX),
        0,
        controller
    )
) : GameObject(), Comparable<Enemy>, java.io.Serializable {

    //position
    override var position = controller.playGround.squareArray[spawnPoint.x][spawnPoint.y].position

    //size
    override var height = controller.playGround.squareSize
    override var width = height

    //movement
    override var speed: Float = 0f
        set(value) {
            val rawPixels = (controller.gameWidth + controller.gameHeight) * value
            field = rawPixels / GameLoop.targetUPS
        }

    //path finding
    private val alg = Astar(controller)
    private var targetIndex: Int = 0
    private var finalTarget =
        Astar.Node(spawnPoint.x, GameManager.squaresY - 1, controller) //initial destination
    private lateinit var sortedPath: List<Astar.Node>
    private var currentTargetNode: Astar.Node
    private var currentTargetPosition: Vector2D
    private var minDistance =
        controller.playGround.squareSize * 0.1f //min distance an enemy has to be from the currentTargetPosition to update it's path

    //queue sorting
    override fun compareTo(other: Enemy): Int = this.position.y.compareTo(other.position.y)

    //game variables
    var healthPoints = 0
    var baseDamage = 1
    var baseSpeed = 0f
    var isDead = false
    var isSlowed = false
    var killValue = 1
    var coinValue = 0

    init {
        orientation = 2 //set default orientation to down
        GameManager.enemiesAlive++ // each enemy spawned adds one enemy alive die() will decrement by one

        if (pathToEnd(spawnPoint)) {
            currentTargetNode = sortedPath.first()
            currentTargetPosition =
                controller.playGround.squareArray[currentTargetNode.x][currentTargetNode.y].position
        } else {
            currentTargetNode = spawnPoint
            currentTargetPosition =
                controller.playGround.squareArray[spawnPoint.x][spawnPoint.y].position
        }

        /**
         * set speed, health and baseDamage depending on the type of the creep instance
         */
        when (type) {
            EnemyType.LEAFBUG -> {
                baseSpeed = 0.03f
                healthPoints = if (GameManager.gameLevel != 0) 5 * GameManager.gameLevel else 5
                coinValue = 10
            }
            EnemyType.FIREBUG -> {
                baseSpeed = 0.02f
                healthPoints = 7 * GameManager.gameLevel
                coinValue = 15
            }
            EnemyType.MAGMACRAB -> {
                baseSpeed = 0.02f
                healthPoints = 8 * GameManager.gameLevel
                coinValue = 25
            }
            EnemyType.SCORPION -> {
                baseSpeed = 0.015f
                healthPoints = 8 * GameManager.gameLevel
                coinValue = 20
            }
            EnemyType.CLAMPBEETLE -> {
                baseSpeed = 0.07f
                healthPoints = 1 * GameManager.gameLevel + 1
                coinValue = 20
            }
            EnemyType.FIREWASP -> {
                baseSpeed = 0.08f
                healthPoints = 1 * GameManager.gameLevel
                coinValue = 20
            }
            EnemyType.LOCUST -> {
                baseSpeed = 0.075f
                healthPoints = 1 * GameManager.gameLevel
                coinValue = 20
            }
            EnemyType.VOIDBUTTERFLY -> {
                baseSpeed = 0.075f
                healthPoints = 1 * GameManager.gameLevel
                coinValue = 20
            }
            EnemyType.SKELETONGRUNT -> {
                baseSpeed = 0.02f
                healthPoints = 5 * GameManager.gameLevel
                coinValue = 20
            }
            EnemyType.NECROMANCER -> {
                baseSpeed = 0.02f
                healthPoints = 6 * GameManager.gameLevel
                coinValue = 25
            }
            EnemyType.SKELETONWARRIOR -> {
                baseSpeed = 0.02f
                healthPoints = 8 * GameManager.gameLevel
                coinValue = 25
            }
            EnemyType.SKELETONKNIGHT -> {
                baseSpeed = 0.02f
                healthPoints = 9 * GameManager.gameLevel
                coinValue = 25
            }
            EnemyType.SKELETONKING -> {
                baseSpeed = 0.005f
                healthPoints = 50 * GameManager.gameLevel
                baseDamage = GameManager.livesMax
                coinValue = 50
            }
        }
        //set initial speed (needed to change its value later on)
        speed = baseSpeed
    }

    override fun update() {
        moveTo(currentTargetPosition)
        orientation = if (distance.x < -5) {
            3 //left
        } else if (distance.x > 5) {
            1 //right
        } else if (distance.y < 0) {
            0 //up
        } else {
            2 //down (default)
        }

        //search new path on each Node
        if (distanceToTargetAbs <= minDistance) {
            if (pathToEnd(currentTargetNode)) {
                currentTargetNode = sortedPath.first()
                findNextTarget()
            }
        }
    }

    /**
     * Looks for a path from given starting point (enemies current position or spawn point) to finish line
     * @param from Astar.Node which is the starting point
     * @param to Astar.Node which is the target
     * @return only true if a path has been found
     */
    private fun pathToEnd(from: Astar.Node): Boolean {
        val path = alg.findPath(
            from,
            finalTarget,
            GameManager.squaresX,
            GameManager.squaresY
        ) //find path from spawn to targetNode
        return if (path != null) {//path is available
            sortedPath =
                if (!this::sortedPath.isInitialized) { //first call must initialize sortedPath!
                    path.reversed()
                } else {
                    if (sortedPath != path.reversed()) { //check if new path is different
                        path.reversed()
                    } else {
                        sortedPath
                    }
                }
            true
        } else {//path is blocked
            false
        }
    }

    /**
     * Finds the device coordinates of the next node inside sortedPath
     * @see sortedPath
     */
    private fun findNextTarget() {
        currentTargetNode = sortedPath[targetIndex]
        finalTarget = Astar.Node(currentTargetNode.x, GameManager.squaresY - 1, controller)
        if (currentTargetNode != sortedPath.last()) {
            targetIndex++
        }
        currentTargetPosition =
            controller.playGround.squareArray[currentTargetNode.x][currentTargetNode.y].position
    }

    fun takeDamage(damageAmount: Int, tower: Tower) {
        //use when for special effects like slow, burn etc.
        when (tower.type) {
            TowerTypes.SLOW -> {
                speed = baseSpeed / tower.slowAmount
                isSlowed = true
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        isSlowed = false
                        speed = baseSpeed
                    }
                }, tower.slowDuration)
            }
            TowerTypes.MAGIC -> {
                //damage over time
                Timer().scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        if (!isDead) {
                            healthPoints -= tower.dotDamage
                        } else {
                            this.cancel()
                        }
                    }
                }, 0, tower.dotInterval)
            }
            else -> {
                healthPoints -= damageAmount
            }
        }
    }

    /**
     * removes the enemy from the game and tells the spawner that their is one less enemy alive
     */
    fun die() {
        isDead = true
        GameManager.enemyList.remove(this)
        GameManager.enemiesAlive--
    }

    /**
     * list of all enemies
     */
    enum class EnemyType {
        LEAFBUG, FIREBUG, MAGMACRAB, SCORPION, //ground insects
        CLAMPBEETLE, FIREWASP, LOCUST, VOIDBUTTERFLY, //flying insects
        SKELETONGRUNT, NECROMANCER, SKELETONWARRIOR, SKELETONKNIGHT, //skeletons and necromancer
        SKELETONKING, //boss
    }
}

