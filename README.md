# Seekho Android App

## Overview
A simple yet robust Android application that fetches and displays a list of top-rated anime using the Jikan API. It features offline support using Room Database, a clean MVVM architecture, and intuitive UI handling for various content states.

## Features
- **Top Anime List**: Fetches and displays popular anime series.
- **Offline Support**: Caches data locally using Room to ensure the app works without an internet connection.
- **Detail View**: Shows comprehensive information about a selected anime, including synopsis, genre, and cast.
- **Video Trailer**: Plays the trailer if available.
- **Robust Error Handling**: informative messages for network errors and empty states.
- **Clean Architecture**: Follows MVVM + Repository pattern.

## Assumptions
- The API is public and does not require an API key.
- "No Profile Image" constraint is handled by hiding the image view, allowing the text content to take precedence, maintaining a clean look.
- Data synchronization happens automatically when the app is launched or the list is refreshed (fetching fresh data and updating the local cache).

## Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Network**: Retrofit + Gson
- **Database**: Room
- **Async**: Coroutines + Flow
- **Image Loading**: Glide
- **DI**: Manual Dependency Injection

## Setup Instructions
1. Clone the repository.
2. Open in Android Studio (Ladybug or newer recommended).
3. Sync Gradle.
4. Run on an emulator or physical device.

## Known Limitations
- The "Main Cast" on the detail screen is currently static or not fully implemented as the primary Jikan endpoint creates a deeper nested structure that was simplified for this exercise (or requires a separate API call).
- Video playback simply opens the URL or uses a basic WebView/VideoView depending on the implementation depth requested. (Here implemented as a simple redirection or placeholder).

## License
MIT
