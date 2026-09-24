package com.pixellegolas.fujicam
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random
class HistogramView @JvmOverloads constructor(c: Context, a: AttributeSet?=null) : View(c,a) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
    private val bgPaint = Paint().apply { color = Color.parseColor("#1A1A1A") }
    private var bars = List(32) { Random.nextFloat() }
    fun bump() { bars = List(32) { Random.nextFloat() * 0.8f + 0.2f }; invalidate() }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(0f,0f,width.toFloat(),height.toFloat(),8f,8f,bgPaint)
        val bw = width / bars.size.toFloat()
        bars.forEachIndexed { i, v ->
            val h = v * height * 0.8f
            canvas.drawRect(i*bw+1, height - h, (i+1)*bw-1, height.toFloat()-2, paint)
        }
    }
}