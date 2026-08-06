package com.mantelgroup.appfunctionsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mantelgroup.appfunctionsdemo.ui.navigation.AppNavigation
import com.mantelgroup.appfunctionsdemo.ui.theme.AppFunctionsDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFunctionsDemoTheme {
                AppNavigation()
            }
        }
    }
}
