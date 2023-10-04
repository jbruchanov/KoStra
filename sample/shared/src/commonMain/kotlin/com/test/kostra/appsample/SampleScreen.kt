@file:OptIn(ExperimentalLayoutApi::class)

package com.test.kostra.appsample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

private object SampleScreenDefaults {
    val spacing = 8.dp
}

@Composable
fun AnnotatedText() {
    val sampleText = "SAMPLE text 12345"
    val text = buildAnnotatedString {
        appendLine(sampleText)
        withStyle(SpanStyle(fontSize = 0.5f.em)) {
            appendLine("$sampleText 0.5em")
        }
        withStyle(SpanStyle(fontSize = 1f.em)) {
            appendLine("$sampleText 1em")
        }
        withStyle(SpanStyle(fontSize = 2.em)) {
            appendLine("$sampleText 2em")
        }

        withStyle(SpanStyle(fontSize = 7.sp)) {
            appendLine("$sampleText 7sp")
        }
        withStyle(SpanStyle(fontSize = 14.sp)) {
            appendLine("$sampleText 14sp")
        }
        withStyle(SpanStyle(fontSize = 28.sp)) {
            appendLine("$sampleText 28sp")
        }
    }
    Text(text, fontSize = 14.sp)
}

@Composable
fun SampleScreen(extraContent: @Composable ColumnScope.() -> Unit = {}) = with(SampleScreenDefaults) {
    AnnotatedText()
}

@Composable
private fun TextCheckBox(onCheckedChange: (Boolean) -> Unit, checked: Boolean, text: CharSequence, modifier: Modifier = Modifier) = with(SampleScreenDefaults) {
    @Suppress("NAME_SHADOWING")
    val checked by rememberUpdatedState(checked)
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(spacing))
            .clickable { onCheckedChange(!checked) }
            .padding(end = spacing),
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        @Suppress("NAME_SHADOWING")
        val text = when {
            text is AnnotatedString -> text
            else -> AnnotatedString(text.toString())
        }
        Text(text)
    }
}
