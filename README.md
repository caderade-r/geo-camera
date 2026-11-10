# Geo-Camera

Geo-Camera is an Android application that allows users to capture photographs using the camera sensor, 
automatically associate them with the device's current geographical location (latitude/longitude), timestamp,
and a text description, and view them on an interactive Google Maps with clickable markers.

---

## 🌟 Key Features

1. **Interactive Google Maps**: Automatically loads and populates map markers at the precise GPS coordinates of all captured photographs, featuring auto-centering and info window previews.
2. **Live Camera Capture**: Integrates system camera capture (`FileProvider` / permanent internal storage) with user-entered text descriptions.
3. **Geolocation & Metadata Association**: Records latitude, longitude, and timestamps for each photograph using Google Play Services Fused Location Provider.
4. **Marker Click Details**: Displays a detailed dialog showing the photo thumbnail, description, timestamp, and coordinates when any map marker is clicked.
5. **Firebase & Google Services**: Integrated with `google-services.json` and Google Maps SDK.

---

## 🏗️ Architecture (MVVM)

The project follows clean **MVVM (Model-View-ViewModel)** architecture:
- **Model**: Room Database (`PhotoEntry`, `PhotoDao`, `AppDatabase` with automatic first-launch sample seeding, and `PhotoRepository`) for local persistence.
- **ViewModel**: `CameraViewModel` and `MapViewModel` handling asynchronous business logic via Kotlin Coroutines and LiveData/Flow.
- **View**: `MainActivity`, `MapFragment`, `CameraFragment`, and `PhotoDetailDialog` displaying UI components and observing state.

---

## 🔒 Security Best Practices

To protect your API keys in public repositories, the Google Maps API key is decoupled from version control:
- Stored securely in your local **`local.properties`** file (`MAPS_API_KEY=...`), which is ignored by `.gitignore`.
- Dynamically read by `app/build.gradle.kts` at build time and injected into `AndroidManifest.xml` via manifest placeholders (`${MAPS_API_KEY}`).

---

## 🚀 Getting Started

### Prerequisites
- Android Studio
- Android SDK / Emulator (configured with Google Play Services, e.g. Google Pixel 9)
- Google Maps API Key

### Setup Instructions
1. **Clone or Open the Project** in Android Studio.
2. **Configure Google Maps API Key**:
   - Open `local.properties` at the project root.
   - Add your API key: `MAPS_API_KEY=YOUR_VALID_API_KEY`.
3. **Firebase Configuration**:
   - Ensure `app/google-services.json` is present.
4. **Build & Run**:
   - Click the green **Run (`▶`)** button in Android Studio to build and deploy.
