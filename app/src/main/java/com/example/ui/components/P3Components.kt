package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Slanted shape cutting bottom-right corner in Persona 3 FES style
val P3CutShape = GenericShape { size, _ ->
  val cut = 16.dp.value
  moveTo(0f, 0f)
  lineTo(size.width, 0f)
  lineTo(size.width, size.height - cut)
  lineTo(size.width - cut, size.height)
  lineTo(0f, size.height)
  close()
}

// Slanted badge shape
val P3AngledBadgeShape = GenericShape { size, _ ->
  val slant = 14.dp.value
  moveTo(slant, 0f)
  lineTo(size.width, 0f)
  lineTo(size.width - slant, size.height)
  lineTo(0f, size.height)
  close()
}

@Composable
fun P3Background(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "p3_pulse")
  val pulse by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            P3VoidBlack,
            P3TartarusDark,
            P3DeepNavy,
            P3VoidBlack
          )
        )
      )
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Subtle Dark Hour grid lines
      val gridStep = 40.dp.toPx()
      var x = 0f
      while (x < w) {
        drawLine(
          color = P3ElectricCyan.copy(alpha = 0.035f),
          start = Offset(x, 0f),
          end = Offset(x, h),
          strokeWidth = 1f
        )
        x += gridStep
      }

      var y = 0f
      while (y < h) {
        drawLine(
          color = P3ElectricCyan.copy(alpha = 0.035f),
          start = Offset(0f, y),
          end = Offset(w, y),
          strokeWidth = 1f
        )
        y += gridStep
      }

      // Slanted Tartarus light streak
      drawLine(
        brush = Brush.linearGradient(
          colors = listOf(
            Color.Transparent,
            P3ElectricCyan.copy(alpha = 0.12f * pulse),
            Color.Transparent
          ),
          start = Offset(0f, h * 0.2f),
          end = Offset(w, h * 0.7f)
        ),
        start = Offset(0f, h * 0.2f),
        end = Offset(w, h * 0.7f),
        strokeWidth = 3.dp.toPx()
      )

      // Celestial Dark Hour crescent moon ring in upper right
      val moonCenter = Offset(w * 0.85f, h * 0.15f)
      val moonRadius = 90.dp.toPx() * pulse

      drawCircle(
        color = P3ElectricCyan.copy(alpha = 0.05f),
        radius = moonRadius,
        center = moonCenter,
        style = Stroke(width = 2.dp.toPx())
      )
      drawCircle(
        color = P3SoftCyan.copy(alpha = 0.03f),
        radius = moonRadius * 1.35f,
        center = moonCenter,
        style = Stroke(width = 1.dp.toPx())
      )
    }

    content()
  }
}

@Composable
fun P3AngledCard(
  modifier: Modifier = Modifier,
  borderColor: Color = P3ElectricCyan.copy(alpha = 0.6f),
  backgroundColor: Color = P3SurfaceNavy.copy(alpha = 0.85f),
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .clip(P3CutShape)
      .background(backgroundColor)
      .border(1.5.dp, borderColor, P3CutShape)
      .padding(16.dp)
  ) {
    content()
  }
}

