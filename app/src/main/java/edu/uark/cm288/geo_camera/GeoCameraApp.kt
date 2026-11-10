package edu.uark.cm288.geo_camera

import android.app.Application

/**
 * Global Application base class for Geo-Camera.
 * Responsible for application-wide lifecycle initialization.
 */
class GeoCameraApp : Application() {

    /**
     * Called when the application is starting, before any activity, service, or receiver objects have been created.
     */
    override fun onCreate() {
        super.onCreate()
    }
}
