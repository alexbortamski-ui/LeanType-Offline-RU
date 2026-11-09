// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.settings.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import helium314.keyboard.latin.R
import helium314.keyboard.settings.Theme
import helium314.keyboard.settings.previewDark
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SliderDialog(
    onDismissRequest: () -> Unit,
    onDone: (Float) -> Unit,
    initialValue: Float,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    showDefault: Boolean = false,
    onDefault: () -> Unit = { },
    onValueChanged: (Float) -> Unit = { },
    title: (@Composable () -> Unit)? = null,
    intermediateSteps: Int? = null,
    positionString: (@Composable (Float) -> String) = { it.toString() },
) {
    val step = if (intermediateSteps != null && intermediateSteps >= 0) {
        val calculated = (range.endInclusive - range.start) / (intermediateSteps + 1)
        if (abs(calculated - calculated.roundToInt()) < 0.001f) calculated.roundToInt().toFloat()
        else calculated
    } else {
        (range.endInclusive - range.start) / 100f
    }
    fun snapToStep(value: Float): Float {
        val stepsCount = ((value - range.start) / step).roundToInt()
        return (range.start + stepsCount * step).coerceIn(range.start, range.endInclusive)
    }

    var sliderPosition by remember { mutableFloatStateOf(snapToStep(initialValue)) }

    ThreeButtonAlertDialog(
        onDismissRequest = onDismissRequest,
        neutralButtonText = if (showDefault) stringResource(R.string.button_default) else null,
        onNeutral = { onDismissRequest(); onDefault() },
        onConfirmed = { onDone(sliderPosition) },
        modifier = modifier,
        title = title,
        content = {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodyLarge
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                sliderPosition = snapToStep(sliderPosition - step)
                                onValueChanged(sliderPosition)
                            },
                            enabled = sliderPosition > range.start
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_minus),
                                contentDescription = "Decrease"
                            )
                        }
                        if (intermediateSteps == null) {
                            Slider(
                                value = sliderPosition,
                                onValueChange = { sliderPosition = snapToStep(it) },
                                onValueChangeFinished = { onValueChanged(sliderPosition) },
                                valueRange = range,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Slider(
                                value = sliderPosition,
                                onValueChange = { sliderPosition = snapToStep(it) },
                                onValueChangeFinished = { onValueChanged(sliderPosition) },
                                valueRange = range,
                                steps = intermediateSteps,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        IconButton(
                            onClick = {
                                sliderPosition = snapToStep(sliderPosition + step)
                                onValueChanged(sliderPosition)
                            },
                            enabled = sliderPosition < range.endInclusive
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = "Increase"
                            )
                        }
                    }
                    Text(positionString(sliderPosition))
                }
            }
        },
    )
}

@Preview
@Composable
private fun PreviewSliderDialog() {
    Theme(previewDark) {
        SliderDialog(
            onDismissRequest = { },
            onDone = { },
            initialValue = 100f,
            range = 0f..500f,
            title = { Text("move it") },
            showDefault = true
        )
    }
}
