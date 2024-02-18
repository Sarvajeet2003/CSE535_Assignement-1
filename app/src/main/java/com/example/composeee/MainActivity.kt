package com.example.composeee
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.atomic.AtomicInteger
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val journeyManager = JourneyManager(this)
        journeyManager.initialize()

        setContent {
            MainScreen(journeyManager, context = this)
        }
    }
}

@Composable
fun MainScreen(journeyManager: JourneyManager, context: Context) {
    var isDistanceInKm by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { journeyManager.moveToNextStop() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Next Stop")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Show in ")
            Switch(
                checked = isDistanceInKm,
                onCheckedChange = { isDistanceInKm = it },
                modifier = Modifier.padding(8.dp)
            )
            Text(text = if (isDistanceInKm) "km" else "miles")
        }

        if (isDistanceInKm) {
            journeyManager.updateDistanceKmTextViews()
            Text(text = journeyManager.distanceCoveredText)
            Text(text = journeyManager.distanceLeftText)
        } else {
            journeyManager.updateDistanceMilesTextViews()
            Text(text = journeyManager.distanceCoveredMilesText)
            Text(text = journeyManager.distanceLeftMilesText)
        }

        JourneyProgress(journeyManager)

        StopsList(journeyManager, context = context)
    }
}

@Composable
fun JourneyProgress(journeyManager: JourneyManager) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        val progressText = journeyManager.progressText
        val progressValue = extractProgressValue(progressText)
        LinearProgressIndicator(progress = progressValue / 100f)
        Text(text = progressText, fontSize = 20.sp)
        Text(text = journeyManager.distanceCoveredText)
        Text(text = journeyManager.distanceLeftText)
    }
}

fun extractProgressValue(progressText: String): Int {
    val regex = Regex("""\d+""") // Matches one or more digits
    val matchResult = regex.find(progressText)
    return matchResult?.value?.toIntOrNull() ?: 0
}

