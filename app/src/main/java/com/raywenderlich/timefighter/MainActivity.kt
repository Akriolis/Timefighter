package com.raywenderlich.timefighter

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    //private val TAG = MainActivity::class.java.simpleName
    // this tag was used for logging

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button

    private var score: Int = 0

    private var gameStarted = false

    private lateinit var countDownTimer: CountDownTimer

    /**
     * The variable that affect main timing of the game (in milliseconds)
     */
    private var initialCountDown: Long = 15000

    private var countDownInterval: Long = 1000

    private var timeLeft = (initialCountDown.toInt() / 1000)

    private val pulseAnimation by lazy {
        AnimationUtils.loadAnimation(this@MainActivity, R.anim.pulse)
    }

    private val bounceAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.bounce)
    }

    private val rotateAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate)
    }

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
                // there was a bug - when you rotated the screen exactly in moment when score hit 0 -
                // it was led to the infinite loop with 0 score
                timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
                val rotateAnimation: Animation = AnimationUtils.loadAnimation(this,R.anim.rotate)
                gameScoreTextView.startAnimation(rotateAnimation)
                //saving rotate animation from autostart during re-create mainActivity
                restoreGame()
            }else{
                //Log.d(TAG,"if timeLeft is zero, we have to reset it")
                resetGame()
            }
        } else{
            resetGame()
        }

        tapMeButton.setOnClickListener {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.about_item){
            showInfo()
        }
        return true
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d(TAG, "onDestroy called score is $score, timeLeft is $timeLeft.")
//    }

    private fun incrementScore(){
        tapMeButton.text = getString(R.string.tap_me)

        if(!gameStarted){
            startGame()
            gameScoreTextView.startAnimation(rotateAnimation)
            //animation started when game started
            //ended in endGame()
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
        gameScoreTextView.clearAnimation()
    }

    companion object{
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    private fun showInfo(){
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

}