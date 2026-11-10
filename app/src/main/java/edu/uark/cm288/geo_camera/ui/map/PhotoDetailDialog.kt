package edu.uark.cm288.geo_camera.ui.map

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import edu.uark.cm288.geo_camera.R
import edu.uark.cm288.geo_camera.data.PhotoEntry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * [DialogFragment] that displays detailed information about a captured photo
 * (including image thumbnail, description, timestamp, and GPS coordinates) when a map marker is clicked.
 *
 * @property photoEntry The [PhotoEntry] containing the photo details to display.
 */
class PhotoDetailDialog(private val photoEntry: PhotoEntry) : DialogFragment() {

    /**
     * Builds and returns the [AlertDialog] displaying the photo image, description, and metadata.
     *
     * @param savedInstanceState Bundle saved state.
     * @return The configured [Dialog] instance.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_photo_detail, null)
        val imageView = view.findViewById<ImageView>(R.id.iv_photo)
        val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        val tvTimestamp = view.findViewById<TextView>(R.id.tv_timestamp)
        val tvLocation = view.findViewById<TextView>(R.id.tv_location)

        tvDescription.text = photoEntry.description
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        tvTimestamp.text = "Taken: ${sdf.format(Date(photoEntry.timestamp))}"
        tvLocation.text = "Lat: ${photoEntry.latitude}, Lng: ${photoEntry.longitude}"

        val imgFile = File(photoEntry.photoPath)
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            imageView.setImageBitmap(bitmap)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Photo Details")
            .setView(view)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
    }
}
