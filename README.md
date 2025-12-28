# Anime Showcase App

Hello! This is a simple yet robust Android application I built to showcase modern Android development practices. It fetches and displays the "Top 25 Anime" using the Jikan API.

I created this project to demonstrate my understanding of building clean, offline-first Android apps using both XML (Classic) and Jetpack Compose (Modern) UI toolkits.

---

## Key Features

*   **Dual UI Implementation:**
    *   You can toggle between the classic XML-based UI and the modern Jetpack Compose UI right from the home screen. This shows I can work with both legacy and new codebases.
*   **Offline First:**
    *   The app works perfectly without internet! I used Room Database to cache data locally.
    *   If you open the app offline, it shows the last saved data instantly.
*   **Smart Error Handling:**
    *   It handles tricky scenarios like API Rate Limits (429) and network failures gracefully.
    *   If you lose internet, the app waits and automatically refreshes the moment you come back online.
*   **Main Cast & Details:**
    *   Tap on any anime to see its synopsis, rating, episodes, and a horizontal scrollable list of the Main Cast members.
    *   It uses a "progressive loading" technique—cached details show up instantly while the app fetches fresh data in the background.

## Tech Stack

I chose a solid, production-ready stack for this project:

*   **Language:** Kotlin
*   **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern
*   **Network:** Retrofit + OkHttp
*   **Database:** Room (SQLite)
*   **Image Loading:** Glide (for XML) & Coil (for Compose)
*   **Asynchronous:** Kotlin Coroutines & Flow

## How to Run

1.  Clone this repository.
2.  Open it in Android Studio.
3.  Let Gradle sync (it might take a minute).
4.  Run it on an Emulator or a real device.

*Note: You don't need any API keys for Jikan, but please be mindful that the API has a rate limit, which the app handles automatically.*

---

Thanks for checking out my work! Feel free to reach out if you have any questions about the implementation.
