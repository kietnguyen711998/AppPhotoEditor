package com.example.appdemophotoeditor.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appdemophotoeditor.R
import com.example.appdemophotoeditor.`interface`.FiltersListFragmentListener
import com.zomato.photofilters.imageprocessors.Filter

class FiltersListFragment : Fragment(), FiltersListFragmentListener {
    var listener: FiltersListFragmentListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters_list, container, false)
    }

    override fun onFilterSelected(filter: Filter) {
        TODO("Not yet implemented")
    }

    companion object {
        var instance: FiltersListFragment? = null
        var bitmap: Bitmap? = null
        fun getInstance(bitmapSave: Bitmap?): FiltersListFragment? {
            bitmap = bitmapSave
            if (instance == null) {
                instance = FiltersListFragment()
            }
            return instance
        }

        fun setListener(
            filtersListFragment: FiltersListFragment,
            listener: FiltersListFragmentListener?
        ) {
            filtersListFragment.listener = listener
        }
    }



    /**
     *
     */
}