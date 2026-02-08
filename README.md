# Aniverse - Android Anime Explorer

Aniverse is a modern Android application that allows users to explore the world of Anime using the Jikan API (MyAnimeList). It features a polished UI, offline support, and seamless media playback.

## 🏗️ Architecture
The app follows **Modern Android Development (MAD)** practices and **Clean Architecture** principles, structured into three layers:

*   **Data Layer**: Handles data retrieval from the Network (Retrofit) and Local Database (Room). It implements the "Single Source of Truth" pattern.
*   **Domain Layer**: Contains the business logic and Use Cases (though simple mapping is done in the repository for this scale).
*   **UI Layer**: Follows the **MVVM (Model-View-ViewModel)** pattern. ViewModels expose `StateFlow` or `LiveData` to the UI, ensuring reactivity and lifecycle awareness.

### Key Libraries
*   **Hilt**: Dependency Injection.
*   **XML**: Code uses XML ViewBinding.
*   **Paging 3**: For efficient pagination of large lists.
*   **Room**: Local database for offline caching.
*   **Retrofit & Moshi**: Network requests and JSON parsing.
*   **Glide**: Image loading.
*   **Android YouTube Player**: For playing trailers.

## 💾 Caching Strategy (Offline Support)
We implemented a robust **Offline-First** capability using Room and Paging 3.

### 1. RemoteMediator (For Lists)
The `TopAnimeRemoteMediator` coordinates between the API and the Database:
*   **Read**: The UI always observes data from the **Room Database** (Single Source of Truth).
*   **Write**: When the UI runs out of data, `RemoteMediator` fetches the next page from the API and saves it into Room.
*   **Result**: Users can scroll through previously loaded lists even when offline.

### 2. Repository Pattern (For Details)
For the Details screen, we use a "Stale-While-Revalidate" approach:
1.  **Immediate Cache**: We first query the DB for the specific Anime ID and emit the cached result immediately for a fast UI response.
2.  **Network Refresh**: Simultaneously, we fetch fresh details from the API.
3.  **Update**: Once the network call succeeds, we update the DB, and the UI automatically updates with the latest data.

## 🎬 Video Playback & Fallbacks
We use the `android-youtube-player` library to play trailers directly within the app. However, YouTube restrictions sometimes prevent playback in embedded players.

**Our Handling Strategy:**
1.  **Primary**: Attempt to play the video in the embedded player.
2.  **Fallback**: If the player returns an error (e.g., `VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER` or restricted content):
    *   We automatically **hide the player** and show the **high-quality poster image** instead.
    *   We display a **"Play in YouTube" button** (FAB).
3.  **External Redirect**: Clicking the FAB launches the official YouTube app (or browser) via an Intent to ensure the user can still watch the content.

## 🚀 How to Install
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/aniverse.git
    ```
2.  **Open in Android Studio**:
    *   File -> Open -> Select the project folder.
3.  **Sync Gradle**:
    *   Allow Android Studio to download dependencies.
4.  **Run**:
    *   Connect a device or start an emulator.
    *   Click the **Run** (▶️) button.

## 📱 Features
*   **Top Anime List**: Infinite scrolling list of popular anime.
*   **Detailed View**: Synopsis, ratings, genres, Episodes,synopsis and airing info.
*   **Trailers**: Watch trailers seamlessly
