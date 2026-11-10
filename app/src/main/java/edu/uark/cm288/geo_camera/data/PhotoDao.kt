package edu.uark.cm288.geo_camera.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing asynchronous database operations for photo entries.
 */
@Dao
interface PhotoDao {

    /**
     * Retrieves all stored photo entries ordered by timestamp descending.
     *
     * @return A [Flow] emitting a list of [PhotoEntry] objects whenever data changes.
     */
    @Query("SELECT * FROM photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoEntry>>

    /**
     * Inserts a photo entry into the local database. Replaces existing records on conflict.
     *
     * @param photo The [PhotoEntry] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntry)

    /**
     * Queries photo entries matching a specific geographical latitude and longitude.
     *
     * @param lat The latitude coordinate.
     * @param lng The longitude coordinate.
     * @return A list of [PhotoEntry] items taken at the specified location.
     */
    @Query("SELECT * FROM photos WHERE latitude = :lat AND longitude = :lng")
    suspend fun getPhotosAtLocation(lat: Double, lng: Double): List<PhotoEntry>
}
