package com.example.ui.components

import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f,
            ) == 0f
        }.getOrDefault(false)
    }
}

@Composable
fun GentleEntrance(
    delayMillis: Int = 0,
    reduceMotion: Boolean = rememberReduceMotion(),
    content: @Composable () -> Unit,
) {
    if (reduceMotion) {
        content()
        return
    }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delayMillis > 0) kotlinx.coroutines.delay(delayMillis.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(280, easing = EaseOutCubic)) +
            slideInVertically(
                animationSpec = tween(280, easing = EaseOutCubic),
                initialOffsetY = { it / 10 },
            ),
    ) {
        content()
    }
}

@Composable
fun AnimatedCounterText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    reduceMotion: Boolean = rememberReduceMotion(),
    modifier: Modifier = Modifier,
) {
    if (reduceMotion) {
        Text(text = text, style = style, color = color, textAlign = TextAlign.Center, modifier = modifier)
        return
    }
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            (fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 4 })
                .togetherWith(fadeOut(tween(140)) + slideOutVertically(tween(140)) { -it / 4 })
        },
        label = "counter_digit_anim",
        modifier = modifier,
    ) { value ->
        Text(
            text = value,
            style = style,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun IslamicGeometricPattern(
    color: Color = GoldPrimary.copy(alpha = 0.16f),
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val step = 56f
        val cols = (size.width / step).toInt() + 2
        val rows = (size.height / step).toInt() + 2
        for (r in 0..rows) {
            for (c in 0..cols) {
                val cx = c * step + if (r % 2 == 1) step / 2f else 0f
                val cy = r * step
                drawEightPointStar(
                    center = Offset(cx, cy),
                    outerR = step * 0.34f,
                    innerR = step * 0.16f,
                    color = color,
                    strokeWidth = 1.4f,
                )
            }
        }
    }
}

private fun DrawScope.drawEightPointStar(
    center: Offset,
    outerR: Float,
    innerR: Float,
    color: Color,
    strokeWidth: Float,
) {
    val path = Path()
    val points = 16
    for (i in 0 until points) {
        val angle = (i * PI / 8.0)
        val radius = if (i % 2 == 0) outerR else innerR
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
}

@Composable
fun CrescentStarEmblem(
    crescentColor: Color = GoldLight,
    starColor: Color = Color(0xFFFFFBF0),
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = min(w, h) * 0.42f
        val cx = w * 0.48f
        val cy = h * 0.52f

        val crescentPath = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(cx - r, cy - r, cx + r, cy + r))
        }
        val cutoutPath = Path().apply {
            val offsetR = r * 0.84f
            val cutCx = cx + r * 0.34f
            val cutCy = cy - r * 0.12f
            addOval(androidx.compose.ui.geometry.Rect(cutCx - offsetR, cutCy - offsetR, cutCx + offsetR, cutCy + offsetR))
        }
        val finalCrescent = Path.combine(
            androidx.compose.ui.graphics.PathOperation.Difference,
            crescentPath,
            cutoutPath,
        )
        drawPath(finalCrescent, color = crescentColor)

        // Small 8-point star nestled inside crescent
        val starCenter = Offset(cx + r * 0.42f, cy - r * 0.15f)
        val starPath = Path()
        val outer = r * 0.24f
        val inner = r * 0.10f
        for (i in 0 until 8) {
            val a = i * PI / 4.0
            val rad = if (i % 2 == 0) outer else inner
            val sx = starCenter.x + (rad * cos(a)).toFloat()
            val sy = starCenter.y + (rad * sin(a)).toFloat()
            if (i == 0) starPath.moveTo(sx, sy) else starPath.lineTo(sx, sy)
        }
        starPath.close()
        drawPath(starPath, color = starColor)
    }
}

