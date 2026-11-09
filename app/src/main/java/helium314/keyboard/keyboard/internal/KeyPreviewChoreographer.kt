/*
 * Copyright (C) 2014 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package helium314.keyboard.keyboard.internal

import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import helium314.keyboard.keyboard.Key
import helium314.keyboard.latin.common.ColorType
import helium314.keyboard.latin.common.CoordinateUtils
import helium314.keyboard.latin.settings.Settings
import helium314.keyboard.latin.utils.ViewLayoutUtils
import helium314.keyboard.latin.utils.dpToPx
import java.util.ArrayDeque
import java.util.HashMap
import kotlin.math.roundToInt

private val PREVIEW_EXIT_INTERPOLATOR = PathInterpolator(0.3f, 0f, 0.8f, 0.15f)

class KeyPreviewChoreographer(private val mParams: KeyPreviewDrawParams) {
    private val mFreeKeyPreviewViews = ArrayDeque<KeyPreviewView>()
    private val mShowingKeyPreviewViews = HashMap<Key, KeyPreviewView>()
    private val mDismissingKeyPreviewViews = HashSet<KeyPreviewView>()

    fun getKeyPreviewView(key: Key, placerView: ViewGroup): KeyPreviewView {
        val showingView = mShowingKeyPreviewViews.remove(key)
        if (showingView != null) {
            showingView.animate().cancel()
            mDismissingKeyPreviewViews.remove(showingView)
            return showingView
        }

        val freeView = mFreeKeyPreviewViews.poll()
        if (freeView != null) {
            freeView.animate().cancel()
            mDismissingKeyPreviewViews.remove(freeView)
            return freeView
        }

        val context = placerView.context
        val newView = KeyPreviewView(context, null)
        newView.setBackgroundResource(mParams.mPreviewBackgroundResId)
        placerView.addView(newView, ViewLayoutUtils.newLayoutParam(placerView, 0, 0))
        return newView
    }

    fun isShowingKeyPreview(key: Key?): Boolean {
        return key != null && mShowingKeyPreviewViews.containsKey(key)
    }

    fun dismissKeyPreview(key: Key?) {
        if (key == null) return
        val duration = Settings.getAnimationDuration(35)
        if (duration == 0L) {
            dismissKeyPreviewWithoutDelay(key)
            return
        }
        val keyPreviewView = mShowingKeyPreviewViews.remove(key) ?: return
        mDismissingKeyPreviewViews.add(keyPreviewView)

        keyPreviewView.tag = null
        keyPreviewView.animate().cancel()
        val safetyRunnable = Runnable {
            if (mDismissingKeyPreviewViews.remove(keyPreviewView)) {
                keyPreviewView.visibility = View.INVISIBLE
                keyPreviewView.scaleX = 1f
                keyPreviewView.scaleY = 1f
                keyPreviewView.translationY = 0f
                keyPreviewView.alpha = 1f
                mFreeKeyPreviewViews.add(keyPreviewView)
            }
        }
        keyPreviewView.postDelayed(safetyRunnable, duration + 20L)
        keyPreviewView.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(PREVIEW_EXIT_INTERPOLATOR)
            .withEndAction {
                keyPreviewView.removeCallbacks(safetyRunnable)
                safetyRunnable.run()
            }
            .start()
    }

    fun dismissKeyPreviewWithoutDelay(key: Key?) {
        if (key == null) return
        val keyPreviewView = mShowingKeyPreviewViews.remove(key) ?: return
        mDismissingKeyPreviewViews.remove(keyPreviewView)
        keyPreviewView.tag = null
        keyPreviewView.animate().cancel()
        keyPreviewView.visibility = View.INVISIBLE
        keyPreviewView.scaleX = 1f
        keyPreviewView.scaleY = 1f
        keyPreviewView.translationY = 0f
        keyPreviewView.alpha = 1f
        mFreeKeyPreviewViews.add(keyPreviewView)
    }

    fun placeAndShowKeyPreview(
        key: Key,
        iconsSet: KeyboardIconsSet,
        drawParams: KeyDrawParams,
        fullKeyboardViewWidth: Int,
        keyboardOrigin: IntArray,
        placerView: ViewGroup
    ) {
        val keyPreviewView = getKeyPreviewView(key, placerView)
        placeKeyPreview(key, keyPreviewView, iconsSet, drawParams, fullKeyboardViewWidth, keyboardOrigin)
        showKeyPreview(key, keyPreviewView)
    }

    private fun placeKeyPreview(
        key: Key,
        keyPreviewView: KeyPreviewView,
        iconsSet: KeyboardIconsSet,
        drawParams: KeyDrawParams,
        fullKeyboardViewWidth: Int,
        originCoords: IntArray
    ) {
        val settingsValues = Settings.getValues()
        val widthScale = settingsValues.mKeyPreviewWidthScale
        val heightScale = settingsValues.mKeyPreviewHeightScale
        val radiusDp = settingsValues.mKeyPreviewRadius

        val keyDrawWidth = key.drawWidth
        val keyHeight = key.height

        val stemHeight = keyHeight
        val bubbleHeight = (keyHeight * 0.85f * heightScale).roundToInt().coerceAtLeast(18.dpToPx(keyPreviewView.resources))
        val previewHeight = stemHeight + bubbleHeight
        val previewWidth = (keyDrawWidth * widthScale).roundToInt().coerceAtLeast(24.dpToPx(keyPreviewView.resources))

        keyPreviewView.setPreviewVisual(key, iconsSet, drawParams, previewWidth, bubbleHeight)

        val minX = CoordinateUtils.x(originCoords)
        val maxX = minX + fullKeyboardViewWidth - previewWidth
        val keyPreviewPosition: Int
        var previewX = key.drawX - (previewWidth - keyDrawWidth) / 2 + minX
        if (previewX < minX) {
            previewX = minX
            keyPreviewPosition = KeyPreviewView.POSITION_LEFT
        } else if (previewX > maxX) {
            previewX = maxX
            keyPreviewPosition = KeyPreviewView.POSITION_RIGHT
        } else {
            keyPreviewPosition = KeyPreviewView.POSITION_MIDDLE
        }

        val hasPopupKeys = key.popupKeys != null
        keyPreviewView.setPreviewBackground(hasPopupKeys, keyPreviewPosition)
        val colors = settingsValues.mColors
        colors.setBackground(keyPreviewView, ColorType.KEY_PREVIEW_BACKGROUND)
        keyPreviewView.setPreviewGeometry(previewWidth, previewHeight, stemHeight, radiusDp, colors)
        mParams.setGeometry(keyPreviewView)

        val previewY = key.y - previewHeight + key.height - mParams.mPreviewOffset + CoordinateUtils.y(originCoords)

        ViewLayoutUtils.placeViewAt(keyPreviewView, previewX, previewY, previewWidth, previewHeight)
        val keyCenterX = key.drawX + keyDrawWidth / 2.0f + minX
        keyPreviewView.pivotX = (keyCenterX - previewX).coerceIn(0f, previewWidth.toFloat())
        keyPreviewView.pivotY = previewHeight.toFloat()
    }

    private fun showKeyPreview(key: Key, keyPreviewView: KeyPreviewView) {
        keyPreviewView.visibility = View.VISIBLE
        keyPreviewView.animate().cancel()
        keyPreviewView.scaleX = 1f
        keyPreviewView.scaleY = 1f
        keyPreviewView.translationY = 0f
        keyPreviewView.alpha = 1f
        mShowingKeyPreviewViews[key] = keyPreviewView
    }

    fun clear() {
        for (view in mShowingKeyPreviewViews.values) {
            view.animate().cancel()
            view.visibility = View.INVISIBLE
            view.scaleX = 1f
            view.scaleY = 1f
            view.translationY = 0f
            view.alpha = 1f
        }
        for (view in mDismissingKeyPreviewViews) {
            view.animate().cancel()
            view.visibility = View.INVISIBLE
            view.scaleX = 1f
            view.scaleY = 1f
            view.translationY = 0f
            view.alpha = 1f
        }
        mShowingKeyPreviewViews.clear()
        mDismissingKeyPreviewViews.clear()
        mFreeKeyPreviewViews.clear()
    }
}
