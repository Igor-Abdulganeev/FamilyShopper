package ru.gorinih.familyshopper.ui.views

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader

/**
 * Created by Igor Abdulganeev on 03.08.2026
 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual class AgslParams actual constructor(shaderCode: String) {
    private val androidShader = RuntimeShader(shaderCode)

    actual fun setUniform(name: String, value: Float) {
        androidShader.setFloatUniform(name, value)
    }

    actual fun setUniform(name: String, valueFirst: Float, valueSecond: Float) {
        androidShader.setFloatUniform(name, valueFirst, valueSecond)
    }

    actual fun setUniform(name: String, color: Color) {
        androidShader.setFloatUniform(name, color.red, color.green, color.blue)
    }

    actual fun asShader(): Shader =
        androidShader
}