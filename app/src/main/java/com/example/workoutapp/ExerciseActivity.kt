package com.example.workoutapp

import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.databinding.ActivityExcerciseBinding
import com.example.workoutapp.databinding.BackDialogBinding
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding : ActivityExcerciseBinding? = null

    private var timer : CountDownTimer? = null
    private var progressTimer = 0

    private var timer2 : CountDownTimer? = null
    private var progressTimer2 = 0

    private var exercises : ArrayList<ExerciseModel>? = null
    var currentExercise = -1

    private var tts : TextToSpeech? = null
    private var player : MediaPlayer? = null

    private var exerciseAdapter : ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //For the toolbar
        setSupportActionBar(binding?.toolbarExercise)

        //for back button
        if(supportActionBar!=null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.toolbarExercise?.setNavigationOnClickListener {
            showBackDialog()
        }

        exercises = Constants.getExercises()
        tts = TextToSpeech(this,this)

        setUpTimer()
        setUpRecyclerView()

    }

    override fun onBackPressed() {
        showBackDialog()
    }

    private fun showBackDialog(){
        val customDialog = Dialog(this)
        val dialogBinding = BackDialogBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        dialogBinding.tvYes.setOnClickListener{
            customDialog.dismiss()
            this@ExerciseActivity.finish()
        }
        dialogBinding.tvNo.setOnClickListener{
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setUpRecyclerView(){
        binding?.rvExcerciseStatus?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exercises!!)
        binding?.rvExcerciseStatus?.adapter = exerciseAdapter
    }

    private fun setUpTimer(){
        try{
            val soundURI = Uri.parse(
                "android.resource://com.example.workoutapp/" + R.raw.app_src_main_res_raw_press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e :Exception){
            e.printStackTrace()
        }

        binding?.frameLayout?.visibility = View.VISIBLE
        binding?.textView?.visibility = View.VISIBLE
        binding?.tvExercise?.visibility = View.INVISIBLE
        binding?.frameLayout2?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        if(timer!=null){
            timer!!.cancel()
            progressTimer=0
        }
        startTimer()
    }
    private fun setUpTimer2(){
        binding?.frameLayout?.visibility = View.INVISIBLE
        binding?.textView?.visibility = View.INVISIBLE
        binding?.tvExercise?.visibility = View.VISIBLE
        binding?.frameLayout2?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        if(timer2!=null){
            timer2!!.cancel()
            progressTimer2=0
        }

        textToSpeech(exercises!! [currentExercise].getName())

        binding?.ivImage?.setImageResource(exercises!![currentExercise].getImage())
        binding?.tvExercise?.text = exercises!![currentExercise].getName()

        startTimer2()
    }

    private fun startTimer(){
        timer = object : CountDownTimer(1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                progressTimer++
                binding?.progressBar?.progress = 10-progressTimer
                binding?.tvTimer?.text = (10-progressTimer).toString()
            }

            override fun onFinish() {
                currentExercise++
                exercises!![currentExercise].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpTimer2()
            }
        }.start()
    }

    private fun startTimer2(){
        timer2 = object : CountDownTimer(3000,1000){
            override fun onTick(millisUntilFinished: Long) {
                progressTimer2++
                binding?.progressBar2?.progress = 30-progressTimer2
                binding?.tvTimer2?.text = (30-progressTimer2).toString()
            }

            override fun onFinish() {
                exercises!![currentExercise].setCompleted(true)
                exercises!![currentExercise].setIsSelected(false)
                exerciseAdapter!!.notifyDataSetChanged()
                if(currentExercise < exercises?.size!! - 1){
                    setUpTimer()
                }else{
                    Toast.makeText(this@ExerciseActivity,"Congrats!!",Toast.LENGTH_SHORT).show()
                    val historyDao = (application as WorkoutApp).db.historyDao()
                    addDateToDatabase(historyDao)
                }
            }
        }.start()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The language specified is not supported")
            }
        }else{
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun textToSpeech(text: String){
        tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }


    override fun onDestroy() {
        super.onDestroy()
        if(timer!=null){
            timer!!.cancel()
            progressTimer=0
        }
        if(timer2!=null){
            timer2!!.cancel()
            progressTimer2=0
        }
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null){
            player!!.stop()
        }
        binding = null
    }

    private fun addDateToDatabase(historyDao: HistoryDao){
        val c = Calendar.getInstance()
        val dateTime = c.time
        Log.e("Date:",""+dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        Log.e("Formatted Date :",""+date)

        lifecycleScope.launch{
            historyDao.insert(HistoryEntity(date))
            Log.e("Date :","Added...")
        }
    }
}