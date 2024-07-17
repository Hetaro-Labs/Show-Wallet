package com.showtime.wallet.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

/**
 * Set view display
 */
fun View.visible() {
    visibility = View.VISIBLE
}


/**
 * Set view placeholder hiding
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * According to the conditions, set the view display to hide as true and hide as false
 */
fun View.visibleOrGone(flag: Boolean) {
    visibility = if (flag) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * According to the conditions, set the view display to hide as true and hide as false
 */
fun View.visibleOrInvisible(flag: Boolean) {
    visibility = if (flag) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

/**
 * Set view to hide
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Set view placeholder hiding
 */

fun isEmpty(view: TextView): Boolean {
    return TextUtils.isEmpty(view.text.toString())
}

/**
 * Convert view to bitmap
 */
@Deprecated("use View.drawToBitmap()") fun View.toBitmap(scale: Float = 1f,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely((width * scale).toInt(), (height * scale).toInt(), config, 1)
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }
}


/**
 * Prevent duplicate click events, default to no duplicate clicks within 0.5 seconds
 * @param interval The default time interval is 0.5 seconds
 * @param action Execution method
 */
var lastClickTime = 0L
fun View.clickNoRepeat(interval: Long = 500, action: (view: View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}


fun Any?.notNull(notNullAction: (value: Any) -> Unit, nullAction1: () -> Unit) {
    if (this != null) {
        notNullAction.invoke(this)
    } else {
        nullAction1.invoke()
    }
}

/**
 * Set underline in text
 *
 * @param textView
 */
fun AppCompatTextView.setTextMiddleLine() {
    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG //strikethrough
    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG // Set the center line and add it clearly
    paint.isAntiAlias = true
}

fun TextView.addTextChangeListener(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // What to do before changing the text
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            // What to do after text changes
        }
    })
}
