package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameController
import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.Sounds
import de.mow2.towerdefense.controller.helper.BitmapPreloader
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

class Projectile(val tower: Tower, val enemy: Enemy, val controller: GameController) :
    GameObject() {
    override var position = tower.positionCenter
    override var width: Int = 0
    override var height: Int = 0
    val baseDamage = tower.damage

    //movement
    override var speed: Float = 0f
        set(value) {
            val rawPixels = (controller.gameWidth + controller.gameHeight) * value
            field = rawPixels / GameLoop.targetUPS
        }

    init {
        speed = 0.2f
        when (tower.type) {
            TowerTypes.SINGLE -> {
                SoundManager.soundPool.play(Sounds.ARROWSHOT.id, 0.2F, 0.2F, 2, 0, 1F)
            }
            TowerTypes.SLOW -> {
                SoundManager.soundPool.play(Sounds.SLOWSHOT.id, 0.2F, 0.2F, 2, 0, 1F)
            }
            TowerTypes.AOE -> {
                SoundManager.soundPool.play(Sounds.AOESHOT.id, 0.3F, 0.3F, 2, 0, 1F)
                width =
                    BitmapPreloader.projectileAnimsArray[tower.towerLevel][tower.type]!!.width / 2
                height = width
                position = Vector2D(position.x - width, position.y - height)
            }
            TowerTypes.MAGIC -> {
                SoundManager.soundPool.play(Sounds.MAGICSHOT.id, 0.2F, 0.2F, 2, 0, 1F)
            }
        }
    }

    override fun update() {
        if (tower.type != TowerTypes.AOE) {
            moveTo(enemy.positionCenter)

            orientation = if (distance.x < -5) {
                3 //left
            } else if (distance.x > 5) {
                1 //right
            } else if (distance.y < 0) {
                0 //up
            } else {
                2 //down (default)
            }
        } else {
            if (!tower.isShooting) {
                GameManager.projectileList.remove(this)
            }
        }
    }
}