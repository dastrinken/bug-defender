package de.mow2.towerdefense.controller.helper

import android.content.Context
import android.util.Log
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.gameobjects.actors.Tower
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes
import java.io.*

/**
 * Saves and loads game data
 * @param context App context
 */
class GameState(val context: Context) {

    /**
     * Returns save game file
     * @param context App context
     */
    fun defineFile(context: Context): File {
        val dir = context.filesDir
        return File(dir, "/gameState.json")
    }

    /**
     * Call this to save game data to local file
     */
    fun saveGameState() {
        val file = defineFile(context)
        //detects if file exists, creates one if not and calls saveGame to store game variables and objects
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Log.i("SaveGame: ", "Neue Datei erstellt")
                    saveGame(file)
                } else {
                    Log.i("SaveGame: ", "Datei konnte nicht erstellt werden")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.i("SaveGame: ", "Vorhandene Datei wird beschrieben")
            saveGame(file)
        }
    }

    /**
     * Saves game data to specified file
     * @param file save game file
     */
    private fun saveGame(file: File) {
        try {
            //open output stream
            val output = FileOutputStream(file)
            val objectOut = ObjectOutputStream(output)
            //save all needed data
            objectOut.writeObject(GameManager.gameLevel)
            objectOut.writeObject(GameManager.enemyIndexStart)
            objectOut.writeObject(GameManager.enemyIndexEnd)
            objectOut.writeObject(GameManager.enemyIndexOffset)
            objectOut.writeObject(GameManager.spawnRate)
            objectOut.writeObject(GameManager.enemiesKilled)
            objectOut.writeObject(GameManager.livesAmnt)
            objectOut.writeObject(GameManager.livesMax)
            objectOut.writeObject(GameManager.coinAmnt)
            objectOut.writeObject(GameManager.killCounter)

            var towerArray = emptyArray<Array<*>>()
            GameManager.towerList.forEach { tower ->
                val type = tower.type
                val level = tower.towerLevel
                val posX = tower.squareField.mapPos["x"]
                val posY = tower.squareField.mapPos["y"]
                val entry = arrayOf(type, level, posX, posY)
                towerArray += entry
            }
            objectOut.writeObject(towerArray)

            //close output stream
            objectOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reads all saved game data from specified file, if it exists
     */
    fun readGameState(playGround: PlayGround) {
        val file = defineFile(context)
        //load from file if it exists
        if (file.exists()) {
            val input = ObjectInputStream(FileInputStream(file))
            //read all saved variables and objects
            val level = input.readObject() as Int
            val enemyIndexStart = input.readObject() as Int
            val enemyIndexEnd = input.readObject() as Int
            val enemyIndexOffset = input.readObject() as Int
            val spawnRate = input.readObject() as Float
            val enemiesKilled = input.readObject() as Int
            val lives = input.readObject() as Int
            val livesMax = input.readObject() as Int
            val coins = input.readObject() as Int
            val kills = input.readObject() as Int
            val towerList = input.readObject() as Array<Array<*>>
            //reset tower objects
            towerList.forEach {
                val squareField = playGround.squareArray[it[2] as Int][it[3] as Int]
                squareField.isBlocked = true
                val tower = Tower(squareField, it[0] as TowerTypes)
                tower.towerLevel = it[1] as Int
                GameManager.towerList.add(tower)
            }
            //replace GameManager variables with saved ones
            GameManager.gameLevel = level
            GameManager.enemyIndexStart = enemyIndexStart
            GameManager.enemyIndexEnd = enemyIndexEnd
            GameManager.enemyIndexOffset = enemyIndexOffset
            GameManager.enemiesKilled = enemiesKilled
            GameManager.spawnRate = spawnRate
            GameManager.livesAmnt = lives
            GameManager.livesMax = livesMax
            GameManager.coinAmnt = coins
            GameManager.killCounter = kills
            //close input stream
            input.close()
        }
    }

    /**
     * Deletes the save game file if exists
     * @return Boolean - true if file is deleted; false otherwise
     */
    fun deleteSaveGame(): Boolean {
        val file = defineFile(context)
        return file.delete()
    }
}