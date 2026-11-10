package edu.uark.cm288.geo_camera.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import edu.uark.cm288.geo_camera.R
import edu.uark.cm288.geo_camera.data.AppDatabase
import edu.uark.cm288.geo_camera.data.PhotoEntry
import edu.uark.cm288.geo_camera.data.PhotoRepository
import edu.uark.cm288.geo_camera.util.LocationHelper
import edu.uark.cm288.geo_camera.util.ViewModelFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [Fragment] responsible for allowing the user to enter a photo description,
 * request runtime permissions, launch the camera live capture intent, retrieve current GPS coordinates,
 * and persist the photo entry to the local database.
 */
class CameraFragment : Fragment() {

    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var locationHelper: LocationHelper
    private lateinit var etDescription: EditText
    private lateinit var btnCapture: Button

    /** Shared file reference for the captured photo image */
    private var photoFile: File? = null

    /** URI for the captured photo file provided by FileProvider */
    private var photoUri: Uri? = null

    /** Activity result launcher for requesting runtime permissions */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (!cameraGranted || !locationGranted) {
            Toast.makeText(requireContext(), "Camera and Location permissions are required", Toast.LENGTH_SHORT).show()
        }
    }

    /** Activity result launcher for capturing an image via camera intent */
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoFile != null) {
            savePhotoEntry()
        } else {
            Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Inflates the fragment layout and initializes ViewModels, LocationHelper, and UI listeners.
     *
     * @param inflater LayoutInflater to inflate views in fragment.
     * @param container Parent view group.
     * @param savedInstanceState Bundle containing saved state.
     * @return The root view of the inflated layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_camera, container, false)
        val repository = PhotoRepository(AppDatabase.getDatabase(requireContext()).photoDao())
        val factory = ViewModelFactory(repository)
        cameraViewModel = ViewModelProvider(this, factory)[CameraViewModel::class.java]
        locationHelper = LocationHelper(requireContext())

        etDescription = root.findViewById(R.id.et_description)
        btnCapture = root.findViewById(R.id.btn_capture)

        btnCapture.setOnClickListener {
            checkPermissionsAndCapture()
        }

        return root
    }

    /**
     * Checks if camera and fine location permissions are granted; requests them if missing,
     * otherwise proceeds to launch the camera.
     */
    private fun checkPermissionsAndCapture() {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val locationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED || locationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            launchCamera()
        }
    }

    /**
     * Prepares internal storage file URI and launches the system camera intent.
     */
    private fun launchCamera() {
        val description = etDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a photo description", Toast.LENGTH_SHORT).show()
            return
        }

        photoFile = File(requireContext().filesDir, "geo_photo_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile!!
        )

        takePictureLauncher.launch(photoUri!!)
    }

    /**
     * Fetches current GPS location, associates timestamp and description,
     * and saves the [PhotoEntry] via [CameraViewModel] to the Room database.
     */
    private fun savePhotoEntry() {
        val description = etDescription.text.toString().trim()
        val path = photoFile?.absolutePath ?: return
        val timestamp = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch {
            val location = locationHelper.getCurrentLocation()
            val lat = location?.latitude ?: 36.0626 // default fallback (Fayetteville, AR)
            val lng = location?.longitude ?: -94.1574

            val entry = PhotoEntry(
                photoPath = path,
                latitude = lat,
                longitude = lng,
                timestamp = timestamp,
                description = description
            )

            cameraViewModel.insertPhoto(entry)
            Toast.makeText(requireContext(), "Photo saved successfully!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.mapFragment)
        }
    }
}
