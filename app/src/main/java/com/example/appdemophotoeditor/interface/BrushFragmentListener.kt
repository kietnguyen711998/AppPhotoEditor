package com.example.appdemophotoeditor.`interface`

interface BrushFragmentListener {
    fun onBrushSizeChangeListener(size: Float)
    fun onBrushOpacityChangeListener(opacity: Int)
    fun onBrushColorChangeListener(color: Int)
    fun onBrushStateChangeListener(isEraser: Boolean)
}