@Composable
fun P3Button(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isPrimary: Boolean = true,
  testTag: String = "p3_button",
  icon: (@Composable () -> Unit)? = null
) {
  val backgroundBrush = if (isPrimary) {
    Brush.horizontalGradient(
      colors = listOf(
        P3ElectricCyan,
        P3NeonBlue
      )
    )
  } else {
    Brush.horizontalGradient(
      colors = listOf(
        P3SurfaceNavy.copy(alpha = 0.9f),
        P3DeepNavy.copy(alpha = 0.9f)
      )
    )
  }

  val textColor = if (isPrimary) P3VoidBlack else P3IceBlue
  val borderColor = if (isPrimary) P3IceBlue else P3ElectricCyan.copy(alpha = 0.7f)

  Box(
    modifier = modifier
      .testTag(testTag)
      .clip(P3CutShape)
      .background(backgroundBrush)
      .border(1.5.dp, borderColor, P3CutShape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 22.dp, vertical = 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = FontWeight.Black,
          letterSpacing = 1.5.sp
        ),
        color = textColor
      )
      Spacer(modifier = Modifier.width(6.dp))
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
fun P3Badge(
  text: String,
  color: Color = P3ElectricCyan,
  textColor: Color = P3VoidBlack
) {
  Box(
    modifier = Modifier
      .clip(P3AngledBadgeShape)
      .background(color)
      .padding(horizontal = 12.dp, vertical = 4.dp)
  ) {
    Text(
      text = text.uppercase(),
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = textColor
    )
  }
}

@Composable
fun P3Header(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .width(5.dp)
          .height(26.dp)
          .background(P3ElectricCyan)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Black,
          letterSpacing = 1.2.sp
        ),
        color = P3IceBlue
      )
      Spacer(modifier = Modifier.weight(1f))
      Box(
        modifier = Modifier
          .height(2.dp)
          .weight(0.5f, fill = false)
          .width(60.dp)
          .background(
            Brush.horizontalGradient(
              listOf(P3ElectricCyan, Color.Transparent)
            )
          )
      )
    }
    if (subtitle != null) {
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = P3SilverGrey,
        modifier = Modifier.padding(start = 15.dp, top = 2.dp)
      )
    }
  }
}

