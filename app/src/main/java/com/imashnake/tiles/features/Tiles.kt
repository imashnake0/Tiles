package com.imashnake.tiles.features

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.*

@ExperimentalFoundationApi
@Composable
fun Tiles(modifier: Modifier) {
    val wallpaperColors = listOf(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.onPrimary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.inversePrimary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.onSecondary,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.onTertiary,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.onBackground,
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.surfaceTint,
        MaterialTheme.colorScheme.inverseSurface,
        MaterialTheme.colorScheme.inverseOnSurface
    )

    val horizontal = 7

    val orderedColors = wallpaperColors.sortedBy { it.value }.toSet().take(horizontal)

    val flatAlphaColors = orderedColors.map { color ->
        List(horizontal) { index ->
            color.copy(alpha = ((index.toFloat() + 1f) / horizontal.toFloat()))
        }
    }.flatten()

    val flatPairs = flatAlphaColors.map {
        Pair(it, flatAlphaColors.indexOf(it))
    }.shuffled()

    val data = remember { mutableStateOf(flatPairs) }
    var initialFromColorPair = Pair(Color.Unspecified, -1)
    var isFirstCheck = true
    var initialList = data.value
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyGridState(
        dragCancelledAnimation = SpringDragCancelledAnimation(),
        onMove = { from, to ->
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            data.value = initialList.toMutableList().apply {
                if (isFirstCheck) {
                    initialFromColorPair = Pair(this[from.index].first, this[from.index].second)
                    isFirstCheck = false
                }

                val temp = this[to.index]
                this[to.index] = initialFromColorPair
                this[initialList.indexOf(initialFromColorPair)] = temp
            }
        },
        onDragEnd = try {
            { _, _ ->
                initialFromColorPair = Pair(Color.Unspecified, -1)
                isFirstCheck = true
                initialList = data.value
            }
        } catch (npe: NullPointerException) {
            { _, _ ->
                Log.d("onDragEnd", "NULL")
            }
        }
    )

    Box(modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(horizontal),
            state = state.gridState,
            contentPadding = PaddingValues(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .reorderable(state)
                .detectReorderAfterLongPress(state),
            userScrollEnabled = false
        ) {
            items(data.value, { it.second.toString() }) { pair ->
                ReorderableItem(
                    state,
                    key = pair.second.toString(),
                    defaultDraggingModifier = Modifier.animateItemPlacement()
                ) { isDragging ->
                    val scale: Float by animateFloatAsState(if (isDragging) 1.4f else 1.0f)

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(pair.first)
                            .aspectRatio(1f)
                            .fillMaxWidth()
                    ) { }
                }
            }
        }
    }
}
