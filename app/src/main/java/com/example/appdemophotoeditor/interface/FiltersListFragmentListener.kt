package com.example.appdemophotoeditor.`interface`

import android.content.Intent
import com.zomato.photofilters.imageprocessors.Filter

interface FiltersListFragmentListener {
    fun onFilterSelected(filter: Filter)
}