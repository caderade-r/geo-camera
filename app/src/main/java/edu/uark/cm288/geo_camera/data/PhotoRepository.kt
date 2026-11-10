package edu.uark.cm288.geo_camera.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository class abstracting access to [PhotoDao] operations.
 * Acts as a clean API for data fetching and mutations across ViewModels.
 *
 * @property photoDao The [PhotoDao] instance used for database interaction.
 */
class PhotoRepository(private val photoDao: PhotoDao) {

    /**
     * Observable stream of all stored photo entries.
     */
    val allPhotos: Flow<List<PhotoEntry>> = photoDao.getAllPhotos()

    /**
     * Inserts a new [PhotoEntry] into the database.
     *
     * @param photo The [PhotoEntry] to insert.
     */
    suspend fun insert(photo: PhotoEntry) {
        photoDao.insertPhoto(photo)
    }

    /**
     * Retrieves photo entries matching the specified geographic coordinates.
     *
     * @param lat Latitude coordinate.
     * @param lng Longitude coordinate.
     * @return List of matching [PhotoEntry] items.
     */
    suspend fun getPhotosAtLocation(lat: Double, lng: Double): List<PhotoEntry> {
        return photoDao.getPhotosAtLocation(lat, lng)
    }
}
