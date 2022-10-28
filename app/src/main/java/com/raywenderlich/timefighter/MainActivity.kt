package com.raywenderlich.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    //private val TAG = MainActivity::class.java.simpleName

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button

    private var score: Int = 0

    private var gameStarted = false

    private lateinit var countDownTimer: CountDownTimer

    /**
     * The variable that affect main timing of the game (in milliseconds)
     */
    private var initialCountDown: Long = 10000

    private var countDownInterval: Long = 1000

    private var timeLeft = (initialCountDown.toInt() / 1000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Log.d(TAG,"onCreate called. Score is: $score")
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)

        if (savedInstanceState != null){
            //checking for existing savedInstanceState
            tapMeButton.text = getString(R.string.tap_me)
            score = savedInstanceState.getInt(SCORE_KEY)
            if (savedInstanceState.getInt(TIME_LEFT_KEY) != 0) {
                // there was a bug - when you rotated the screen exactly in moment when score hit 0
                // it was led to the infinite loop with 0 score
                timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
                restoreGame()
            }else{
                //Log.d(TAG,"if timeLeft is zero, we have to reset it")
                resetGame()
            }
        } else{
            resetGame()
        }

        tapMeButton.setOnClickListener {
            val bounceAnimation = AnimationUtils.loadAnimation(this,R.anim.bounce)
            it.startAnimation(bounceAnimation)
            incrementScore()
        }
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        if (score != 0 && timeLeft != ((initialCountDown.toInt()) / 1000)) {
            //to prevent from saving default values as InstanceState data
            outState.putInt(SCORE_KEY, score)
            outState.putInt(TIME_LEFT_KEY, timeLeft)
            countDownTimer.cancel()
            //Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time left $timeLeft")
        }

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d(TAG, "onDestroy called score is $score, timeLeft is $timeLeft.")
//    }

    private fun incrementScore(){
        tapMeButton.text = getString(R.string.tap_me)
        if(!gameStarted){
            startGame()
        }

        score++

        gameScoreTextView.text = getString(R.string.your_score,score)
    }

    private fun resetGame(){
        tapMeButton.text = getString(R.string.tap_to_start)

        score = 0

        gameScoreTextView.text = getString(R.string.your_score, score)

        timeLeftTextView.text = getString(R.string.time_left,timeLeft)

        countDownTimer = object : CountDownTimer(initialCountDown,countDownInterval){

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000

                timeLeftTextView.text = getString(R.string.time_left,timeLeft)

                if (timeLeft <= 5) {

                    val pulseAnimation: Animation =
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.pulse)
                    timeLeftTextView.startAnimation(pulseAnimation)
                }
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false

    }

    private fun restoreGame(){
        gameScoreTextView.text = getString(R.string.your_score,score)

        timeLeftTextView.text = getString(R.string.time_left, timeLeft)

        countDownTimer = object: CountDownTimer((timeLeft * 1000).toLong(), countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000

                timeLeftTextView.text = getString(R.string.time_left,timeLeft)

                if (timeLeft <= 5) {

                    val pulseAnimation: Animation =
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.pulse)
                    timeLeftTextView.startAnimation(pulseAnimation)
                }
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.game_over_message,score),Toast.LENGTH_LONG).show()
        resetGame()
        tapMeButton.text = getString(R.string.tap_to_start)
    }

    companion object{
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }


}