@Composable
fun StopsList(journeyManager: JourneyManager, context: Context) {
    val stops = remember { journeyManager.getSampleStops().toMutableList() }
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(stops.size) { index ->
            Text(text = stops[index].name)
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    val context = LocalContext.current
    MainScreen(journeyManager = JourneyManager(MainActivity()), context = context)
}

class JourneyManager(private val activity: ComponentActivity) {
    private var isDistanceInKm = true
    var distanceCoveredText by mutableStateOf("")
    var distanceLeftText by mutableStateOf("")
    var progressText by mutableStateOf("")
    private lateinit var sampleStops: List<Stop>
    private var totalDistance: Int = 0
    private val currentStopIndex = AtomicInteger(0)
    var distanceCoveredMilesText by mutableStateOf("")
    var distanceLeftMilesText by mutableStateOf("")

    fun updateDistanceMilesTextViews() {
        val currentStop = sampleStops[currentStopIndex.get()]
        val distanceCovered = currentStop.distance
        val distanceLeft = totalDistance - distanceCovered

        distanceCoveredMilesText = "Distance Covered: ${distanceCovered * 0.621371} miles"
        distanceLeftMilesText = "Distance Left: ${distanceLeft * 0.621371} miles"
    }
    fun updateDistanceKmTextViews() {
        val currentStop = sampleStops[currentStopIndex.get()]
        val distanceCovered = currentStop.distance
        val distanceLeft = totalDistance - distanceCovered

        distanceCoveredText = "Distance Covered: $distanceCovered km"
        distanceLeftText = "Distance Left: $distanceLeft km"
    }


    data class Stop(val name: String, val distance: Int)

    fun initialize() {
        sampleStops = getSampleStops()
        totalDistance = calculateTotalDistance(sampleStops)
        updateDistanceTextViews()
        updateProgress(0)
    }

    fun moveToNextStop() {
        if (currentStopIndex.get() < sampleStops.size) {
            val currentStop = sampleStops[currentStopIndex.getAndIncrement()]
            val distanceCovered = currentStop.distance
            updateDistanceTextViews()
            updateProgress(distanceCovered)
        } else {
            println("Journey completed!")
        }
    }

    private fun updateProgress(distanceCovered: Int) {
        val progress = if (currentStopIndex.get() < sampleStops.size) {
            (currentStopIndex.get().toDouble() / sampleStops.size.toDouble() * 100).toInt()
        } else {
            100
        }
        progressText = "Progress: $progress%"
    }

    private fun updateDistanceTextViews() {
        val currentStop = sampleStops[currentStopIndex.get()]
        val distanceCovered = currentStop.distance
        val distanceLeft = totalDistance - distanceCovered

        distanceCoveredText = if (isDistanceInKm) {
            "Distance Covered: $distanceCovered km"
        } else {
            val distanceCoveredMiles = distanceCovered * 0.621371
            "Distance Covered: $distanceCoveredMiles miles"
        }

        distanceLeftText = if (isDistanceInKm) {
            "Distance Left: $distanceLeft km"
        } else {
            val distanceLeftMiles = distanceLeft * 0.621371
            "Distance Left: $distanceLeftMiles miles"
        }
    }

    private fun calculateTotalDistance(stops: List<Stop>): Int {
        if (stops.isEmpty()) {
            return 0
        }
        return stops.last().distance
    }

    fun getSampleStops(): List<Stop> {
        return listOf(
            Stop("Source: Delhi\n", 0),
            Stop("Stop2. Agra, Uttar Pradesh\n", 150),
            Stop("Stop3. Jaipur, Rajasthan\n", 280),
            Stop("Stop4. Kota, Rajasthan\n", 350),
            Stop("Stop5. Ajmer, Rajasthan\n", 415),
            Stop("Stop6. Udaipur, Rajasthan\n", 550),
            Stop("Stop7. Ahmedabad, Gujarat\n", 945),
            Stop("Stop8. Vadodara, Gujarat\n", 1100),
            Stop("Stop9. Surat, Gujarat\n", 1300),
            Stop("Stop10. Mumbai, Maharashtra\n", 1475),
            Stop("Stop11. Thane, Maharashtra\n", 1600),
            Stop("Stop12. Pune, Maharashtra\n", 2005),
            Stop("Stop13. Satara, Maharashtra\n", 2150),
            Stop("Stop14. Kolhapur, Maharashtra\n", 2250),
            Stop("Stop15. Belgaum, Karnataka\n", 2315),
            Stop("Stop16. Dharwad, Karnataka\n", 2400),
            Stop("Stop17. Hubli, Karnataka\n", 2480),
            Stop("Stop18. Davanagere, Karnataka\n", 2550),
            Stop("Stop19. Tumkur, Karnataka\n", 2600),
            Stop("Stop20. Hosur, Tamil Nadu\n", 2650),
            Stop("Stop21. Bangalore, Karnataka\n", 2700),
            Stop("Stop22. Electronic City, Karnataka\n", 2720),
            Stop("Stop23. Mysuru, Karnataka\n", 2800),
            Stop("Stop24. Ooty, Tamil Nadu\n", 3000),
            Stop("Stop25. Coimbatore, Tamil Nadu\n", 3100),
            Stop("Stop26. Salem, Tamil Nadu\n", 3200),
            Stop("Stop27. Erode, Tamil Nadu\n", 3300),
            Stop("Stop28. Tiruppur, Tamil Nadu\n", 3400),
            Stop("Stop29. Coonoor, Tamil Nadu\n", 3500),
            Stop("Stop30. Palakkad, Kerala\n", 3600),
            Stop("Stop31. Thrissur, Kerala\n", 3700),
            Stop("Stop32. Kochi, Kerala\n", 3800),
            Stop("Stop33. Alappuzha, Kerala\n", 3900),
            Stop("Stop34. Kollam, Kerala\n", 4000),
            Stop("Stop35. Trivandrum, Kerala\n", 4100),
            Stop("Destination: Kanyakumari, Tamil Nadu\n", 4200)
        )
    }
}