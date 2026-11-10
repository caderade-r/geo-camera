package edu.uark.cm288.geo_camera.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import edu.uark.cm288.geo_camera.data.PhotoEntry
import edu.uark.cm288.geo_camera.data.PhotoRepository
import kotlinx.coroutines.flow.Flow

/**
 * [ViewModel] for managing map state and observing stored photo entries for map markers.
 *
 * @property repository The [PhotoRepository] data repository.
 */
class MapViewModel(private val repository: PhotoRepository) : ViewModel() {

    /**
     * Cold Flow stream of all stored photo entries.
     */
    val allPhotos: Flow<List<PhotoEntry>> = repository.allPhotos

    /**
     * LiveData stream of all stored photo entries, converted from repository Flow for UI observation.
     */
    val allPhotosLiveData = repository.allPhotos.asLiveData()
}
