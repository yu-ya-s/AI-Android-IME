package com.zeroconvert.ime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SettingsTab(
    val id: String,
    val label: String,
    val icon: ImageVector
)

val SettingsTabs = listOf(
    SettingsTab("input", "入力", Icons.Outlined.Keyboard),
    SettingsTab("conversion", "変換", Icons.Outlined.Tune),
    SettingsTab("data", "学習とデータ", Icons.Outlined.Storage),
    SettingsTab("translation", "翻訳", Icons.Outlined.Translate),
    SettingsTab("benchmark", "モデルと評価", Icons.Outlined.Speed),
    SettingsTab("appearance", "外観", Icons.Outlined.Palette),
    SettingsTab("about", "情報", Icons.Outlined.Info)
)

@Composable
fun SettingsSection(
    title: String,
    palette: ZeroConPalette,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = palette.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.1).sp
        )
        SettingsCard(palette = palette, content = content)
    }
}

@Composable
fun SettingsCard(
    palette: ZeroConPalette,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surface)
            .border(1.dp, palette.borderSubtle, RoundedCornerShape(10.dp)),
        content = content
    )
}

@Composable
fun SettingsInnerCard(
    palette: ZeroConPalette,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surfaceElevated),
        content = content
    )
}

@Composable
fun SettingsDivider(palette: ZeroConPalette) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(palette.borderSubtle)
    )
}

@Composable
fun SettingsRow(
    title: String,
    palette: ZeroConPalette,
    description: String? = null,
    badge: String? = null,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    color = palette.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
                if (badge != null) {
                    SettingsBadge(badge, palette)
                }
            }
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    color = palette.textMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
        control()
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    palette: ZeroConPalette,
    onCheckedChange: (Boolean) -> Unit,
    description: String? = null,
    badge: String? = null,
    enabled: Boolean = true
) {
    SettingsRow(
        title = title,
        palette = palette,
        description = description,
        badge = badge
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.onAccent,
                checkedTrackColor = palette.primaryAccent,
                uncheckedThumbColor = palette.textSecondary,
                uncheckedTrackColor = palette.surfaceElevated,
                uncheckedBorderColor = palette.borderSubtle
            )
        )
    }
}

