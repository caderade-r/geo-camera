package edu.uark.cm288.geo_camera

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import edu.uark.cm288.geo_camera.data.AppDatabase
import edu.uark.cm288.geo_camera.data.PhotoEntry
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry

@RunWith(AndroidJUnit4::class)
class MapMarkerIntegrationTest {

    @Test
    fun testPhotoMarkerCreationAndDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = AppDatabase.getDatabase(context)
        
        // 1. Insert a test photo entry representing a captured photo with geolocation
        val testPhoto = PhotoEntry(
            id = 999L,
            photoPath = "/data/data/edu.uark.cm288.geo_camera/files/test_photo.jpg",
            latitude = 36.0626, // Fayetteville, AR
            longitude = -94.1574,
            timestamp = System.currentTimeMillis(),
            description = "Test Verification Photo Marker"
        )

        runBlocking {
            database.photoDao().insertPhoto(testPhoto)
        }

        // 2. Launch MainActivity (which loads MapFragment and populates markers from DB)
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // 3. Keep the test active so the user can visually confirm the marker on the map screen
        try {
            Thread.sleep(15000) // Display map on screen for 15 seconds for visual confirmation
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        scenario.close()
    }
}
