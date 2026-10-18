package com.oddzmint.slipstash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.oddzmint.slipstash.presentation.list.ExpenseListScreen
import com.oddzmint.slipstash.ui.theme.SlipStashTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlipStashTheme {
            }
        }
    }
}
