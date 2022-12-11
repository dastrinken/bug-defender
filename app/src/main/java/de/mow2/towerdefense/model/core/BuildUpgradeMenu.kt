package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.controller.GameController
import de.mow2.towerdefense.controller.SoundManager.soundPool
import de.mow2.towerdefense.controller.Sounds
import de.mow2.towerdefense.model.gameobjects.actors.Tower
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

/**
 * Defines logic behind build and upgrade functions
 */
class BuildUpgradeMenu(private val controller: GameController) {
    /**
     * Calculates tower value based on its type and level
     * @param type value of TowerTypes
     * @param level this towers level
     */
    fun getTowerCost(type: TowerTypes, level: Int = 0): Int {
        val cost = when (type) {
            TowerTypes.SINGLE -> 100
            TowerTypes.SLOW -> 200
            TowerTypes.AOE -> 500
            TowerTypes.MAGIC -> 5000
        }
        return cost * (level + 1)
    }

    /**
     * Adds a specific tower to the drawing list and blocks the underlying field for enemy movement
     * @param selectedField the field at which the tower is to be placed
     * @param towerType the specific type of the tower to be built
     */
    fun buildTower(selectedField: SquareField, towerType: TowerTypes) {
        if (!selectedField.isBlocked) {
            val cost = getTowerCost(towerType)
            if (controller.gameManager.decreaseCoins(cost)) {
                val tower = when (towerType) {
                    TowerTypes.SINGLE -> {
                        Tower(selectedField, TowerTypes.SINGLE)
                    }
                    TowerTypes.SLOW -> {
                        Tower(selectedField, TowerTypes.SLOW)
                    }
                    TowerTypes.AOE -> {
                        Tower(selectedField, TowerTypes.AOE)
                    }
                    TowerTypes.MAGIC -> {
                        Tower(selectedField, TowerTypes.MAGIC)
                    }
                }
                selectedField.isBlocked = true //important!! block field for path finding
                GameManager.addTower(tower)
                GameManager.lastTower = tower
                soundPool.play(Sounds.BUILD.id, 1F, 1F, 1, 0, 1F)
                controller.gameManager.validatePlayGround()
            } else {
                controller.showToastMessage("money")
            }
        }
    }

    /**
     * Destroys a tower and resets the squareField
     */
    fun destroyTower(tower: Tower) {
        tower.squareField.removeTower() //free square
        GameManager.towerList.remove(tower) //remove tower from drawing list
        controller.gameManager.increaseCoins(
            getTowerCost(
                tower.type,
                tower.towerLevel
            ) / 2
        ) //get half of the tower value back
        soundPool.play(Sounds.TOWERDESTROY.id, 1F, 1F, 1, 0, 1F)
        controller.gameManager.validatePlayGround()
    }

    /**
     * Upgrades a tower
     */
    fun upgradeTower(selectedTower: Tower?) {
        //only upgrade if there is a tower which is below max level and if player can afford the upgrade
        if ((selectedTower != null) && (selectedTower.towerLevel < GameManager.maxTowerLevel) && controller.gameManager.decreaseCoins(
                getTowerCost(selectedTower.type, selectedTower.towerLevel + 1)
            )
        ) {
            selectedTower.towerLevel++
        }
    }
}