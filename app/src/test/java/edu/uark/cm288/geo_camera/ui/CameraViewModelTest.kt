package edu.uark.cm288.geo_camera.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.uark.cm288.geo_camera.data.PhotoDao
import edu.uark.cm288.geo_camera.data.PhotoEntry
import edu.uark.cm288.geo_camera.data.PhotoRepository
import edu.uark.cm288.geo_camera.ui.camera.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CameraViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePhotoDao: FakePhotoDao
    private lateinit var photoRepository: PhotoRepository
    private lateinit var cameraViewModel: CameraViewModel

    class FakePhotoDao : PhotoDao {
        val insertedPhotos = mutableListOf<PhotoEntry>()
        override fun getAllPhotos(): Flow<List<PhotoEntry>> = flowOf(insertedPhotos)
        override suspend fun insertPhoto(photo: PhotoEntry) {
            insertedPhotos.add(photo)
        }
        override suspend fun getPhotosAtLocation(lat: Double, lng: Double): List<PhotoEntry> {
            return insertedPhotos.filter { it.latitude == lat && it.longitude == lng }
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePhotoDao = FakePhotoDao()
        photoRepository = PhotoRepository(fakePhotoDao)
        cameraViewModel = CameraViewModel(photoRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInsertPhoto() = runTest {
        val entry = PhotoEntry(
            id = 1L,
            photoPath = "path.jpg",
            latitude = 10.0,
            longitude = 20.0,
            timestamp = 123456L,
            description = "Desc"
        )
        cameraViewModel.insertPhoto(entry)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, fakePhotoDao.insertedPhotos.size)
        assertEquals("Desc", fakePhotoDao.insertedPhotos[0].description)
    }
}
