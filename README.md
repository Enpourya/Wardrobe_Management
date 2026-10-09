# WardrobeApp 👔

**A Smart, Modern Home Wardrobe Organizer for Android**

WardrobeApp is an Android application designed to help users organize their home wardrobes, drawers, clothing, and personal belongings in one convenient place.

Users can create custom storage locations, add items with photos, search for their belongings instantly, and discover exactly which wardrobe or drawer contains a particular item.

The application also offers an optional AI-powered outfit recommendation feature using the OpenRouter API. By describing an upcoming event, users can receive three outfit suggestions based on the clothing items recorded in their personal wardrobe.

Built with Kotlin and Android's native development tools, WardrobeApp features a modern user interface, Persian and English language support, local database storage, and backup and restore functionality.

## ✨ Features

### 🌐 1. Bilingual Interface: Persian and English

WardrobeApp supports both Persian and English.

- On the first launch, users choose their preferred language.
- The entire interface adapts to the selected language.
- English and Persian translations are maintained separately.
- Persian uses a right-to-left (RTL) layout.
- English uses a left-to-right (LTR) layout.
- Language selection is saved for future sessions.
- Users can change their language through the Settings screen.

The interface is designed to remain consistent and usable across both languages.

### 🏠 2. Manage Wardrobes and Drawers

Users can organize their belongings by creating custom storage locations.

Available functionality includes:

- Add a wardrobe or drawer using the `+` button.
- Select the storage type.
- Assign a short, descriptive name to the storage location.
- View all registered wardrobes and drawers on the home screen.
- Open a storage location to manage its contents.
- Edit or remove storage locations.
- Browse large collections using smooth scrolling.

The storage list uses a grid-based layout designed to prevent scrolling from disrupting other interface elements.

Examples of storage locations include:

- Bedroom Wardrobe
- Winter Clothes Drawer
- Shoe Cabinet
- Accessories Drawer
- Travel Storage

### 👕 3. Manage Clothing and Personal Items

Each wardrobe or drawer can contain multiple registered items.

Users can:

- Add an item with a name and photo.
- Select an existing photo or capture one using the camera, where supported.
- View items in a visual grid.
- Open an item to see its full details.
- Edit item names and images.
- Remove items when they are no longer needed.
- View the storage location associated with each item.

Each item is linked to its corresponding storage location in the database.

Images should be stored locally and associated with their database records. The application should handle image scaling efficiently to avoid excessive memory usage.

### 🔍 4. Real-Time Search

WardrobeApp includes a dedicated search screen for finding registered items quickly.

Users can search by item name and receive matching results as they type.

Search results should display:

- Item name.
- Item photo.
- Associated wardrobe or drawer name.
- A convenient way to open the item details or its storage location.

For example, searching for `Black Jacket` should help the user identify the matching item and determine which wardrobe or drawer contains it.

Search should work with the locally stored database and remain available without an internet connection.

### 🤖 5. Optional AI Outfit Recommendations

WardrobeApp includes an optional AI-powered outfit recommendation feature using OpenRouter.

This feature is available when the user configures a valid OpenRouter API key in Settings.

#### How It Works

1. The user opens the AI section.
2. The user describes an event, occasion, or activity.
3. The application retrieves relevant clothing items from the local wardrobe database.
4. The retrieved items are sent as context to an AI model through OpenRouter.
5. The AI recommends three outfit options based on the available clothing information.
6. The application displays the suggestions in an easy-to-understand format.

Example request:

> I am attending a formal dinner tonight. Suggest three outfits using the clothes I already own.

The AI should prioritize items that actually exist in the user's registered wardrobe rather than recommending unavailable clothing.

Recommendations may consider:

- Occasion and formality.
- Clothing type.
- Color compatibility.
- Available accessories.
- Seasonal suitability, when relevant information is available.

The application should clearly identify the items used in each recommendation and indicate their storage locations when that information is available.

#### Optional Feature

- The application must remain fully functional without an OpenRouter API key.
- AI functionality should be enabled only when a key has been configured.
- Users must be informed when wardrobe information is sent to an external AI service.
- API errors and network failures should be handled gracefully.

### 💾 6. Local Database and Privacy

WardrobeApp uses Room, built on SQLite, for structured local data storage.

The database manages:

- Storage locations.
- Clothing and personal item records.
- Item names and image references.
- Relationships between items and storage locations.

The `WardrobeRepository` provides an abstraction between the user interface and the database layer.

The application is designed to keep wardrobe information on the user's device by default.

Important privacy requirements:

- Do not upload wardrobe data automatically.
- Keep images and database records locally unless the user explicitly requests an operation that requires external sharing.
- Protect API credentials from exposure.
- Avoid logging sensitive information.
- Explain that information used for AI recommendations may be transmitted to OpenRouter.

Local storage does not automatically guarantee encryption. If stronger protection is required, database encryption and secure credential storage should be implemented explicitly.

### 📦 7. Backup and Restore

