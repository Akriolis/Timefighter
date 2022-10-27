package com.raywenderlich.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button
    private var score = 0

    private var gameStarted = false

    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 10000
    private var countDownInterval: Long = 1000
    private var timeLeft = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)

        tapMeButton.setOnClickListener { incrementScore() }

        resetGame()
    }

    private fun incrementScore(){
        if(!gameStarted){
            startGame()
        }

        score++

        gameScoreTextView.text = getString(R.string.your_score,score)
    }

    private fun resetGame(){
        score = 0

        gameScoreTextView.text = getString(R.string.your_score,score)

        timeLeftTextView.text = getString(R.string.time_left,timeLeft)

        countDownTimer = object : CountDownTimer(initialCountDown,countDownInterval){

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000

                timeLeftTextView.text = getString(R.string.time_left,timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.game_over_message,score),Toast.LENGTH_LONG).show()
        resetGame()

    }

}