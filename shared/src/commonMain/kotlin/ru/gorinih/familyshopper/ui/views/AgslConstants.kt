package ru.gorinih.familyshopper.ui.views

/**
 * Created by Igor Abdulganeev on 03.08.2026
 */

const val ANIMATED_GRADIENT_SHADER = """
uniform float2 u_resolution;
uniform float  u_time;
uniform float3 u_color1;
uniform float3 u_color2;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / u_resolution;
    // центрируем координаты
    uv = uv * 2.0 - 1.0;
    float t = u_time * 0.12;
    // несколько плавных волн
    float wave1 = sin((1.0 - uv.x) * 2.5 + t);
    float wave2 = sin(uv.y * 2.0 - t * 0.7);
    float wave3 = sin((uv.x + uv.y) * 1.5 + t * 0.5);
    // смешиваем
    float flow = (wave1 + wave2 + wave3) / 3.0;
    // нормализуем 0..1
    float gradient = flow * 0.5 + 0.5;
    // делаем мягче
    gradient = smoothstep(0.1, 0.9, gradient);
    half3 color = mix(u_color1, u_color2, gradient);
    return half4(color, 1.0);
}
"""
