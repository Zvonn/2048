package com.example.game2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var gameLogic: GameLogic
    private val paint = Paint()
    private val textPaint = Paint()
    private val backgroundPaint = Paint()
    private val tileRectF = RectF()

    private val backgroundRectF = RectF()
    private val tileColors = IntArray(12)
    private val textColors = IntArray(12)
    private val textSizes = FloatArray(12)

    init {

        backgroundPaint.color = ContextCompat.getColor(context, R.color.backgroundColor)


        tileColors[0] = ContextCompat.getColor(context, R.color.tile2)
        tileColors[1] = ContextCompat.getColor(context, R.color.tile4)
        tileColors[2] = ContextCompat.getColor(context, R.color.tile8)
        tileColors[3] = ContextCompat.getColor(context, R.color.tile16)
        tileColors[4] = ContextCompat.getColor(context, R.color.tile32)
        tileColors[5] = ContextCompat.getColor(context, R.color.tile64)
        tileColors[6] = ContextCompat.getColor(context, R.color.tile128)
        tileColors[7] = ContextCompat.getColor(context, R.color.tile256)
        tileColors[8] = ContextCompat.getColor(context, R.color.tile512)
        tileColors[9] = ContextCompat.getColor(context, R.color.tile1024)
        tileColors[10] = ContextCompat.getColor(context, R.color.tile2048)
        tileColors[11] = ContextCompat.getColor(context, R.color.tileSuper)


        textColors[0] = ContextCompat.getColor(context, R.color.textColorDark)
        textColors[1] = ContextCompat.getColor(context, R.color.textColorDark)
        for (i in 2 until 12) {
            textColors[i] = ContextCompat.getColor(context, R.color.textColorLight)
        }


        val baseTextSize = context.resources.getDimension(R.dimen.tile_text_size)
        for (i in 0 until 12) {
            textSizes[i] = when {
                i < 6 -> baseTextSize
                i < 9 -> baseTextSize * 0.9f
                else -> baseTextSize * 0.8f
            }
        }
    }

    fun setGameLogic(gameLogic: GameLogic) {
        this.gameLogic = gameLogic
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!::gameLogic.isInitialized) return

        val width = width.toFloat()
        val height = height.toFloat()
        val gridSize = minOf(width, height)
        val cellSize = (gridSize - 5 * context.resources.getDimension(R.dimen.grid_spacing)) / 4
        val startX = (width - gridSize) / 2
        val startY = (height - gridSize) / 2


        backgroundRectF.set(startX, startY, startX + gridSize, startY + gridSize)
        canvas.drawRoundRect(
            backgroundRectF,
            context.resources.getDimension(R.dimen.grid_corner_radius),
            context.resources.getDimension(R.dimen.grid_corner_radius),
            backgroundPaint
        )


        paint.color = ContextCompat.getColor(context, R.color.emptyTile)
        val spacing = context.resources.getDimension(R.dimen.grid_spacing)

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val left = startX + col * (cellSize + spacing) + spacing
                val top = startY + row * (cellSize + spacing) + spacing
                val right = left + cellSize
                val bottom = top + cellSize

                tileRectF.set(left, top, right, bottom)
                canvas.drawRoundRect(
                    tileRectF,
                    context.resources.getDimension(R.dimen.tile_corner_radius),
                    context.resources.getDimension(R.dimen.tile_corner_radius),
                    paint
                )
            }
        }


        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val tile = gameLogic.getTileAt(row, col)
                if (tile != null) {
                    val left = startX + col * (cellSize + spacing) + spacing
                    val top = startY + row * (cellSize + spacing) + spacing
                    val right = left + cellSize
                    val bottom = top + cellSize

                    tileRectF.set(left, top, right, bottom)

                    val colorIndex = when (tile.value) {
                        2 -> 0
                        4 -> 1
                        8 -> 2
                        16 -> 3
                        32 -> 4
                        64 -> 5
                        128 -> 6
                        256 -> 7
                        512 -> 8
                        1024 -> 9
                        2048 -> 10
                        else -> 11
                    }

                    paint.color = tileColors[colorIndex]
                    canvas.drawRoundRect(
                        tileRectF,
                        context.resources.getDimension(R.dimen.tile_corner_radius),
                        context.resources.getDimension(R.dimen.tile_corner_radius),
                        paint
                    )

                    textPaint.color = textColors[colorIndex]
                    textPaint.textSize = textSizes[colorIndex]
                    textPaint.textAlign = Paint.Align.CENTER
                    textPaint.isFakeBoldText = true

                    val text = tile.value.toString()
                    val textX = left + cellSize / 2
                    val textY = top + cellSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2

                    canvas.drawText(text, textX, textY, textPaint)
                }
            }
        }

        if (gameLogic.isGameOver()) {
            paint.color = ContextCompat.getColor(context, R.color.gameOverBackground)
            canvas.drawRoundRect(
                backgroundRectF,
                context.resources.getDimension(R.dimen.grid_corner_radius),
                context.resources.getDimension(R.dimen.grid_corner_radius),
                paint
            )

            textPaint.color = ContextCompat.getColor(context, R.color.gameOverText)
            textPaint.textSize = context.resources.getDimension(R.dimen.game_over_text_size)
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                context.getString(R.string.game_over),
                width / 2,
                height / 2,
                textPaint
            )
        } else if (gameLogic.hasWon() && !gameLogic.isContinuingAfterWin()) {
            paint.color = ContextCompat.getColor(context, R.color.winBackground)
            canvas.drawRoundRect(
                backgroundRectF,
                context.resources.getDimension(R.dimen.grid_corner_radius),
                context.resources.getDimension(R.dimen.grid_corner_radius),
                paint
            )

            textPaint.color = ContextCompat.getColor(context, R.color.winText)
            textPaint.textSize = context.resources.getDimension(R.dimen.win_text_size)
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                context.getString(R.string.you_win),
                width / 2,
                height / 2,
                textPaint
            )
        }
    }
}