@Composable
fun P3Dialog(
  show: Boolean,
  onDismiss: () -> Unit,
  title: String,
  message: String,
  confirmButtonText: String = "ENTENDIDO",
  onConfirm: () -> Unit = onDismiss,
  isWarning: Boolean = true
) {
  if (show) {
    AlertDialog(
      onDismissRequest = onDismiss,
      containerColor = P3TartarusDark,
      titleContentColor = if (isWarning) P3AmberAccent else P3ElectricCyan,
      textContentColor = P3IceBlue,
      shape = RoundedCornerShape(12.dp),
      icon = {
        Icon(
          imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.Info,
          contentDescription = null,
          tint = if (isWarning) P3AmberAccent else P3ElectricCyan,
          modifier = Modifier.size(36.dp)
        )
      },
      title = {
        Text(
          text = title.uppercase(),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Text(
          text = message,
          style = MaterialTheme.typography.bodyMedium,
          lineHeight = 20.sp
        )
      },
      confirmButton = {
        TextButton(onClick = onConfirm) {
          Text(
            text = confirmButtonText.uppercase(),
            color = P3ElectricCyan,
            fontWeight = FontWeight.Bold
          )
        }
      }
    )
  }
}

// Illustrative DualShock 2 PS2 controller preview for the Controls Settings screen
@Composable
fun P3Ps2ControllerPreview(
  modifier: Modifier = Modifier,
  virtualOpacity: Float = 0.85f,
  virtualSizeScale: Float = 1.0f
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(220.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(P3VoidBlack)
      .border(1.dp, P3ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
      .padding(12.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val cx = w / 2f
      val cy = h / 2f

      // Controller main body outline
      val bodyPath = Path().apply {
        moveTo(cx - 140f, cy - 45f)
        lineTo(cx + 140f, cy - 45f)
        cubicTo(cx + 190f, cy - 45f, cx + 200f, cy + 20f, cx + 180f, cy + 65f)
        lineTo(cx + 150f, cy + 80f)
        cubicTo(cx + 110f, cy + 85f, cx + 80f, cy + 45f, cx + 55f, cy + 45f)
        lineTo(cx - 55f, cy + 45f)
        cubicTo(cx - 80f, cy + 45f, cx - 110f, cy + 85f, cx - 150f, cy + 80f)
        lineTo(cx - 180f, cy + 65f)
        cubicTo(cx - 200f, cy + 20f, cx - 190f, cy - 45f, cx - 140f, cy - 45f)
        close()
      }

      // Draw controller body
      drawPath(
        path = bodyPath,
        color = P3DeepNavy.copy(alpha = 0.9f)
      )
      drawPath(
        path = bodyPath,
        color = P3ElectricCyan.copy(alpha = 0.5f),
        style = Stroke(width = 2.dp.toPx())
      )

      // Center PS2 Logo mark
      drawCircle(
        color = P3SurfaceNavy,
        radius = 16f,
        center = Offset(cx, cy - 10f)
      )

      // Analog Sticks
      val leftStick = Offset(cx - 55f, cy + 20f)
      val rightStick = Offset(cx + 55f, cy + 20f)
      val stickRadius = 24f * virtualSizeScale

      drawCircle(
        color = P3TartarusDark,
        radius = stickRadius + 4f,
        center = leftStick
      )
      drawCircle(
        color = P3SoftCyan.copy(alpha = virtualOpacity),
        radius = stickRadius,
        center = leftStick,
        style = Stroke(width = 2.dp.toPx())
      )

      drawCircle(
        color = P3TartarusDark,
        radius = stickRadius + 4f,
        center = rightStick
      )
      drawCircle(
        color = P3SoftCyan.copy(alpha = virtualOpacity),
        radius = stickRadius,
        center = rightStick,
        style = Stroke(width = 2.dp.toPx())
      )

      // D-Pad on the left
      val dpadCenter = Offset(cx - 120f, cy - 5f)
      val dpadArm = 18f * virtualSizeScale
      // Up, Down, Left, Right
      drawRect(
        color = P3ElectricCyan.copy(alpha = virtualOpacity),
        topLeft = Offset(dpadCenter.x - 7f, dpadCenter.y - dpadArm - 10f),
        size = androidx.compose.ui.geometry.Size(14f, dpadArm)
      )
      drawRect(
        color = P3ElectricCyan.copy(alpha = virtualOpacity),
        topLeft = Offset(dpadCenter.x - 7f, dpadCenter.y + 10f),
        size = androidx.compose.ui.geometry.Size(14f, dpadArm)
      )
      drawRect(
        color = P3ElectricCyan.copy(alpha = virtualOpacity),
        topLeft = Offset(dpadCenter.x - dpadArm - 10f, dpadCenter.y - 7f),
        size = androidx.compose.ui.geometry.Size(dpadArm, 14f)
      )
      drawRect(
        color = P3ElectricCyan.copy(alpha = virtualOpacity),
        topLeft = Offset(dpadCenter.x + 10f, dpadCenter.y - 7f),
        size = androidx.compose.ui.geometry.Size(dpadArm, 14f)
      )

      // Face Buttons on the right (Triangle, Circle, Cross, Square)
      val faceCenter = Offset(cx + 120f, cy - 5f)
      val dist = 22f * virtualSizeScale
      val btnRadius = 8f * virtualSizeScale

      // Triangle (Top - Green)
      drawCircle(
        color = Ps2TriangleGreen.copy(alpha = virtualOpacity),
        radius = btnRadius,
        center = Offset(faceCenter.x, faceCenter.y - dist)
      )
      // Circle (Right - Red)
      drawCircle(
        color = Ps2CircleRed.copy(alpha = virtualOpacity),
        radius = btnRadius,
        center = Offset(faceCenter.x + dist, faceCenter.y)
      )
      // Cross (Bottom - Blue)
      drawCircle(
        color = Ps2CrossBlue.copy(alpha = virtualOpacity),
        radius = btnRadius,
        center = Offset(faceCenter.x, faceCenter.y + dist)
      )
      // Square (Left - Pink)
      drawCircle(
        color = Ps2SquarePink.copy(alpha = virtualOpacity),
        radius = btnRadius,
        center = Offset(faceCenter.x - dist, faceCenter.y)
      )

      // Shoulder buttons (L1/R1, L2/R2)
      drawRoundRect(
        color = P3ElectricCyan.copy(alpha = 0.6f),
        topLeft = Offset(cx - 150f, cy - 55f),
        size = androidx.compose.ui.geometry.Size(40f, 8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
      )
      drawRoundRect(
        color = P3ElectricCyan.copy(alpha = 0.6f),
        topLeft = Offset(cx + 110f, cy - 55f),
        size = androidx.compose.ui.geometry.Size(40f, 8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
      )
    }

    // Overlay legend
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "DUALSHOCK 2 CONTROLLER MAPPING",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = P3SilverGrey
      )
      Text(
        text = "Tamanho: ${(virtualSizeScale * 100).toInt()}% • Opacidade: ${(virtualOpacity * 100).toInt()}%",
        style = MaterialTheme.typography.bodySmall,
        color = P3ElectricCyan
      )
    }
  }
}
