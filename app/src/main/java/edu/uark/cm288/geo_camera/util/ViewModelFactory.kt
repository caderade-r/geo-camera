package edu.uark.cm288.geo_camera.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.uark.cm288.geo_camera.data.PhotoRepository
import edu.uark.cm288.geo_camera.ui.camera.CameraViewModel
import edu.uark.cm288.geo_camera.ui.map.MapViewModel

/**
 * Custom [ViewModelProvider.Factory] responsible for instantiating ViewModels
 * that require a [PhotoRepository] constructor dependency.
 *
 * @property repository The [PhotoRepository] data repository to inject into ViewModels.
 */
class ViewModelFactory(private val repository: PhotoRepository) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given ViewModel class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A newly created ViewModel instance of type [T].
     * @throws IllegalArgumentException If the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
