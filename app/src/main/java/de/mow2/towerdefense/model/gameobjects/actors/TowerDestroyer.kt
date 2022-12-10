package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameController
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

class TowerDestroyer(towerToDestroy: Tower, val controller: GameController) : GameObject() {
    override var position = Vector2D(0f, 0f)
    override var height = controller.playGround.squareSize
    override var width = height
    override var speed: Float = 0f
        set(value) {
            val rawPixels = (controller.gameWidth + controller.gameHeight) * value
            field = rawPixels / GameLoop.targetUPS
        }

    private var target = 0f

    var isDone = false

    init {
        speed = 0.15f
        orientation = if (towerToDestroy.squareField.mapPos["x"]!! < GameManager.squaresX / 2) {
            position = Vector2D(960f, towerToDestroy.position.y)
            target = (0 - width).toFloat()
            1
        } else {
            position = Vector2D(0f, towerToDestroy.position.y)
            target = (controller.playGround.squareSize * GameManager.squaresX + width).toFloat()
            0
        }
    }

    override fun update() {
        moveTo(Vector2D(target, position.y))
        isDone = distanceToTargetAbs <= 10f
    }
}