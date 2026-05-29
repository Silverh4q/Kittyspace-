package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainMasterScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.KittyViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: KittyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainMasterScreen(
          viewModel = viewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
