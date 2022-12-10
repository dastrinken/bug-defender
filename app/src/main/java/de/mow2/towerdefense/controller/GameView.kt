package de.mow2.towerdefense.controller

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.helper.BitmapPreloader
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.helper.Vector2D

/**
 * View component that displays game content on screen such as play ground, towers, enemies and so on
 */
class GameView(context: Context) :
    SurfaceView(context), SurfaceHolder.Callback {
    constructor(context: Context, controller: GameController) : this(context) {
        this.controller = controller
    }

    //background tiles
    private lateinit var controller: GameController
    private val bgPaint: Paint

    init {
        holder.addCallback(this)
        holder.setFormat(0x00000004)

        //initializing background tiles
        bgPaint = Paint()
        bgPaint.style = Paint.Style.FILL
        bgPaint.shader = BitmapShader(
            BitmapPreloader.playgroundBG,
            Shader.TileMode.REPEAT,
            Shader.TileMode.REPEAT
        )
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setWillNotDraw(false)
        //start game loop
        controller.initGameLoop()
        if (!GameManager.tutorialsActive) {
            controller.toggleGameLoop(true)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                controller.gameLoop.setRunning(false)
                controller.gameLoop.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            retry = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //drawing background
        canvas.drawPaint(bgPaint)

        ////////////////////
        //object draw area//

        drawObjects(canvas)

        //object draw area end//
        ///////////////////////

        //redraw canvas if canvas has changed
        invalidate()
    }

    /**
     * Iterates through game objects and calls draw method
     *
     * ! Use iterators for lists, or use ConcurrentHashMaps (or similar thread safe collection) to avoid ConcurrentModificationException !
     */
    private fun drawObjects(canvas: Canvas) {
        //enemies
        GameManager.enemyList.forEach { enemy ->
            draw(
                canvas,
                BitmapPreloader.enemyAnims[enemy.type]!!.nextFrame(enemy.orientation),
                enemy.position
            )
        }
        //towers
        GameManager.towerList.forEach { tower ->
            draw(
                canvas,
                BitmapPreloader.towerImagesArray[tower.towerLevel][tower.type],
                tower.position
            )
            if (controller.selectedTool == R.id.upgradeButton && tower.towerLevel < GameManager.maxTowerLevel && controller.buildMenu.getTowerCost(
                    tower.type,
                    tower.towerLevel + 1
                ) <= GameManager.coinAmnt
            ) {
                draw(canvas, BitmapPreloader.upgradeOverlay, tower.position)
            }
            if (tower.isShooting && GameManager.waveActive) {
                draw(
                    canvas,
                    BitmapPreloader.weaponAnimsArray[tower.towerLevel][tower.type]!!.nextFrame(tower.orientation),
                    Vector2D(
                        tower.position.x + tower.rotationCorrection,
                        tower.position.y + tower.weaponOffset + tower.rotationCorrection
                    )
                )
            } else {
                draw(
                    canvas,
                    BitmapPreloader.weaponAnimsArray[tower.towerLevel][tower.type]!!.idleImage,
                    Vector2D(tower.position.x, tower.position.y + tower.weaponOffset)
                )
            }
        }
        //projectiles
        GameManager.projectileList.forEach { projectile ->
            draw(
                canvas,
                BitmapPreloader.projectileAnimsArray[projectile.tower.towerLevel][projectile.tower.type]!!.nextFrame(
                    projectile.orientation
                ),
                projectile.position
            )
        }

        if (GameManager.towerDestroyer != null) {
            draw(
                canvas,
                BitmapPreloader.towerDestroyerAnims.nextFrame(GameManager.towerDestroyer!!.orientation),
                GameManager.towerDestroyer!!.position
            )
        }
    }

    /**
     * draws a bitmap onto canvas
     */
    private fun draw(canvas: Canvas, bitmap: Bitmap?, position: Vector2D) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, position.x, position.y, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = controller.gameHeight
        val width = controller.gameWidth

        setMeasuredDimension(width, height)
    }

    /**
     * handling user inputs
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val x: Float;
        val y: Float

        if (ev?.action == MotionEvent.ACTION_UP) {
            if (GameManager.waveActive) {
                x = ev.x
                y = ev.y
                val selectedField = getTouchedSquare(x, y)
                if (selectedField.mapPos["y"] in 1 until GameManager.squaresY - 1) {
                    when (controller.selectedTool) {
                        R.id.deleteButton -> {
                            if (selectedField.tower != null) {
                                controller.buildMenu.destroyTower(selectedField.tower!!)
                            }
                        }
                        R.id.buildButton -> {
                            controller.buildMenu.buildTower(selectedField, controller.selectedTower)
                        }
                        R.id.upgradeButton -> {
                            controller.buildMenu.upgradeTower(selectedField.tower)
                        }
                    }
                }
                invalidate()
            }
        }
        return true
    }

    /**
     * Takes x and y position of a touch event and returns the specific SquareField to work with
     * @param x The horizontal position on screen in pixels
     * @param y The vertical position on screen in pixels
     */
    private fun getTouchedSquare(x: Float, y: Float): SquareField {
        var xPos = 0
        var yPos = 0
        controller.playGround.squareArray.forEachIndexed { i, it ->
            it.forEachIndexed { j, element ->
                val coordRangeX = element.position.x..(element.position.x + element.width)
                val coordRangeY = element.position.y..(element.position.y + element.height)
                if (x in coordRangeX && y in coordRangeY) {
                    xPos = i
                    yPos = j
                }
            }
        }
        return controller.playGround.squareArray[xPos][yPos]
    }
}