@Composable
fun SettingsNote(
    text: String,
    palette: ZeroConPalette,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = palette.textMuted,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsBadge(text: String, palette: ZeroConPalette) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = palette.primaryAccent.copy(alpha = 0.14f)
    ) {
        Text(
            text = text,
            color = palette.primaryAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SettingsStatusLine(
    text: String,
    ready: Boolean,
    palette: ZeroConPalette
) {
    // Only the dot carries the state colour; a ready model reads as plain secondary text.
    val color = if (ready) palette.readyEmerald else palette.warningAmber
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Text(
            text = text,
            color = if (ready) palette.textSecondary else color,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun <T> SettingsSegmented(
    options: List<Pair<T, String>>,
    selected: T,
    palette: ZeroConPalette,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.surfaceElevated, RoundedCornerShape(8.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEach { (value, label) ->
            val isSelected = value == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelected(value) },
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) palette.surface else Color.Transparent,
                border = if (isSelected) BorderStroke(1.dp, palette.borderSubtle) else null
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) palette.textPrimary else palette.textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Theme picker: a "system" toggle plus one swatch per palette. Each swatch
 * previews its own colors so the choice is visible before it is applied.
 */
@Composable
fun SettingsThemePicker(
    selected: String,
    palette: ZeroConPalette,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val systemDark = isSystemInDarkTheme()
    val followsSystem = selected == ZERO_CON_THEME_SYSTEM
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsSwitchRow(
            title = "システム連動",
            description = if (followsSystem) {
                "端末のダークモードに合わせて、グラファイトまたはペーパーを使います。"
            } else {
                "オフのときは下で選んだテーマを、端末設定に関係なく使います。"
            },
            checked = followsSystem,
            palette = palette,
            onCheckedChange = { enabled ->
                onSelected(
                    if (enabled) ZERO_CON_THEME_SYSTEM
                    else resolveZeroConThemeId(ZERO_CON_THEME_SYSTEM, systemDark)
                )
            }
        )
        ZeroConThemes.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { theme ->
                    ThemeSwatch(
                        theme = theme,
                        selected = theme.id == selected,
                        // In system mode no swatch is chosen, so mark the one the device resolves to.
                        implied = followsSystem &&
                            theme.id == resolveZeroConThemeId(selected, systemDark),
                        palette = palette,
                        onClick = { onSelected(theme.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ThemeSwatch(
    theme: ZeroConTheme,
    selected: Boolean,
    implied: Boolean,
    palette: ZeroConPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swatch = theme.palette
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(swatch.canvas)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = when {
                    selected -> palette.primaryAccent
                    implied -> palette.primaryAccent.copy(alpha = 0.55f)
                    else -> palette.borderSubtle
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(swatch.primaryAccent, RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(swatch.surfaceElevated, RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(swatch.keyNormal, RoundedCornerShape(4.dp))
            )
        }
        Text(
            text = theme.label,
            color = swatch.textPrimary,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (theme.isDark) "暗色" else "明色",
            color = swatch.textMuted,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            maxLines = 1
        )
    }
}

@Composable
fun SettingsTabBar(
    tabs: List<SettingsTab>,
    selectedId: String,
    palette: ZeroConPalette,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Text tabs on one hairline: the selected tab is marked by weight and an accent underline,
    // so the header reads as navigation rather than a row of buttons.
    val scroll = rememberScrollState()
    var viewportWidth by remember { mutableIntStateOf(0) }
    val tabBounds = remember { mutableStateMapOf<String, Pair<Int, Int>>() }
    // Swiping the pages can select a tab that is scrolled out of the bar; keep it in view.
    LaunchedEffect(selectedId, viewportWidth) {
        val (left, width) = tabBounds[selectedId] ?: return@LaunchedEffect
        scroll.animateScrollTo((left - (viewportWidth - width) / 2).coerceIn(0, scroll.maxValue))
    }
    Box(modifier = modifier.fillMaxWidth().onSizeChanged { viewportWidth = it.width }) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.borderSubtle)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .padding(horizontal = 8.dp)
        ) {
            tabs.forEach { tab ->
                val selected = tab.id == selectedId
                Column(
                    modifier = Modifier
                        .onPlaced { tabBounds[tab.id] = it.positionInParent().x.roundToInt() to it.size.width }
                        .clickable { onSelected(tab.id) }
                        .padding(horizontal = 12.dp)
                        .width(IntrinsicSize.Max),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab.label,
                        modifier = Modifier.padding(top = 10.dp, bottom = 9.dp),
                        color = if (selected) palette.textPrimary else palette.textSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(
                                if (selected) palette.primaryAccent else Color.Transparent,
                                RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsPrimaryButton(
    text: String,
    palette: ZeroConPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.primaryAccent,
            contentColor = palette.onAccent,
            disabledContainerColor = palette.surfaceElevated,
            disabledContentColor = palette.textMuted
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun SettingsSecondaryButton(
    text: String,
    palette: ZeroConPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, palette.borderSubtle),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.surfaceElevated,
            contentColor = palette.textPrimary,
            disabledContainerColor = palette.surface,
            disabledContentColor = palette.textMuted
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun settingsTextFieldColors(palette: ZeroConPalette): TextFieldColors =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = palette.textPrimary,
        unfocusedTextColor = palette.textPrimary,
        disabledTextColor = palette.textMuted,
        focusedBorderColor = palette.primaryAccent,
        unfocusedBorderColor = palette.borderSubtle,
        disabledBorderColor = palette.borderSubtle,
        cursorColor = palette.primaryAccent,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedPlaceholderColor = palette.textMuted,
        unfocusedPlaceholderColor = palette.textMuted
    )
