package com.example.appdemophotoeditor.`interface`

import android.graphics.Typeface

interface AddTextFragmentListener {
    fun onAddTextButtonClick(typeface: Typeface, text: String, color: Int)
}