/*
 * Copyright (C) 2014 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package helium314.keyboard.keyboard.internal

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import helium314.keyboard.keyboard.Key
import helium314.keyboard.latin.R
import helium314.keyboard.latin.common.isEmoji
import helium314.keyboard.latin.settings.Settings
import helium314.keyboard.latin.utils.dpToPx

open class KeyPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    private val mBackgroundPadding = Rect()
    private val mTypeface: Typeface? = Settings.getInstance().customTypeface

    init {
        gravity = Gravity.CENTER
    }

    fun setPreviewVisual(
        key: Key,
        iconsSet: KeyboardIconsSet,
        drawParams: KeyDrawParams,
        previewWidth: Int = -1,
        bubbleHeight: Int = -1
    ) {
        if (key.iconName != null) {
            setCompoundDrawables(key.getPreviewIcon(iconsSet), null, null, null)
            text = null
            return
        }

        setCompoundDrawables(null, null, null, null)
        setTextColor(drawParams.mPreviewTextColor)
        val baseSize = key.selectPreviewTextSize(drawParams) * Settings.getValues().mFontSizeMultiplier
        val maxSize = if (bubbleHeight > 0) (bubbleHeight - 8.dpToPx(resources)).toFloat() else Float.MAX_VALUE
        val targetSize = if (maxSize > 0f) minOf(baseSize, maxSize) else baseSize
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetSize)
        typeface = mTypeface ?: key.selectPreviewTypeface(drawParams)
        val contentWidth = if (previewWidth > 0) previewWidth - 8.dpToPx(resources) else -1
        setTextAndScaleX(key.previewLabel, contentWidth)
    }

    private fun setTextAndScaleX(text: String?, contentWidth: Int = -1) {
        setTextScaleX(1.0f)
        setText(text)
        if (text == null || sNoScaleXTextSet.contains(text)) {
            return
        }
        if (isEmoji(text)) {
            sNoScaleXTextSet.add(text)
            return
        }
        val maxWidth = if (contentWidth > 0) {
            contentWidth
        } else {
            val background = background ?: return
            background.getPadding(mBackgroundPadding)
            background.intrinsicWidth - mBackgroundPadding.left - mBackgroundPadding.right
        }
        val width = getTextWidth(text, paint)
        if (width <= maxWidth) {
            sNoScaleXTextSet.add(text)
            return
        }
        setTextScaleX(maxWidth.toFloat() / width)
    }

    fun setPreviewBackground(hasPopupKeys: Boolean, position: Int) {
        val background = background ?: return
        val hasPopupKeysState = if (hasPopupKeys) STATE_HAS_POPUPKEYS else STATE_NORMAL
        background.state = KEY_PREVIEW_BACKGROUND_STATE_TABLE[position][hasPopupKeysState]
    }

    fun setPreviewGeometry(
        width: Int,
        height: Int,
        bottomPadding: Int,
        radiusDp: Float,
        colors: helium314.keyboard.latin.common.Colors
    ) {
        val hPad = 4.dpToPx(resources)
        val tPad = 4.dpToPx(resources)
        setPadding(hPad, tPad, hPad, bottomPadding)

        val radiusPx = if (radiusDp >= 0f) {
            radiusDp * resources.displayMetrics.density
        } else {
            5f * resources.displayMetrics.density
        }
        val bg = background
        if (bg != null) {
            colors.applyKeyBorderRadius(bg, radiusPx)
        }

        val widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        measure(widthSpec, heightSpec)
    }

    companion object {
        const val POSITION_MIDDLE = 0
        const val POSITION_LEFT = 1
        const val POSITION_RIGHT = 2

        private val sNoScaleXTextSet = HashSet<String>()

        fun clearTextCache() {
            sNoScaleXTextSet.clear()
        }

        private fun getTextWidth(text: String?, paint: TextPaint): Float {
            if (text.isNullOrEmpty()) return 0.0f
            val len = text.length
            val widths = FloatArray(len)
            val count = paint.getTextWidths(text, 0, len, widths)
            var width = 0.0f
            for (i in 0 until count) {
                width += widths[i]
            }
            return width
        }

        private val KEY_PREVIEW_BACKGROUND_STATE_TABLE = arrayOf(
            arrayOf(
                intArrayOf(),
                intArrayOf(R.attr.state_has_popup_keys)
            ),
            arrayOf(
                intArrayOf(R.attr.state_left_edge),
                intArrayOf(R.attr.state_left_edge, R.attr.state_has_popup_keys)
            ),
            arrayOf(
                intArrayOf(R.attr.state_right_edge),
                intArrayOf(R.attr.state_right_edge, R.attr.state_has_popup_keys)
            )
        )

        private const val STATE_NORMAL = 0
        private const val STATE_HAS_POPUPKEYS = 1
    }
}
