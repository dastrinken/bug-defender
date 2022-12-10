package de.mow2.towerdefense.controller

import de.mow2.towerdefense.controller.helper.GameState
import de.mow2.towerdefense.model.core.BuildUpgradeMenu
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

interface GameController {
    var gameManager: GameManager
    var gameLoop: GameLoop
    var gameHeight: Int
    var gameWidth: Int
    var playGround: PlayGround

    var gameState: GameState
    val buildMenu: BuildUpgradeMenu
    var selectedTool: Int?
    var selectedTower: TowerTypes

    fun updateGUI()
    fun updateHealthBarMax(newMax: Int)
    fun updateProgressBarMax(newMax: Int)
    fun onGameOver()
    fun showToastMessage(type: String)

    fun initGameLoop()
    fun toggleGameLoop(setRunning: Boolean)
}