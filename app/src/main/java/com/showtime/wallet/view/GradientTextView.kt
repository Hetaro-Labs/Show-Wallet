package com.showtime.wallet.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class GradientTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var gradientShader: Shader? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gradientShader = LinearGradient(
            0f, 0f, w.toFloat(), 0f,
            intArrayOf(0xFF9945FF.toInt(), 0xFF19FB9B.toInt()), // Replace with your colors
            null,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        paint.shader = gradientShader
        super.onDraw(canvas)
    }
}