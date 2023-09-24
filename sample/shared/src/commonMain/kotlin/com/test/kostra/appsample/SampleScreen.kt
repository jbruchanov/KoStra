@file:OptIn(ExperimentalLayoutApi::class)

package com.test.kostra.appsample

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun SampleScreen(extraContent: @Composable ColumnScope.() -> Unit = {}) = with(SampleScreenDefaults) {
    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(spacing),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier
                .padding(spacing)
                .animateContentSize()
                .wrapContentSize(),
        ) {
            val defaultQualifiers = LocalQualifiers.current
            val locales = remember {
                val codes = listOf("ar", "cs", "en", "enGB", "enUS", "he", "hi", "ja", "ko", "ru", "th")
                (listOf(defaultQualifiers.locale) + codes.map { KLocale(it) }).distinct()
            }
            var localeIndex by remember { mutableStateOf(0) }
            val locale by remember { derivedStateOf { KLocale(locales[localeIndex].languageRegion) } }

            val dpis = remember { listOf(null, KDpi.Undefined, KDpi.XHDPI, KDpi.XXHDPI) }
            var dpiIndex by remember { mutableStateOf(0) }
            val dpi by remember { derivedStateOf { dpis[dpiIndex] ?: defaultQualifiers.dpi } }

            val qualifiers by remember { derivedStateOf { KQualifiers(locale, dpi) } }

            CompositionLocalProvider(LocalQualifiers provides qualifiers) {
                Text("KQualifiers:$defaultQualifiers")
                Text("KLocale")
                FlowRow(
                    verticalArrangement = Arrangement.Center,

                ) {
                    locales.forEachIndexed { index, locale ->
                        TextCheckBox(
                            onCheckedChange = { if (it) localeIndex = index else Unit },
                            checked = index == localeIndex,
                            text = locale.languageRegion,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Text("DPI: ${defaultQualifiers.dpi}")
                Text("Density:${LocalDensity.current}")
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    dpis.forEachIndexed { index, dpi ->
                        var text = dpi?.name ?: "Dev:${defaultQualifiers.dpi.name}"
                        if (dpi == KDpi.XXHDPI) {
                            text += "\n(CS/EN-GB for capital_city asset)"
                        }
                        TextCheckBox(
                            onCheckedChange = { if (it) dpiIndex = index else Unit },
                            checked = index == dpiIndex,
                            text = text,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    Button(onClick = {}) { Text(stringResource(K.string.action_add)) }
                    Button(onClick = {}) { Text(stringResource(K.string.action_remove)) }
                    Button(onClick = {}) { Text(stringResource(K.string.color)) }
                }

                Text(assetPath(K.images.capital_city))
                Image(painterResource(K.images.capital_city), contentDescription = null)

                Spacer(modifier = Modifier.height(4.dp))
                Text(assetPath(K.flagsxml.country_flag))
                Image(painterResource(K.flagsxml.country_flag), contentDescription = null, modifier = Modifier.height(64.dp))

                extraContent()

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
