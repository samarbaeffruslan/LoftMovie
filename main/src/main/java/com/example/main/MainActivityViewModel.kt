package com.example.main

import androidx.lifecycle.ViewModel
import com.example.data.MainRepository
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {


}