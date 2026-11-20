package com.zeroconvert.ime

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InlineSuggestionsRequest
import android.view.inputmethod.InlineSuggestionsResponse
import android.widget.inline.InlinePresentationSpec
import androidx.annotation.RequiresApi
import androidx.autofill.inline.UiVersions
import androidx.autofill.inline.common.TextViewStyle
import androidx.autofill.inline.common.ViewStyle
import androidx.autofill.inline.v1.InlineSuggestionUi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Autofill suggestions (password managers such as Samsung Pass) shown in the keyboard toolbar,
 * like Gboard, instead of as a dropdown under the field. The autofill service renders each chip;
 * the keyboard only places the returned views and never sees the credentials.
 */
@RequiresApi(Build.VERSION_CODES.R)
internal class InlineAutofillSuggestions(private val context: Context) {
    private val viewsState = mutableStateOf<List<View>>(emptyList())
    val views: List<View> get() = viewsState.value

    // Inflation is asynchronous; a newer response or a finished input discards older results.
    private var generation = 0

    // ViewStyle.Builder exposes setBackground/setPadding publicly, but they are declared on a
    // library-restricted base class, which lint reports as a restricted call.
    @SuppressLint("RestrictedApi")
    fun createRequest(palette: ZeroConPalette, uiExtras: Bundle): InlineSuggestionsRequest {
        val density = context.resources.displayMetrics.density
        fun px(dp: Int) = (dp * density).toInt()
        val chip = ViewStyle.Builder()
            .setBackground(
                Icon.createWithResource(context, R.drawable.inline_suggestion_chip)
                    .setTint(palette.keyNormal.toArgb())
            )
            .setPadding(px(12), 0, px(12), 0)
            .build()
        val style = InlineSuggestionUi.newStyleBuilder()
            .setSingleIconChipStyle(chip)
            .setChipStyle(chip)
            .setTitleStyle(
                TextViewStyle.Builder().setTextColor(palette.textPrimary.toArgb()).setTextSize(14f).build()
            )
            .setSubtitleStyle(
                TextViewStyle.Builder().setTextColor(palette.textSecondary.toArgb()).setTextSize(12f).build()
            )
            .build()
        val styles = UiVersions.newStylesBuilder().addStyle(style).build()
        val height = px(34)
        val spec = InlinePresentationSpec.Builder(Size(px(48), height), Size(px(320), height))
            .setStyle(styles)
            .build()
        return InlineSuggestionsRequest.Builder(listOf(spec))
            .setMaxSuggestionCount(MAX_SUGGESTIONS)
            .setExtras(uiExtras)
            .build()
    }

    fun onResponse(response: InlineSuggestionsResponse): Boolean {
        val current = ++generation
        val suggestions = response.inlineSuggestions
        if (suggestions.isEmpty()) {
            viewsState.value = emptyList()
            return true
        }
        val inflated = arrayOfNulls<View>(suggestions.size)
        var remaining = suggestions.size
        val wrap = Size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        suggestions.forEachIndexed { index, suggestion ->
            suggestion.inflate(context, wrap, context.mainExecutor) { view ->
                if (current != generation) return@inflate
                inflated[index] = view
                remaining--
                if (remaining == 0) viewsState.value = inflated.filterNotNull()
            }
        }
        return true
    }

    fun clear() {
        generation++
        viewsState.value = emptyList()
    }

    private companion object {
        const val MAX_SUGGESTIONS = 6
    }
}

@Composable
internal fun InlineAutofillStrip(views: List<View>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        views.forEach { view ->
            key(view) {
                AndroidView(factory = {
                    // A view moved between compositions must leave its old parent first.
                    (view.parent as? ViewGroup)?.removeView(view)
                    view
                })
            }
        }
    }
}