@Composable
fun MosqueSilhouette(
    color: Color = GoldPrimary.copy(alpha = 0.28f),
    highlightColor: Color = GoldLight.copy(alpha = 0.45f),
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val baseY = h

        val path = Path().apply {
            moveTo(0f, baseY)
            lineTo(0f, h * 0.80f)
            lineTo(w * 0.08f, h * 0.80f)
            // Left minaret
            lineTo(w * 0.08f, h * 0.38f)
            lineTo(w * 0.10f, h * 0.24f)
            lineTo(w * 0.12f, h * 0.38f)
            lineTo(w * 0.12f, h * 0.72f)
            // Left small dome
            lineTo(w * 0.18f, h * 0.72f)
            cubicTo(w * 0.19f, h * 0.56f, w * 0.29f, h * 0.56f, w * 0.30f, h * 0.72f)
            lineTo(w * 0.36f, h * 0.72f)
            // Central grand dome
            cubicTo(w * 0.37f, h * 0.34f, w * 0.46f, h * 0.22f, w * 0.50f, h * 0.16f)
            cubicTo(w * 0.54f, h * 0.22f, w * 0.63f, h * 0.34f, w * 0.64f, h * 0.72f)
            lineTo(w * 0.70f, h * 0.72f)
            // Right small dome
            cubicTo(w * 0.71f, h * 0.56f, w * 0.81f, h * 0.56f, w * 0.82f, h * 0.72f)
            lineTo(w * 0.88f, h * 0.72f)
            // Right minaret
            lineTo(w * 0.88f, h * 0.38f)
            lineTo(w * 0.90f, h * 0.24f)
            lineTo(w * 0.92f, h * 0.38f)
            lineTo(w * 0.92f, h * 0.80f)
            lineTo(w, h * 0.80f)
            lineTo(w, baseY)
            close()
        }
        drawPath(path, color = color)

        // Subtle golden spire finial on central dome
        drawLine(
            color = highlightColor,
            start = Offset(w * 0.50f, h * 0.08f),
            end = Offset(w * 0.50f, h * 0.17f),
            strokeWidth = 2.4f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun TasbihBeadRing(
    currentCount: Int,
    targetCount: Int,
    reduceMotion: Boolean = rememberReduceMotion(),
    modifier: Modifier = Modifier,
    centerContent: @Composable BoxScope.() -> Unit,
) {
    val safeTarget = targetCount.coerceAtLeast(1)
    val targetTotalAngle = (currentCount.toFloat() / safeTarget.toFloat()) * 360f

    val animatedTotalAngle by animateFloatAsState(
        targetValue = targetTotalAngle,
        animationSpec = when {
            reduceMotion -> tween(0)
            currentCount == 0 -> tween(220, easing = EaseOutCubic)
            else -> androidx.compose.animation.core.spring(
                dampingRatio = 0.88f,
                stiffness = androidx.compose.animation.core.Spring.StiffnessMedium,
            )
        },
        label = "tasbih_total_angle",
    )

    // Subtle expanding golden pulse on every tap increment
    val tapPulse = remember { androidx.compose.animation.core.Animatable(0f) }
    var previousCount by remember { mutableStateOf(currentCount) }
    LaunchedEffect(currentCount) {
        if (currentCount > previousCount && !reduceMotion) {
            tapPulse.snapTo(0f)
            tapPulse.animateTo(1f, animationSpec = tween(260, easing = EaseOutCubic))
        } else if (currentCount == 0) {
            tapPulse.snapTo(0f)
        }
        previousCount = currentCount
    }

    val currentTurnSweep = when {
        currentCount == 0 && animatedTotalAngle < 0.5f -> 0f
        else -> {
            val mod = animatedTotalAngle % 360f
            if (mod < 0.01f && animatedTotalAngle > 1f) 360f else mod
        }
    }

    val beadCount = if (safeTarget <= 33) safeTarget else 33
    val stepAngle = 360f / beadCount.toFloat()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val diameter = min(size.width, size.height)
            val strokeW = diameter * 0.052f
            val ringRadius = (diameter - strokeW * 2.8f) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val topLeft = Offset(center.x - ringRadius, center.y - ringRadius)
            val arcSize = Size(ringRadius * 2f, ringRadius * 2f)

            // Inner soft emerald radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        EmeraldBright.copy(alpha = 0.30f),
                        EmeraldDeep.copy(alpha = 0.92f),
                    ),
                    center = center,
                    radius = ringRadius * 0.92f,
                ),
                radius = ringRadius * 0.90f,
                center = center,
            )

            // Subtle tap pulse wave inside the ring
            val pulseProgress = tapPulse.value
            if (pulseProgress > 0f && pulseProgress < 1f) {
                val pulseRadius = ringRadius * (0.45f + 0.44f * pulseProgress)
                drawCircle(
                    color = GoldLight.copy(alpha = (1f - pulseProgress) * 0.28f),
                    radius = pulseRadius,
                    center = center,
                    style = Stroke(width = 3f * (1f - pulseProgress * 0.5f)),
                )
            }

            // Track ring
            drawArc(
                color = GoldPrimary.copy(alpha = 0.22f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeW, cap = StrokeCap.Round),
            )

            // Active progress arc synchronized monotonically with animatedTotalAngle
            if (currentTurnSweep > 0.2f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            GoldPrimary,
                            GoldLight,
                            EmeraldBright,
                            GoldPrimary,
                        ),
                        center = center,
                    ),
                    startAngle = -90f,
                    sweepAngle = currentTurnSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round),
                )
            }

            // Tasbih beads aligned accurately with 1..beadCount steps clockwise from 12 o'clock
            val beadOrbitRadius = ringRadius
            val baseBeadRadius = diameter * (if (beadCount > 24) 0.021f else 0.028f)
            for (i in 0 until beadCount) {
                val beadPosDeg = (i + 1) * stepAngle
                val angleDeg = -90.0 + beadPosDeg
                val angleRad = Math.toRadians(angleDeg)
                val bx = center.x + (beadOrbitRadius * cos(angleRad)).toFloat()
                val by = center.y + (beadOrbitRadius * sin(angleRad)).toFloat()
                val isCompleted = currentTurnSweep >= beadPosDeg - 0.6f

                drawCircle(
                    color = if (isCompleted) GoldPrimary else EmeraldMedium,
                    radius = baseBeadRadius,
                    center = Offset(bx, by),
                )
                drawCircle(
                    color = if (isCompleted) Color.White.copy(alpha = 0.45f) else GoldPrimary.copy(alpha = 0.30f),
                    radius = baseBeadRadius * 0.40f,
                    center = Offset(bx - baseBeadRadius * 0.22f, by - baseBeadRadius * 0.22f),
                )
            }

            // Smoothly gliding active golden bead at exact arc tip
            if (currentCount > 0 || currentTurnSweep > 0.2f) {
                val tipAngleRad = Math.toRadians(-90.0 + currentTurnSweep)
                val tx = center.x + (beadOrbitRadius * cos(tipAngleRad)).toFloat()
                val ty = center.y + (beadOrbitRadius * sin(tipAngleRad)).toFloat()
                val activeRadius = baseBeadRadius * 1.55f

                drawCircle(
                    color = GoldLight.copy(alpha = 0.35f),
                    radius = activeRadius * 1.5f,
                    center = Offset(tx, ty),
                )
                drawCircle(
                    color = GoldLight,
                    radius = activeRadius,
                    center = Offset(tx, ty),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.65f),
                    radius = activeRadius * 0.42f,
                    center = Offset(tx - activeRadius * 0.24f, ty - activeRadius * 0.24f),
                )
            }
        }
        centerContent()
    }
}
