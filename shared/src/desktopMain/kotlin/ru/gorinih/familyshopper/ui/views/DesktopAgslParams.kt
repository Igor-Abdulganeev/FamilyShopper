package ru.gorinih.familyshopper.ui.views

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeShader
import org.jetbrains.skia.Data
import org.jetbrains.skia.RuntimeEffect
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by Igor Abdulganeev on 03.08.2026
 */

actual class AgslParams actual constructor(shaderCode: String) {
    private val runtimeEffect = RuntimeEffect.makeForShader(shaderCode)

    private val values = mutableMapOf<String, FloatArray>()

    actual fun setUniform(name: String, value: Float) {
        values[name] = floatArrayOf(value)
    }

    actual fun setUniform(name: String, valueFirst: Float, valueSecond: Float) {
        values[name] = floatArrayOf(valueFirst, valueSecond)
    }

    actual fun setUniform(name: String, color: Color) {
        values[name] = floatArrayOf(color.red, color.green, color.blue)
    }

    actual fun asShader(): Shader {
        val totalFloats = values.values.sumOf { it.size }
        val buffer = ByteBuffer.allocate(totalFloats * 4).order(ByteOrder.nativeOrder())

        values["u_resolution"]?.forEach { buffer.putFloat(it) }
        values["u_time"]?.forEach { buffer.putFloat(it) }
        values["u_color1"]?.forEach { buffer.putFloat(it) }
        values["u_color2"]?.forEach { buffer.putFloat(it) }

        val skiaData = Data.makeFromBytes(buffer.array())

        val skiaShader = runtimeEffect.makeShader(skiaData, null, null)

        return skiaShader.asComposeShader()
    }

}