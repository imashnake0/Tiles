package com.imashnake.tiles

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.imashnake.tiles.ui.theme.TilesTheme
import org.burnoutcrew.reorderable.*

private val TAG = "ColorSwap"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TilesTheme {
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
                val state = rememberReorderableLazyGridState(
                    dragCancelledAnimation = SpringDragCancelledAnimation(),
                    onMove = { from, to ->
                        data.value = initialList.toMutableList().apply {
                            if (isFirstCheck) {
                                initialFromColorPair = Pair(this[from.index].first, this[from.index].second)
                                isFirstCheck = false
                            }
                            Log.d(TAG, "initialFromColor: $")

                            val temp = this[to.index]
                            this[to.index] = initialFromColorPair
                            this[initialList.indexOf(initialFromColorPair)] = temp
                        }
                    },
                    onDragEnd =
                    try {
                        { _, _ ->
                            initialFromColorPair = Pair(Color.Unspecified, -1)
                            isFirstCheck = true
                            initialList = data.value
                        }
                    } catch (npe: java.lang.NullPointerException) {
                        { _, _ ->
                            Log.d(TAG, "NULL")
                        }
                    }
                )
                Box(Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(horizontal),
                        state = state.gridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .reorderable(state),
                        verticalArrangement = Arrangement.Center,
                        userScrollEnabled = false
                    ) {
                        items(data.value, { it.second }) { pair ->
                            ReorderableItem(
                                state,
                                key = pair.second,
                                defaultDraggingModifier = Modifier
                            ) { isDragging ->
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(pair.first)
                                        .height((LocalConfiguration.current.screenWidthDp / horizontal).dp)
                                        .fillMaxWidth()
                                        .detectReorderAfterLongPress(state)
//                                        .pointerInput(Unit) {
//                                            detectTapGestures(
//                                                onLongPress = {
//
//                                                }
//                                            )
//                                        }
                                ) { }
                            }
                        }
                    }
                }
            }
        }
    }
}
