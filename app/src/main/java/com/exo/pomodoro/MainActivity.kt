package com.exo.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.exo.pomodoro.databinding.ActivityMainBinding
import com.exo.pomodoro.viewModel.MainViewModel
import java.time.Duration
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.currentNumber.observe(this , Observer {
            binding.etRound.text = it.toString()
            val TotalTime = viewModel.currentNumber.value?.times(20)
            if (TotalTime != null) {
                binding.text2.text = LocalTime.MIN.plus(
                    Duration.ofMinutes(  TotalTime.toLong())
                ).toString()+":00"
            }
        })


        binding.incrase.setOnClickListener{
            if (viewModel.number < 20){

                viewModel.currentNumber.value = ++viewModel.number
                println(viewModel.number)
            }else{
                viewModel.currentNumber.value = viewModel.number
            }

        }

        binding.decrase.setOnClickListener{
            if(viewModel.number > 1){
                viewModel.currentNumber.value = --viewModel.number
            }else{
                viewModel.currentNumber.value = viewModel.number
            }

        }

        binding.tvStart.setOnClickListener {
            val studyTime = 20 //binding.etStudy.text.toString()
            val breakTime = 0 //binding.etBreak.text.toString()
            val roundCount = binding.etRound.text.toString()


                val intent = Intent(this,FeedActivity::class.java)
                intent.putExtra("study",studyTime.toInt())
                intent.putExtra("break",breakTime.toInt())
                intent.putExtra("round",roundCount.toInt())
                startActivity(intent)


        }

    }


}