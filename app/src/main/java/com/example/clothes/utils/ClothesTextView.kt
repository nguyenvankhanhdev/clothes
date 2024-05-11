package com.example.clothes.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ClothesTextView(context: Context, attributeSet: AttributeSet): AppCompatTextView(context, attributeSet) {
    init {
        applyFont();

    }
    private fun applyFont() {
        val normalTypeface: Typeface = Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(normalTypeface)

    }
}