WardrobeApp provides backup and restoration functionality through the Settings screen.

Users should be able to:

- Create a backup of their wardrobe data.
- Export the backup as a ZIP file.
- Select a previously created backup file.
- Restore the saved data from within the application.
- Recover item records and associated images.

A complete backup should include the database and the image files referenced by its records.

#### Backup Safety

- Validate backup files before restoring them.
- Handle corrupted or incompatible backups gracefully.
- Preserve the current data until the restore operation has been validated.
- Create a safety backup before replacing existing data.
- Restore image references consistently with the corresponding files.
- Avoid overwriting unrelated application files.

The backup format and database schema should be versioned to support future upgrades.

### 🎨 8. Modern User Interface

WardrobeApp uses a modern Android interface with an indigo and gray color palette.

Interface features include:

- Visually appealing storage cards.
- Image-based clothing cards.
- Bottom navigation.
- Floating action buttons for adding items.
- Smooth navigation transitions.
- Consistent spacing and typography.
- Modern dialogs for creating and editing records.
- Responsive layouts for different screen sizes.
- Persian RTL and English LTR layouts.

The application uses separate screens for storage management, item browsing, search, AI recommendations, and settings.

## 🏗️ Project Architecture

WardrobeApp follows a modular architecture that separates data management, business logic, user interface components, and utility functions.

### Project Structure

```text
WardrobeApp/
├── build.gradle
├── settings.gradle
├── gradle.properties
│
└── app/
    ├── build.gradle
    ├── proguard-rules.pro
    │
    └── src/main/
        ├── AndroidManifest.xml
        │
        ├── java/com/pourya/wardrobe/
        │   ├── WardrobeApp.kt
        │   │
        │   ├── data/
        │   │   ├── model/
        │   │   │   └── Models.kt
        │   │   ├── db/
        │   │   │   ├── WardrobeDatabase.kt
        │   │   │   └── Daos.kt
        │   │   └── repository/
        │   │       └── WardrobeRepository.kt
        │   │
        │   ├── ui/
        │   │   ├── language/
        │   │   │   └── LanguageSelectionActivity.kt
        │   │   ├── home/
        │   │   │   └── MainActivity.kt
        │   │   ├── storage/
        │   │   │   ├── StorageFragment.kt
        │   │   │   ├── StorageAdapter.kt
        │   │   │   ├── StorageViewModel.kt
        │   │   │   └── StorageFragmentDirections.kt
        │   │   ├── item/
        │   │   │   ├── ItemFragment.kt
        │   │   │   ├── ItemAdapter.kt
        │   │   │   ├── ItemViewModel.kt
        │   │   │   ├── ItemDetailFragment.kt
        │   │   │   └── ItemFragmentNavHelpers.kt
        │   │   ├── search/
        │   │   │   ├── SearchFragment.kt
        │   │   │   ├── SearchViewModel.kt
        │   │   │   └── SearchResultAdapter.kt
        │   │   ├── ai/
        │   │   │   ├── AIFragment.kt
        │   │   │   └── AIViewModel.kt
        │   │   └── settings/
        │   │       └── SettingsFragment.kt
        │   │
        │   └── utils/
        │       ├── LocaleManager.kt
        │       ├── ImageManager.kt
        │       ├── BackupManager.kt
        │       └── AIService.kt
        │
        └── res/
            ├── anim/
            ├── drawable/
            ├── font/
            ├── layout/
            ├── menu/
            ├── mipmap-hdpi/
            ├── mipmap-mdpi/
            ├── mipmap-xhdpi/
            ├── mipmap-xxhdpi/
            ├── mipmap-xxxhdpi/
            ├── navigation/
            ├── values/
            ├── values-fa/
            └── xml/
```

The resource directories contain the corresponding XML layouts, drawables, animations, navigation graph, launcher icons, colors, themes, translations, and FileProvider configuration.

### Core Components

| Component | Responsibility |
|---|---|
| `WardrobeApp.kt` | Initialize application-level configuration and locale handling. |
| `Models.kt` | Define storage types and storage/item data models. |
| `WardrobeDatabase.kt` | Configure the Room database and type converters. |
| `Daos.kt` | Provide database access operations for storage locations and items. |
| `WardrobeRepository.kt` | Coordinate data access between the UI and database. |
| `LocaleManager.kt` | Manage language selection and locale configuration. |
| `ImageManager.kt` | Save, retrieve, and resize images. |
| `BackupManager.kt` | Create and restore ZIP backups. |
| `AIService.kt` | Communicate with OpenRouter. |
| `AIViewModel.kt` | Manage AI recommendation state and requests. |
| `SearchViewModel.kt` | Perform item searches and resolve storage names. |

## 🧰 Technology Stack

- **Language:** Kotlin
- **Platform:** Android
- **UI:** Android XML layouts and native Android components
- **Database:** Room / SQLite
- **Architecture:** Repository and ViewModel
- **Navigation:** Android Navigation Component
- **AI Integration:** OpenRouter API
- **Backup Format:** ZIP
- **Image Management:** Local image storage and scaling
- **Build System:** Gradle
- **Localization:** Android string resources for English and Persian

