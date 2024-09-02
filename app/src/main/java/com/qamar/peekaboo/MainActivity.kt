package com.qamar.peekaboo

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.atan2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppIcons()
        }
    }
}

@Composable
fun MyAppIcons() {
    val context = LocalContext.current
    val sensorManager = ContextCompat.getSystemService(context, SensorManager::class.java)!!
    val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animationSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = 200f) // Faster spring

    var isRotated by remember { mutableStateOf(false) }

    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                val values = event.values

                // Calculate the angle of rotation around the Z-axis (vertical)
                val rotationAngle = Math.toDegrees(atan2(values[1], values[0]).toDouble())

                // Check if the device is rotated more than 45 degrees
                isRotated = abs(rotationAngle) > 45f // Adjust threshold as needed

                // Update animation progress based on 'isRotated'
                animationProgress = if (isRotated) 1f else 0f
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    LaunchedEffect(Unit) {
        sensorManager.registerListener(
            sensorEventListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(
                listOf(
                    "Icon 1", "Icon 2", "Icon 3", "Icon 4",
                    "Icon 1", "Icon 2", "Icon 3", "Icon 4",
                )
            ) { iconName ->
                AnimatedIcon(iconName, animationProgress, animationSpec)
            }
        }
    }
}

@Composable
fun AnimatedIcon(
    iconName: String,
    progress: Float,
    animationSpec: AnimationSpec<Float>
) {
    // Animate the X-offset for displacement
    val animatedXOffset by animateFloatAsState(
        targetValue = if (progress == 1f) 32.dp.value else 0f,
        animationSpec = animationSpec, label = "",
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color.Red, shape = RoundedCornerShape(15.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with your icon
            contentDescription = iconName,
            modifier = Modifier
                .fillMaxSize()
                .size(64.dp)
                .clip(RoundedCornerShape(15.dp))
                .offset(
                    x =
                    if (iconName == "Icon 4")
                        -animatedXOffset.dp else 0.dp, y = 0.dp
                ), // Apply X-offset animation
            alignment = Alignment.Center,
            contentScale = ContentScale.FillBounds
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAppIcons()
}