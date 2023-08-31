package com.test.kostra.appsample

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jibru.kostra.KDpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.compose.LocalQualifiers
import com.jibru.kostra.icu.FixedDecimal
import com.sample.app.K
import com.sample.app.assetPath
import com.sample.app.ordinalStringResource
import com.sample.app.painterResource
import com.sample.app.pluralStringResource
import com.sample.app.stringResource

private object SampleScreenDefaults {
    val spacing = 8.dp
}

@Composable
fun SampleScreen() = with(SampleScreenDefaults) {
    Box(
        modifier = Modifier
            .padding(spacing)
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier
                .padding(spacing)
                .animateContentSize()
                .wrapContentSize(),
        ) {
            val locales = remember { listOf(java.util.Locale.getDefault(), java.util.Locale.ENGLISH, java.util.Locale("cs")) }
            var localeIndex by remember { mutableStateOf(0) }
            val locale by derivedStateOf { KLocale(locales[localeIndex].toLanguageTag()) }

            val defaultQualifiers = LocalQualifiers.current
            val dpis = remember { listOf(null, KDpi.Undefined, KDpi.XXHDPI) }
            var dpiIndex by remember { mutableStateOf(0) }
            val dpi by derivedStateOf { dpis[dpiIndex] ?: defaultQualifiers.dpi }

            val qualifiers by derivedStateOf { KQualifiers(locale, dpi) }

            CompositionLocalProvider(LocalQualifiers provides qualifiers) {
                Text("KLocale")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    locales.forEachIndexed { index, locale ->
                        TextCheckBox(
                            onCheckedChange = { if (it) localeIndex = index else Unit },
                            checked = index == localeIndex,
                            text = locale.toLanguageTag(),
                        )
                    }
                }

                Text("DPI: ${defaultQualifiers.dpi}")
                Text("Density:${LocalDensity.current}")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    dpis.forEachIndexed { index, dpi ->
                        TextCheckBox(
                            onCheckedChange = { if (it) dpiIndex = index else Unit },
                            checked = index == dpiIndex,
                            text = dpi?.name ?: "Device:${defaultQualifiers.dpi.name}",
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    Button(onClick = {}) { Text(stringResource(K.string.action_add)) }
                    Button(onClick = {}) { Text(stringResource(K.string.action_remove)) }
                    Button(onClick = {}) { Text(stringResource(K.string.color)) }
                }

                Text(assetPath(K.drawable.capital_city))
                Image(painterResource(K.drawable.capital_city), contentDescription = null)

                var quantity by remember { mutableStateOf("1") }

                TextField(
                    value = quantity,
                    label = { Text(stringResource(K.string.plurals)) },
                    placeholder = { Text(stringResource(K.string.type_quantity)) },
                    onValueChange = { quantity = it.takeIf { it.isEmpty() || it.toFloatOrNull() != null } ?: quantity },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                Text(stringResource(K.string.plurals) + ": " + (quantity.toDoubleOrNull()?.let { pluralStringResource(K.plural.bug_x, FixedDecimal(it), quantity) } ?: ""))
                Text(stringResource(K.string.ordinals) + ": " + (quantity.toDoubleOrNull()?.let { ordinalStringResource(K.plural.day_x, FixedDecimal(it), quantity) } ?: ""))
            }
        }
    }
}

@Composable
private fun TextCheckBox(onCheckedChange: (Boolean) -> Unit, checked: Boolean, text: String, modifier: Modifier = Modifier) = with(SampleScreenDefaults) {
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
        Text(text)
    }
}
