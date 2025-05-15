package com.example.game2048

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var scoreTextView: TextView
    private lateinit var bestScoreTextView: TextView
    private lateinit var newGameButton: Button
    private lateinit var gameLogic: GameLogic
    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)
        scoreTextView = findViewById(R.id.scoreTextView)
        bestScoreTextView = findViewById(R.id.bestScoreTextView)
        newGameButton = findViewById(R.id.newGameButton)

        gameLogic = GameLogic(this)
        gameView.setGameLogic(gameLogic)

        val sharedPref = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val bestScore = sharedPref.getInt("best_score", 0)
        gameLogic.setBestScore(bestScore)
        updateScores()

        newGameButton.setOnClickListener {
            gameLogic.startNewGame()
            updateScores()
            gameView.invalidate()
        }

        gestureDetector = GestureDetectorCompat(this, GameGestureListener())

        gameLogic.startNewGame()
        updateScores()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun updateScores() {
        scoreTextView.text = getString(R.string.score, gameLogic.getScore())
        bestScoreTextView.text = getString(R.string.best_score, gameLogic.getBestScore())
    }

    override fun onPause() {
        super.onPause()
        // Сохраняем лучший результат
        val sharedPref = getSharedPreferences("game_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("best_score", gameLogic.getBestScore())
            apply()
        }
    }

    inner class GameGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

         override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            if (e1 == null || e2 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (abs(diffX) > abs(diffY)) {

                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {

                        if (gameLogic.move(GameLogic.Direction.RIGHT)) {
                            updateScores()
                            gameView.invalidate()
                        }
                    } else {

                        if (gameLogic.move(GameLogic.Direction.LEFT)) {
                            updateScores()
                            gameView.invalidate()
                        }
                    }
                }
            } else {

                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {

                        if (gameLogic.move(GameLogic.Direction.DOWN)) {
                            updateScores()
                            gameView.invalidate()
                        }
                    } else {

                        if (gameLogic.move(GameLogic.Direction.UP)) {
                            updateScores()
                            gameView.invalidate()
                        }
                    }
                }
            }
            return true
        }
    }
}