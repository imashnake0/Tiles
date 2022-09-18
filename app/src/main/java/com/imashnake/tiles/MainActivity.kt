package com.imashnake.tiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import com.imashnake.tiles.features.Tiles
import com.imashnake.tiles.ui.theme.TilesTheme

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TilesTheme {
                Tiles()
            }
        }
    }
}
