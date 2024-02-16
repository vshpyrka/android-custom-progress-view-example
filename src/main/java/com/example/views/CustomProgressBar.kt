package com.example.views

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.max

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val defaultSize = 100f.dpToPx(context).toInt()

    private val defaultStrokeWidth = 20f.dpToPx(context)

    private val colors = intArrayOf(
        ContextCompat.getColor(context, R.color.purple),
        ContextCompat.getColor(context, R.color.purple),
        ContextCompat.getColor(context, R.color.teal),
        ContextCompat.getColor(context, R.color.blue),
        ContextCompat.getColor(context, R.color.purple),
    )

    var maxValue = 100
        set(value) {
            field = value
            invalidate()
        }

    var progressTextSize = 20f.spToPx(context)
        set(value) {
            field = value
            textPaint.textSize = value
            invalidate()
        }

    val gradientRectF = RectF()

    private val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
        flags = Paint.ANTI_ALIAS_FLAG
        strokeWidth = defaultStrokeWidth
    }

    private val textPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        textAlign = Paint.Align.CENTER
        textSize = progressTextSize
    }

    private var progress: Float = 82.45f

    private var animator: Animator? = null

    fun setProgress(progress: Float) {
        animateProgressChange(this.progress, progress)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        println("View is attached to window")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = resolveSize(defaultSize, widthMeasureSpec)
        val height = resolveSize(defaultSize, heightMeasureSpec)
        val size = max(width, height)

        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gradientRectF.set(0f, 0f, width.toFloat(), height.toFloat())
        val sweepGradient = SweepGradient(
            width.toFloat() / 2,
            height.toFloat() / 2,
            colors,
            null,
        )
        paint.shader = sweepGradient
        textPaint.shader = sweepGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(
            defaultStrokeWidth / 2,
            defaultStrokeWidth / 2,
            width.toFloat() - defaultStrokeWidth / 2,
            height.toFloat() - defaultStrokeWidth / 2,
            -90f,
            ((progress / maxValue) * 360),
            false,
            paint
        )

        val percent = progress / maxValue * 100
        val textValue = String.format("%3.2f%%", percent)
        canvas.drawText(
            textValue,
            0,
            textValue.length,
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            textPaint
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        println("View is detached from window")
        animator?.cancel()
    }

    private fun animateProgressChange(current: Float, to: Float) {
        animator?.cancel()
        ValueAnimator.ofFloat(current, to)
            .apply {
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    progress = it.animatedValue as Float
                    invalidate()
                }
            }
            .also {
                animator = it
            }
            .start()
    }

    private fun Float.dpToPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
        )
    }

    private fun Float.spToPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
        )
    }
}
