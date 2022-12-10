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
    private var baseSpawnsPerMinute = 10f
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

    //enemy index for scaling purpose
    var enemyIndexStart = 0
    var enemyIndexEnd = 3

    /**
     *
     */
    fun initWave(gameLevel: Int) {
        waveEnemyList.clear()
        for (i in 0..gameLevel) {
            if (baseSpawnsPerMinute <= maxSpawnRPM) {
                baseSpawnsPerMinute *= 1.08f //default = 1.2f; increase spawn rate by 20%
            }
        }
        when {
            //starting wave
            gameLevel == 0 -> {
                waveEnemyList.add(EnemyType.values().first())
                spawnsPerMinute = baseSpawnsPerMinute
                enemyCount = 5
            }
            //for each enemy type one level (except first)
            gameLevel in 1 until EnemyType.values().size - 1 -> { //until + size-1 because SKELETONKING (BOSS) is on the last position
                waveEnemyList.add(EnemyType.values()[gameLevel])
                enemyCount = baseSpawnCount + gameLevel
                spawnsPerMinute = baseSpawnsPerMinute
            }
            //Boss wave
            gameLevel % (EnemyType.values().size - 1) == 0 -> {
                EnemyType.values().forEachIndexed { i, type ->
                    if (i in enemyIndexStart..enemyIndexEnd) {
                        waveEnemyList.add(type)
                    }
                }
                GameManager.addEnemy(Enemy(EnemyType.SKELETONKING, controller))
                enemyCount = baseSpawnCount + gameLevel
                spawnsPerMinute = baseSpawnsPerMinute

                if (enemyIndexEnd < (EnemyType.values().size - 2)) { //if enemyIndexEnd < 11
                    enemyIndexStart += 4
                    enemyIndexEnd += 4
                } else {
                    enemyIndexStart = 0
                    enemyIndexEnd = 3
                }
            }
            //infinite waves. Enemies spawned in groups. No Boss
            gameLevel > EnemyType.values().size - 1 -> {
                EnemyType.values().forEachIndexed { i, type ->
                    if (i in enemyIndexStart..enemyIndexEnd) {
                        waveEnemyList.add(type)
                    }
                }
                enemyCount = baseSpawnCount * gameLevel
                spawnsPerMinute = baseSpawnsPerMinute
            }
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
        Log.i("enemyIndexStart", "$enemyIndexStart")
        Log.i("enemyIndexEnd", "$enemyIndexEnd")
    }
}


