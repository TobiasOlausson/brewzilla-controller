@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.brewzilla_controller

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.brewzilla_controller.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme() {
                Content()
            }
        }
    }
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Content() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TemperatureView(modifier = Modifier.align(Alignment.Center))
            StartButton(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}
@Composable
fun TemperatureView(modifier: Modifier) {
    var currentTemperature by remember { mutableDoubleStateOf(1.0) }
    var targetTemperature by remember { mutableDoubleStateOf(0.0) }
    var recipeName by remember { mutableStateOf("") }

    // set the target temperature from brewfather
    LaunchedEffect(Unit) {
        val brewfatherFetcher = BrewfatherFetcher()
        val batch = brewfatherFetcher.getLatestAllGrainBatch()
        val name = batch?.recipe?.name
        if (name != null) recipeName = name
        val strikeTemp = batch?.strikeTemp ?: return@LaunchedEffect
        targetTemperature = strikeTemp
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(16.dp)
    ) {
        item { CardView("$targetTemperature°C", "Target") }
        item { CardView("$currentTemperature°C", "Current") }
        item { CardView("24:54", "Mash step 1 of 2") }
        item { CardView(recipeName, "") }
    }
}

@Composable
fun CardView(content: String, label: String, modifier: Modifier = Modifier, isButton: Boolean = false) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .clickable(isButton) { /* Handle click here if needed */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), // Increase elevation for clickable cards
        onClick = { /* Handle click here if needed */ }
    ) {
        if (isButton) {
            Button(onClick = { }, modifier = Modifier.fillMaxSize(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary)) {
                Text(text = content, modifier = Modifier.align(Alignment.CenterVertically))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                if (label.isNotEmpty()) {
                    Text(text = label)
                }
            }
        }
    }
}


@Composable
fun StartButton(modifier: Modifier) {
    Button(
        onClick = { /* Handle click here if needed */ },
        modifier = modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = "Start brewing", modifier = Modifier.align(Alignment.CenterVertically))
    }
}
