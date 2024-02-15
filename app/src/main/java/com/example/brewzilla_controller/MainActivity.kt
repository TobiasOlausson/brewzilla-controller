package com.example.brewzilla_controller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewzilla_controller.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Content()
        }
    }
}
@Preview
@Composable
fun Content() {
    return MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            TemperatureView(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize())
        }
    }
}

@Composable
fun TemperatureView(modifier: Modifier) {
    var currentTemperature by remember { mutableDoubleStateOf(1.0) }
    var targetTemperature by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(Unit) {
        val fetcher = BrewfatherFetcher()
        val batch = fetcher.getLatestAllGrainBatch()
        val strikeTemp = batch?.strikeTemp
        if (strikeTemp != null) {
            targetTemperature = strikeTemp
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$targetTemperature°C",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(text = "Target")
        Text(
            text = "$currentTemperature°C",
            style = TextStyle(fontSize = 72.sp)
        )
        Text(text = "Current", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(64.dp))

        Button(onClick = { }) {
            Text(text = "Start Brewing")
        }
    }
}
