package edu.uark.cm288.geo_camera.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PhotoDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var photoDao: PhotoDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        photoDao = database.photoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadPhoto() = runBlocking {
        val photo = PhotoEntry(
            id = 1L,
            photoPath = "/test/path.jpg",
            latitude = 36.0626,
            longitude = -94.1574,
            timestamp = System.currentTimeMillis(),
            description = "Test Photo Description"
        )
        photoDao.insertPhoto(photo)
        val photos = photoDao.getAllPhotos().first()
        assertEquals(photos.size, 1)
        assertEquals(photos[0].description, "Test Photo Description")
        assertEquals(photos[0].latitude, 36.0626, 0.0001)
    }

    @Test
    @Throws(Exception::class)
    fun getPhotosAtLocationTest() = runBlocking {
        val photo = PhotoEntry(
            id = 1L,
            photoPath = "/test/path.jpg",
            latitude = 35.0,
            longitude = -93.0,
            timestamp = System.currentTimeMillis(),
            description = "Location Test"
        )
        photoDao.insertPhoto(photo)
        val found = photoDao.getPhotosAtLocation(35.0, -93.0)
        assertEquals(found.size, 1)
        assertEquals(found[0].description, "Location Test")
    }
}
