package edu.uark.cm288.geo_camera.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database abstract class for storing geo-tagged photo entries locally.
 */
@Database(entities = [PhotoEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to the [PhotoDao] operations.
     */
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
        * Returns the singleton instance of the [AppDatabase], initializing it if necessary
        * and seeding an initial sample photo on first database creation.
        *
        * @param context Application context.
        * @return The singleton [AppDatabase] instance.
        */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "geo_camera_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.photoDao().insertPhoto(
                                        PhotoEntry(
                                            photoPath = "",
                                            latitude = 36.0626,
                                            longitude = -94.1574,
                                            timestamp = System.currentTimeMillis(),
                                            description = "Sample Captured Photo at University of Arkansas"
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
