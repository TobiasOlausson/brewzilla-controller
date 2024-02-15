package com.example.brewzilla_controller.models

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val _id: String,
    val author: String? = null,
    val name: String? = null,
    val type: String? = null,
    val _timestamp_ms: Long,
    private val data: Data? = null,
    val strikeTemp: Double? = data?.strikeTemp,
    val mash: Mash? = null,
)

@Serializable
data class Data(
    val strikeTemp: Double? = null,
)

@Serializable
data class Mash(
    val steps: List<MashStep>
)
@Serializable
data class MashStep(
    val displayStepTemp: Double? = null,
    val name: String? = null,
    val rampTime: Double? = null,
    val stepTemp: Double? = null,
    val stepTime: Double? = null,
    val type: String? = null
)