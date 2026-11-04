package com.example.identity_dissolution_appmovil.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.identity_dissolution_appmovil.R
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.time.Duration.Companion.milliseconds

/**
 * Creado por Bryan el 31/10/2026 a las 04:30 p.m..
 * Proyecto: Identity_DIssolution_AppMovil
 */

@Composable
fun Welcome(){
    val familiFont =
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        for (i in 1..100){
            currentProgress = i / 100f
            delay(30.milliseconds)
        }
    }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = Color.Black)
        .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {


        val borderWith = 6.dp
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Welcome",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp)
                .border(
                    BorderStroke(borderWith, Color.Red),
                    CircleShape
                )
                .padding(borderWith )
                .clip(CircleShape)
        )

        Spacer(Modifier.height(15.dp))

        Text(text = "HEARTEBEAT",
            color = Color.Red,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(5.dp))

        Text(text = "COMPANION",
            color = Color.Red,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(15.dp))

        LoadLine(
            progress = {currentProgress}
        )

        Spacer(Modifier.height(15.dp))


        Row() {

            Image(painter = painterResource(id = R.drawable.corazon),
                contentDescription = null,
                modifier = Modifier.width(30.dp)

            )




        }

    }

}

// Preloading
// Cargar las pantallas menos la de bestiario(Mounstruos)

@Composable
fun LoadLine(progress: () -> Float) {


    LinearProgressIndicator(
        progress = progress,
        color = Color.Red,
        trackColor = Color.DarkGray,
        modifier = Modifier.width(250.dp).height(4.dp)

    )
}




@Preview(showSystemUi = true)
@Composable
fun ScreenWelcome(){
    Welcome()


}
