package edu.uark.cm288.geo_camera.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.uark.cm288.geo_camera.R
import edu.uark.cm288.geo_camera.data.AppDatabase
import edu.uark.cm288.geo_camera.data.PhotoRepository
import edu.uark.cm288.geo_camera.util.ViewModelFactory

/**
 * [Fragment] responsible for displaying the Google Map, observing stored photo entries,
 * rendering geographical map markers with timestamps and descriptions, and handling marker click interactions.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel

    /** Shared GoogleMap instance */
    private var googleMap: GoogleMap? = null

    /**
     * Inflates fragment layout and initializes ViewModel and repository.
     *
     * @param inflater LayoutInflater.
     * @param container Parent view group.
     * @param savedInstanceState Bundle saved state.
     * @return The inflated root view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        val repository = PhotoRepository(AppDatabase.getDatabase(requireContext()).photoDao())
        val factory = ViewModelFactory(repository)
        mapViewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]
        return root
    }

    /**
     * Initializes the [SupportMapFragment] asynchronously.
     *
     * @param view Root view.
     * @param savedInstanceState Saved state bundle.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    /**
     * Callback triggered when the Google Map is ready for interaction.
     * Configures location layers, marker click listeners, and observes database photo updates.
     *
     * @param map The initialized [GoogleMap] instance.
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                googleMap?.isMyLocationEnabled = true
            } catch (e: Exception) {
                // Ignore if location layer fails
            }
        }

        // Handle marker clicks to show photo details dialog
        googleMap?.setOnMarkerClickListener { marker ->
            val photoEntry = marker.tag as? edu.uark.cm288.geo_camera.data.PhotoEntry
            if (photoEntry != null) {
                PhotoDetailDialog(photoEntry).show(parentFragmentManager, "PhotoDetailDialog")
                true
            } else {
                false
            }
        }

        // Observe photo entries from Room database and populate map markers
        mapViewModel.allPhotosLiveData.observe(viewLifecycleOwner) { photos ->
            googleMap?.clear()
            var lastMarker: com.google.android.gms.maps.model.Marker? = null
            var lastLatLng: LatLng? = null
            for (photo in photos) {
                val latLng = LatLng(photo.latitude, photo.longitude)
                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                val timeString = dateFormat.format(java.util.Date(photo.timestamp))
                val marker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(photo.description)
                        .snippet(timeString)
                )
                marker?.tag = photo
                lastMarker = marker
                lastLatLng = latLng
            }
            if (lastLatLng != null && googleMap != null) {
                googleMap?.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(lastLatLng, 15f))
                view?.handler?.postDelayed({
                    lastMarker?.showInfoWindow()
                }, 400)
            }
        }
    }
}
