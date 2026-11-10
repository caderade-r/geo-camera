package edu.uark.cm288.geo_camera.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uark.cm288.geo_camera.data.PhotoEntry
import edu.uark.cm288.geo_camera.data.PhotoRepository
import kotlinx.coroutines.launch

/**
 * [ViewModel] for managing camera capture operations and persisting new photo entries.
 *
 * @property repository The [PhotoRepository] data repository.
 */
class CameraViewModel(private val repository: PhotoRepository) : ViewModel() {

    /**
     * Inserts a captured [PhotoEntry] into the database asynchronously using [viewModelScope].
     *
     * @param photo The [PhotoEntry] containing path, coordinates, timestamp, and description.
     */
    fun insertPhoto(photo: PhotoEntry) {
        viewModelScope.launch {
            repository.insert(photo)
        }
    }
}
