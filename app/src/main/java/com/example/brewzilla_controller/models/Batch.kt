package com.example.brewzilla_controller.models

import com.example.brewzilla_controller.models.Recipe
import kotlinx.serialization.Serializable

@Serializable
data class Batch(
    val recipe: Recipe,
    val _timestamp_ms: Long,
    val strikeTemp: Double? = recipe.strikeTemp
)