The exact Android SDK versions and dependency versions should be defined in the project's Gradle configuration.

## 🚀 Getting Started

### Prerequisites

- Android Studio.
- A compatible JDK.
- Android SDK components required by the project.
- Gradle configuration included in the repository.
- An Android emulator or physical Android device.

An OpenRouter API key is optional and is needed only for AI-powered outfit recommendations.

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/WardrobeApp.git
cd WardrobeApp
```

Replace `YOUR_USERNAME` with your GitHub username.

### 2. Open the Project

Open the `WardrobeApp` directory in Android Studio.

Allow Gradle to synchronize the project and download the dependencies declared in the Gradle files.

### 3. Configure the Android SDK

Ensure that the Android SDK version required by the project is installed.

If needed, configure the local SDK path through Android Studio.

### 4. Run the Application

Select an Android emulator or connect a physical Android device with USB debugging enabled.

Run the application using Android Studio.

Alternatively, build a debug APK from the project root:

```bash
./gradlew assembleDebug
```

On Windows Command Prompt or PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

The resulting APK is typically generated under:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The build commands assume that the Gradle wrapper files are included in the repository.

## 🔑 OpenRouter Configuration

To enable AI-powered outfit recommendations:

1. Create an OpenRouter account.
2. Obtain an API key.
3. Open WardrobeApp.
4. Navigate to Settings.
5. Enter the API key in the designated field.
6. Open the AI section and describe the occasion.

The application should verify the configuration and provide clear feedback if the key is invalid or the service is unavailable.

**Security recommendations:**

- Do not hardcode the API key in the source code.
- Do not commit personal API keys to GitHub.
- Store credentials using Android's secure storage mechanisms where appropriate.
- Avoid including credentials in backups or logs.
- Handle network requests asynchronously so the interface remains responsive.

For stronger protection, applications distributed publicly should also consider the limitations of storing a provider API key directly on a user device.

## 🧭 Application Navigation

The main interface includes four primary navigation destinations:

1. **Wardrobes:** Browse and manage storage locations and their contents.
2. **Search:** Find items and identify their storage locations.
3. **AI:** Receive optional AI-powered outfit recommendations.
4. **Settings:** Configure language, API credentials, and backup operations.

The first-run language selection screen appears before the main interface when no language preference has been saved.

## 🧪 Testing Checklist

- [ ] First-run language selection works correctly.
- [ ] English and Persian translations are complete.
- [ ] RTL and LTR layouts render correctly.
- [ ] Users can create, edit, and remove wardrobes and drawers.
- [ ] Storage lists scroll without disrupting the layout.
- [ ] Users can add, edit, and remove items.
- [ ] Item photos are stored and displayed correctly.
- [ ] Search results show the correct storage location.
- [ ] Core storage and search features work offline.
- [ ] AI recommendations are disabled when no API key is configured.
- [ ] The AI returns three suggestions based on available wardrobe items.
- [ ] Network errors and invalid API keys are handled gracefully.
- [ ] ZIP backups include the database and required images.
- [ ] Backup restoration validates files and preserves data integrity.
- [ ] Navigation and transitions work in both languages.
- [ ] The application builds and runs on supported Android versions.

## 🗺️ Roadmap

- [ ] Complete the bilingual onboarding experience.
- [ ] Finalize wardrobe and drawer management.
- [ ] Complete image-based item management.
- [ ] Optimize real-time search.
- [ ] Integrate OpenRouter outfit recommendations.
- [ ] Complete ZIP backup and restoration.
- [ ] Improve accessibility and responsive layouts.
- [ ] Add database migrations and backup compatibility checks.
- [ ] Test across different Android screen sizes and versions.
- [ ] Prepare a release APK or Android App Bundle.

## 🤝 Contributing

Contributions, bug reports, feature requests, and suggestions are welcome.

When contributing:

1. Keep the existing project structure organized.
2. Follow Kotlin and Android development conventions.
3. Preserve compatibility with English and Persian.
4. Protect local user data.
5. Test database and backup changes carefully.
6. Avoid committing API keys, personal data, or generated backup files.

## 🔒 Privacy

WardrobeApp is designed to store wardrobe records and item photos locally.

AI recommendations are optional. When enabled, the application sends the necessary item information and the user's event description to the configured online AI service.

Users should be informed about this data transfer, and the application should send only the information needed to generate recommendations.

## 📄 License

No license has been specified yet. Add an appropriate `LICENSE` file before publicly distributing the project as open source.

## 📌 Project Status

WardrobeApp is designed to be a personal, offline-first wardrobe organizer with optional AI-powered recommendations.

The features described in this README represent the intended functionality of the application. Actual availability depends on the implementation and testing of each feature in the source code.

---

**Organize your space. Find what you need. Dress with confidence.**

