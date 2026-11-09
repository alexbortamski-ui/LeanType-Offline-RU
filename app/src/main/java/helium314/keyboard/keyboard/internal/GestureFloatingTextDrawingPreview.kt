/*
 * Copyright (C) 2012 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package helium314.keyboard.keyboard.internal

import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextUtils
import helium314.keyboard.keyboard.Key
import helium314.keyboard.keyboard.PointerTracker
import helium314.keyboard.latin.R
import helium314.keyboard.latin.SuggestedWords
import helium314.keyboard.latin.common.ColorType
import helium314.keyboard.latin.common.CoordinateUtils
import helium314.keyboard.latin.settings.Settings
import kotlin.math.max
import kotlin.math.min

open class GestureFloatingTextDrawingPreview(mainKeyboardViewAttr: TypedArray) : AbstractDrawingPreview() {

    protected class GesturePreviewTextParams(mainKeyboardViewAttr: TypedArray) {
        val mGesturePreviewDynamic: Boolean
        val mGesturePreviewTextOffset: Int
        val mGesturePreviewTextHeight: Int
        val mGesturePreviewHorizontalPadding: Float
        val mGesturePreviewVerticalPadding: Float
        val mGesturePreviewRoundRadius: Float
        val mDisplayWidth: Int
        val mDisplayDensity: Float

        private val mGesturePreviewTextSize: Int
        private val mGesturePreviewTextColor: Int
        private val mGesturePreviewColor: Int
        private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            val colors = Settings.getValues().mColors
            mGesturePreviewDynamic = Settings.getValues().mGestureFloatingPreviewDynamicEnabled
            mGesturePreviewTextSize = mainKeyboardViewAttr.getDimensionPixelSize(
                R.styleable.MainKeyboardView_gestureFloatingPreviewTextSize, 0)
            mGesturePreviewTextColor = colors.get(ColorType.KEY_TEXT)
            mGesturePreviewTextOffset = mainKeyboardViewAttr.getDimensionPixelOffset(
                R.styleable.MainKeyboardView_gestureFloatingPreviewTextOffset, 0)
            mGesturePreviewColor = colors.get(ColorType.GESTURE_PREVIEW)
            mGesturePreviewHorizontalPadding = mainKeyboardViewAttr.getDimension(
                R.styleable.MainKeyboardView_gestureFloatingPreviewHorizontalPadding, 0.0f)
            mGesturePreviewVerticalPadding = mainKeyboardViewAttr.getDimension(
                R.styleable.MainKeyboardView_gestureFloatingPreviewVerticalPadding, 0.0f)
            mGesturePreviewRoundRadius = mainKeyboardViewAttr.getDimension(
                R.styleable.MainKeyboardView_gestureFloatingPreviewRoundRadius, 0.0f)
            mDisplayWidth = mainKeyboardViewAttr.resources.displayMetrics.widthPixels
            mDisplayDensity = mainKeyboardViewAttr.resources.displayMetrics.density

            mTextPaint.textAlign = Paint.Align.CENTER
            mTextPaint.textSize = mGesturePreviewTextSize.toFloat()
            mTextPaint.typeface = Settings.getInstance().customTypeface
            mTextPaint.color = mGesturePreviewTextColor

            mBackgroundPaint.color = mGesturePreviewColor

            val textRect = Rect()
            mTextPaint.getTextBounds(TEXT_HEIGHT_REFERENCE_CHAR, 0, 1, textRect)
            mGesturePreviewTextHeight = textRect.height()
        }

        val textPaint: Paint
            get() {
                mTextPaint.typeface = Settings.getInstance().customTypeface
                mTextPaint.color = mGesturePreviewTextColor
                return mTextPaint
            }

        val backgroundPaint: Paint
            get() {
                mBackgroundPaint.color = mGesturePreviewColor
                return mBackgroundPaint
            }

        companion object {
            private val TEXT_HEIGHT_REFERENCE_CHAR = charArrayOf('M')
        }
    }

    private val mParams = GesturePreviewTextParams(mainKeyboardViewAttr)
    private val mGesturePreviewRectangle = RectF()
    private var mPreviewTextX = 0
    private var mPreviewTextY = 0
    private var mSuggestedWords: SuggestedWords = SuggestedWords.getEmptyInstance()
    private val mLastPointerCoords: IntArray = CoordinateUtils.newInstance()
    private var mLastKey: Key? = null
    private var mKeyboardHeight = 0

    override fun onDeallocateMemory() {
        mLastKey = null
    }

    override fun setKeyboardViewGeometry(originCoords: IntArray, width: Int, height: Int) {
        super.setKeyboardViewGeometry(originCoords, width, height)
        mKeyboardHeight = height
    }

    fun dismissGestureFloatingPreviewText() {
        setSuggestedWords(SuggestedWords.getEmptyInstance())
        mLastKey = null
    }

    fun setSuggestedWords(suggestedWords: SuggestedWords) {
        if (!isPreviewEnabled) return
        mSuggestedWords = suggestedWords
        updatePreviewPosition()
    }

    override fun setPreviewPosition(tracker: PointerTracker) {
        if (!isPreviewEnabled) return
        tracker.getLastCoordinates(mLastPointerCoords)
        mLastKey = tracker.key ?: tracker.getKeyOn(CoordinateUtils.x(mLastPointerCoords), CoordinateUtils.y(mLastPointerCoords))
        updatePreviewPosition()
    }

    override fun drawPreview(canvas: Canvas) {
        if (!isPreviewEnabled || mSuggestedWords.isEmpty || TextUtils.isEmpty(mSuggestedWords.getWord(0))) {
            return
        }
        val maxRound = mGesturePreviewRectangle.height() / 2.0f
        val previewRadius = Settings.getValues().mKeyPreviewRadius * mParams.mDisplayDensity
        val round = if (previewRadius >= 0f) {
            min(previewRadius, maxRound)
        } else if (mParams.mGesturePreviewRoundRadius > 0.0f) {
            min(mParams.mGesturePreviewRoundRadius, maxRound)
        } else {
            maxRound
        }
        canvas.drawRoundRect(mGesturePreviewRectangle, round, round, mParams.backgroundPaint)
        val text = mSuggestedWords.getWord(0)
        canvas.drawText(text, mPreviewTextX.toFloat(), mPreviewTextY.toFloat(), mParams.textPaint)
    }

    protected fun updatePreviewPosition() {
        if (mSuggestedWords.isEmpty || TextUtils.isEmpty(mSuggestedWords.getWord(0))) {
            invalidateDrawingView()
            return
        }
        val text = mSuggestedWords.getWord(0)

        val textHeight = mParams.mGesturePreviewTextHeight
        val textWidth = mParams.textPaint.measureText(text)
        val hPad = mParams.mGesturePreviewHorizontalPadding
        val vPad = mParams.mGesturePreviewVerticalPadding
        val rectWidth = textWidth + hPad * 2.0f
        val rectHeight = textHeight + vPad * 2.0f

        val rectX = if (mParams.mGesturePreviewDynamic) {
            min(max(CoordinateUtils.x(mLastPointerCoords).toFloat() - rectWidth / 2.0f, 0.0f), mParams.mDisplayWidth.toFloat() - rectWidth)
        } else {
            (mParams.mDisplayWidth.toFloat() - rectWidth) / 2.0f
        }
        val keyboardHeight = mKeyboardHeight.takeIf { it > 0 }
            ?: (230 * mParams.mDisplayDensity).toInt()
        val verticalOffsetPx = (keyboardHeight * Settings.getValues().mPopupKeysVerticalOffsetFraction).toInt()

        val defaultKeyHeight = (keyboardHeight / 4).coerceAtLeast((40 * mParams.mDisplayDensity).toInt())
        val keyHeight = (mLastKey?.height ?: defaultKeyHeight).toFloat()
        val popupOffset = (keyHeight * 0.5f) + verticalOffsetPx

        val rectY = if (mParams.mGesturePreviewDynamic) {
            CoordinateUtils.y(mLastPointerCoords).toFloat() - popupOffset - rectHeight
        } else {
            -verticalOffsetPx.toFloat() - rectHeight
        }
        mGesturePreviewRectangle.set(rectX, rectY, rectX + rectWidth, rectY + rectHeight)

        mPreviewTextX = (rectX + rectWidth / 2.0f).toInt()
        val fontMetrics = mParams.textPaint.fontMetrics
        mPreviewTextY = (rectY + rectHeight / 2.0f - (fontMetrics.ascent + fontMetrics.descent) / 2.0f).toInt()
        invalidateDrawingView()
    }
}
