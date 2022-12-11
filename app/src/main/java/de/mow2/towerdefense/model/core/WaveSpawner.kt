package de.mow2.towerdefense.model.core

import android.util.Log
import de.mow2.towerdefense.controller.GameController
import de.mow2.towerdefense.model.gameobjects.actors.Enemy
import de.mow2.towerdefense.model.gameobjects.actors.Enemy.EnemyType

class WaveSpawner(val controller: GameController) {
    //enemy types and count for a specific wave
    private val baseSpawnCount = 5
    private var waveEnemyList = arrayListOf<EnemyType>()
    var enemyCount: Int = 0  //the number of enemies for this wave.

    //spawn rate values
    private var baseSpawnsPerMinute = GameManager.spawnRate
    private var maxSpawnRPM = 130 //default = 120
    private var updateCycle: Float = 0f
    private var waitUpdates: Float = 0f
    private var spawnsPerMinute: Float = 0f
        set(value) {
            field = value
            val spawnsPerSecond: Float = field / 60
            //link with target updates per second to convert to updates per spawn
            updateCycle = GameLoop.targetUPS / spawnsPerSecond
        }

    //spawn type calculation
    private val enemyTypesCount = EnemyType.values().size - 1 //total amount of different enemy types without boss
    private var regWaveStartIndex = GameManager.enemyIndexStart
    private var regWaveEndIndex = GameManager.enemyIndexEnd
    private var regWaveIndexOffset = GameManager.enemyIndexOffset

    /**
     * spawn rate cycle. Wait a specific amount of updates
     * @see spawnsPerMinute controls how often per minute this function returns true
     * @return true || false
     */
    private fun canSpawn(): Boolean {
        return if (waitUpdates <= 0f) {
            waitUpdates += updateCycle
            true
        } else {
            waitUpdates--
            false
        }
    }

    /**
     *
     */
    fun spawnEnemy() {
        if (canSpawn() && enemyCount != 0) {
            if (enemyCount > 0) {
                GameManager.addEnemy(Enemy(waveEnemyList.random(), controller))
                enemyCount--
            }
        }
    }

    fun initWave(gameLevel: Int) {
        waveEnemyList.clear()
        if (baseSpawnsPerMinute <= maxSpawnRPM) {
            baseSpawnsPerMinute *= 1.15f //spawnrate increases each level
        }
        when {
            //first wave
            gameLevel == 0 -> {
                waveEnemyList.add(EnemyType.values().first())
                spawnsPerMinute = baseSpawnsPerMinute
                enemyCount = 5
            }
            gameLevel < EnemyType.values().lastIndex -> {
                waveEnemyList.add(EnemyType.values()[gameLevel])
                enemyCount = baseSpawnCount + gameLevel
                spawnsPerMinute = baseSpawnsPerMinute
            }
            //Boss wave
            gameLevel % EnemyType.values().lastIndex == 0 -> {
                regWaveStartIndex = 0
                regWaveEndIndex = regWaveIndexOffset
                GameManager.addEnemy(Enemy(EnemyType.SKELETONKING, controller)) //spawn boss
                EnemyType.values().forEachIndexed { i, type ->
                    if (i in regWaveStartIndex..regWaveEndIndex) {
                        waveEnemyList.add(type)
                    }
                }
                enemyCount = baseSpawnCount + gameLevel
                spawnsPerMinute = baseSpawnsPerMinute

                if(regWaveIndexOffset < 4) regWaveIndexOffset += 1
            }
            //generate regular waves
            else -> {
                if(regWaveEndIndex < enemyTypesCount) {
                    regWaveEndIndex += 1
                } else {
                    regWaveStartIndex = 0
                    regWaveEndIndex = regWaveIndexOffset
                }
                EnemyType.values().forEachIndexed { i, type ->
                    if (i in regWaveStartIndex..regWaveEndIndex) {
                        waveEnemyList.add(type)
                    }
                }
                regWaveStartIndex += 1

                enemyCount = baseSpawnCount + gameLevel
                spawnsPerMinute = baseSpawnsPerMinute
            }/*
            //infinite waves. Enemies spawned in groups. No Boss
            gameLevel > EnemyType.values().size - 1 -> {
                EnemyType.values().forEachIndexed { i, type ->
                    if (i in enemyIndexStart..enemyIndexEnd) {
                        waveEnemyList.add(type)
                    }
                }
                enemyCount = baseSpawnCount * gameLevel
                spawnsPerMinute = baseSpawnsPerMinute
            }*/
        }
    }

    //
    private fun debugSpawnerValues() {
        Log.i("spacer", "----------")
        Log.i("gameLevel", "${GameManager.gameLevel}")
        Log.i("spawnCount", "$enemyCount")
        Log.i("startingStrong", "$maxSpawnRPM")
        Log.i("spawnsPerMinute", "$spawnsPerMinute")
        Log.i("waveEnemyList", "$waveEnemyList")
        Log.i("enemyIndexStart", "$regWaveStartIndex")
        Log.i("enemyIndexEnd", "$regWaveEndIndex")
        Log.i("enemyIndexOffset", "$regWaveIndexOffset")
    }
}


