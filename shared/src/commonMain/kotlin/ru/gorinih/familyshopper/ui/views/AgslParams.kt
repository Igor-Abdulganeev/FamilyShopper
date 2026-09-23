package ru.gorinih.familyshopper.ui.views

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader

/**
 * Created by Igor Abdulganeev on 03.08.2026
 */

expect class AgslParams(shaderCode: String) {
    fun setUniform(name: String, value: Float)
    fun setUniform(name: String, valueFirst: Float, valueSecond: Float)
    fun setUniform(name: String, color: Color)
    fun asShader(): Shader
}