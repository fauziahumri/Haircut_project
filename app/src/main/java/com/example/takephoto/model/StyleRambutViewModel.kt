package com.example.takephoto.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StyleRambutViewModel : ViewModel() {
    var list_style = MutableLiveData<ArrayList<StyleRambut>>()

    init{
        list_style.value = ArrayList<StyleRambut>()
    }
}