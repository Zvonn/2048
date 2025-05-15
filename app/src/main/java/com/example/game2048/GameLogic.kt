package com.example.game2048

import android.content.Context
import kotlin.random.Random

class GameLogic(private val context: Context) {
    enum class Direction { UP, RIGHT, DOWN, LEFT }

    private val grid = Array(4) { arrayOfNulls<Tile>(4) }
    private var score = 0
    private var bestScore = 0
    private var won = false
    private var continueAfterWin = false
    private var gameOver = false

    fun startNewGame() {

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                grid[row][col] = null
            }
        }

        score = 0
        won = false
        continueAfterWin = false
        gameOver = false

        addRandomTile()
        addRandomTile()
    }

    fun getTileAt(row: Int, col: Int): Tile? {
        return grid[row][col]
    }

    fun getScore(): Int = score

    fun getBestScore(): Int = bestScore

    fun setBestScore(score: Int) {
        bestScore = score
    }

    fun hasWon(): Boolean = won

    fun isGameOver(): Boolean = gameOver

    fun isContinuingAfterWin(): Boolean = continueAfterWin

    fun continueAfterWin() {
        continueAfterWin = true
    }

    fun move(direction: Direction): Boolean {
        if (gameOver) return false
        resetMergeFlags()
        var moved = false

        val gridCopy = Array(4) { row ->
            Array(4) { col ->
                grid[row][col]?.copy()
            }
        }

        when (direction) {
            Direction.UP -> {
                for (col in 0 until 4) {
                    moved = moveTilesUp(col) || moved
                }
            }
            Direction.RIGHT -> {
                for (row in 0 until 4) {
                    moved = moveTilesRight(row) || moved
                }
            }
            Direction.DOWN -> {
                for (col in 0 until 4) {
                    moved = moveTilesDown(col) || moved
                }
            }
            Direction.LEFT -> {
                for (row in 0 until 4) {
                    moved = moveTilesLeft(row) || moved
                }
            }
        }

        if (moved) {
            addRandomTile()


            if (!won) {
                checkForWin()
            }


            if (!canMove()) {
                gameOver = true
            }


            if (score > bestScore) {
                bestScore = score
            }
        }

        return moved
    }

    private fun moveTilesUp(col: Int): Boolean {
        var moved = false
        for (row in 1 until 4) {
            if (grid[row][col] != null) {
                var targetRow = row
                while (targetRow > 0 &&
                    (grid[targetRow - 1][col] == null ||
                            (grid[targetRow - 1][col]?.value == grid[targetRow][col]?.value &&
                                    grid[targetRow - 1][col]?.mergedFrom == null))) {

                    if (grid[targetRow - 1][col] == null) {

                        grid[targetRow - 1][col] = grid[targetRow][col]
                        grid[targetRow][col] = null
                        targetRow--
                        moved = true
                    } else {

                        val mergedValue = grid[targetRow][col]!!.value * 2
                        grid[targetRow - 1][col] = Tile(mergedValue)
                        grid[targetRow - 1][col]!!.mergedFrom = arrayOf(grid[targetRow - 1][col]!!, grid[targetRow][col]!!)
                        grid[targetRow][col] = null
                        score += mergedValue
                        moved = true
                        break
                    }
                }
            }
        }
        return moved
    }

    private fun moveTilesRight(row: Int): Boolean {
        var moved = false
        for (col in 2 downTo 0) {
            if (grid[row][col] != null) {
                var targetCol = col
                while (targetCol < 3 &&
                    (grid[row][targetCol + 1] == null ||
                            (grid[row][targetCol + 1]?.value == grid[row][targetCol]?.value &&
                                    grid[row][targetCol + 1]?.mergedFrom == null))) {

                    if (grid[row][targetCol + 1] == null) {

                        grid[row][targetCol + 1] = grid[row][targetCol]
                        grid[row][targetCol] = null
                        targetCol++
                        moved = true
                    } else {

                        val mergedValue = grid[row][targetCol]!!.value * 2
                        grid[row][targetCol + 1] = Tile(mergedValue)
                        grid[row][targetCol + 1]!!.mergedFrom = arrayOf(grid[row][targetCol + 1]!!, grid[row][targetCol]!!)
                        grid[row][targetCol] = null
                        score += mergedValue
                        moved = true
                        break
                    }
                }
            }
        }
        return moved
    }

    private fun moveTilesDown(col: Int): Boolean {
        var moved = false
        for (row in 2 downTo 0) {
            if (grid[row][col] != null) {
                var targetRow = row
                while (targetRow < 3 &&
                    (grid[targetRow + 1][col] == null ||
                            (grid[targetRow + 1][col]?.value == grid[targetRow][col]?.value &&
                                    grid[targetRow + 1][col]?.mergedFrom == null))) {

                    if (grid[targetRow + 1][col] == null) {
                        // Перемещаемся в пустую ячейку
                        grid[targetRow + 1][col] = grid[targetRow][col]
                        grid[targetRow][col] = null
                        targetRow++
                        moved = true
                    } else {
                        // Объединяемся с такой же плиткой
                        val mergedValue = grid[targetRow][col]!!.value * 2
                        grid[targetRow + 1][col] = Tile(mergedValue)
                        grid[targetRow + 1][col]!!.mergedFrom = arrayOf(grid[targetRow + 1][col]!!, grid[targetRow][col]!!)
                        grid[targetRow][col] = null
                        score += mergedValue
                        moved = true
                        break
                    }
                }
            }
        }
        return moved
    }

    private fun moveTilesLeft(row: Int): Boolean {
        var moved = false
        for (col in 1 until 4) {
            if (grid[row][col] != null) {
                var targetCol = col
                while (targetCol > 0 &&
                    (grid[row][targetCol - 1] == null ||
                            (grid[row][targetCol - 1]?.value == grid[row][targetCol]?.value &&
                                    grid[row][targetCol - 1]?.mergedFrom == null))) {

                    if (grid[row][targetCol - 1] == null) {

                        grid[row][targetCol - 1] = grid[row][targetCol]
                        grid[row][targetCol] = null
                        targetCol--
                        moved = true
                    } else {

                        val mergedValue = grid[row][targetCol]!!.value * 2
                        grid[row][targetCol - 1] = Tile(mergedValue)
                        grid[row][targetCol - 1]!!.mergedFrom = arrayOf(grid[row][targetCol - 1]!!, grid[row][targetCol]!!)
                        grid[row][targetCol] = null
                        score += mergedValue
                        moved = true
                        break
                    }
                }
            }
        }
        return moved
    }

    private fun addRandomTile() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                if (grid[row][col] == null) {
                    emptyCells.add(Pair(row, col))
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val randomCell = emptyCells[Random.nextInt(emptyCells.size)]
            val value = if (Random.nextFloat() < 0.9f) 2 else 4
            grid[randomCell.first][randomCell.second] = Tile(value)
        }
    }

    private fun checkForWin() {

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                if (grid[row][col]?.value == 2048) {
                    won = true
                    return
                }
            }
        }
    }

    private fun canMove(): Boolean {


        for (row in 0 until 4) {
            for (col in 0 until 4) {
                if (grid[row][col] == null) {
                    return true
                }
            }
        }

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val tile = grid[row][col]

                if (row < 3 && tile?.value == grid[row + 1][col]?.value) {
                    return true
                }
                if (col < 3 && tile?.value == grid[row][col + 1]?.value) {
                    return true
                }
            }
        }

        return false
    }
    private fun resetMergeFlags() {
        for (row in 0 until 4) {
            for (col in 0 until 4) {
                grid[row][col]?.mergedFrom = null
            }
        }
    }
}
