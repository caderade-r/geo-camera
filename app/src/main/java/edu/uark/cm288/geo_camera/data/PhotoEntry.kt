package edu.uark.cm288.geo_camera.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a captured photo with its geographical location, timestamp, and description.
 */
@Entity(tableName = "photos")
data class PhotoEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val description: String
)
