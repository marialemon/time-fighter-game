package com.marianunez.timefighter

//this lines allows you to reuse code that you or others may have written
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.marianunez.timefighter.databinding.ActivityMainBinding
import java.nio.BufferOverflowException

//MainActivity extends AppCompatActivity, this is about inheritance
class MainActivity : AppCompatActivity() {

    //Variables
    private var score = 0

    private var gameStarted = false

    private lateinit var countDownTimer: CountDownTimer

    //Indicates the length of the time
    private var initialCountDown: Long = 20000

    //this is for later to saveInstanceState
    private var timeLeftOnTimer: Long = 20000

    //Indicates the rate at which the time will decrement
    private var countDownInterval: Long = 1000

    //Bind with XML
    private lateinit var binding: ActivityMainBinding

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        /** define the keys that we are be using to identify the values we want to store
         * we need to store the score and the time left
         * the companion object is a handy way to keep identifiers or keys, for all the saved values together
         * the keys are constant strings
         */
        private const val KEY_SCORE = "KEY_SCORE"
        private const val KEY_TIME_LEFT = "KEY_TIME_LEFT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG,"onCreate called. Score is : $score")

        //Con esto evitamos que al inicio se vea la string de %1$d asignando el valor de su var
        binding.scoreText.text = getString(R.string.score, score)
        binding.tapMeButton.setOnClickListener {
            val bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
            it.startAnimation(bounceAnim)
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(KEY_SCORE)
            timeLeftOnTimer = savedInstanceState.getLong(KEY_TIME_LEFT)
            restoreGame()
        } else {
            resetGame()
        }
    }

    //this is to handle menu selections
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout) {
            showInfo()
        }
        return true
    }
    //info in info button
    private fun showInfo(){
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.about_title, BuildConfig.VERSION_NAME))
            setMessage(getString(R.string.about_message))
            create()
            show()
        }
    }
    //

    override fun onSaveInstanceState(outState: Bundle) {
        /**
         * outState is a Bundle.
         * A Bundle is a dictionary that is used by Android to pass values across different Activities.
         * And a dictionary is a list of values where you can identify a specific value via a unique key
         * so in the next lines we are saving the values of our score and time left
         * in the key we just created in the companion object
         */
        outState.putInt(KEY_SCORE, score)
        outState.putLong(KEY_TIME_LEFT, timeLeftOnTimer)

        countDownTimer.cancel()
        Log.d(TAG,"onSaveInstanceState called. Score is : $score and Time left is: $timeLeftOnTimer")
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy called.")
        super.onDestroy()
    }

    private fun resetGame() {
        score = 0
        val initialTimeLeft = initialCountDown / 1000 // this val is to transform milliseconds to seconds
        binding.timeLeftText.text = getString(R.string.timer, initialTimeLeft)
        binding.tapMeButton.text = getString(R.string.start_game)

        // https://developer.android.com/reference/kotlin/android/os/CountDownTimer
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                // this is for savedInstanceState
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                ////

                binding.timeLeftText.text = getString(R.string.timer, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }

    /**
     * this is for saveInstanceState, the difference between this and resetGame is that in this
     * we are using values restored via the passed in savedInstanceState parameter
     * instead of the default values for a new game
     */
    private fun restoreGame(){
        val restoredTime = timeLeftOnTimer / 1000

        binding.apply {
            timeLeftText.text = getString(R.string.timer, restoredTime)
            scoreText.text = getString(R.string.score, score)
        }

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished

                val timeLeft = millisUntilFinished / 1000
                binding.timeLeftText.text = getString(R.string.timer, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        startGame()
    }
    /***/

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
        binding.tapMeButton.text = getString(R.string.tap_me)
    }

    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }
        score++
        val newScore = getString(R.string.score, score)
        binding.scoreText.text = newScore
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.times_up, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

}
