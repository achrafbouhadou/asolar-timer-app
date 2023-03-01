package com.exo.pomodoro.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel(){
     var number = 1
    var Text = "00:20:00"
    val currentNumber: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }
    val currentText: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
}