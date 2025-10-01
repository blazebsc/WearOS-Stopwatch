# WearOS Stopwatch

A modern, feature-rich stopwatch application built specifically for Wear OS devices using Jetpack Compose.

## Features

- ⏱️ **Precise Timing**: High-accuracy stopwatch with centisecond precision (0.01s)
- 🏁 **Lap Times**: Record multiple lap times with detailed tracking
- 🎨 **Modern UI**: Beautiful Material Design interface optimized for round watches
- 🔄 **Smooth Animations**: Fluid animations with circular progress indicator
- 📊 **Lap History**: View all recorded laps with split times and totals
- 👆 **Intuitive Gestures**: Swipe up to view laps, swipe down to return
- 🎯 **Standalone App**: Runs independently without requiring a phone companion

## Screenshots

<!-- Add screenshots here when available -->

## Technical Details

### Built With

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit for Wear OS
- **Wear Compose Material** - Material Design components for Wear OS
- **ViewModel** - State management and lifecycle awareness
- **Coroutines** - Asynchronous programming for smooth timer updates

### Requirements

- **Minimum SDK**: API 30 (Android 11)
- **Target SDK**: API 36
- **Compile SDK**: API 36

## Installation

### Prerequisites

- Android Studio (Latest version recommended)
- Wear OS emulator or physical Wear OS device
- Android SDK with Wear OS support

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/blazebsc/WearOS-Stopwatch.git
   cd WearOS-Stopwatch
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Connect your Wear OS device or start an emulator

5. Run the app:
   - Click the "Run" button in Android Studio, or
   - Use the command line: `./gradlew installDebug`

## Usage

### Basic Operations

- **Start/Stop**: Tap the play/pause button to start or stop the timer
- **Lap**: Tap the flag button to record a lap time (only available when running)
- **Reset**: Tap the replay button to reset the stopwatch to 00.00
- **View Laps**: Swipe up on the main screen to see all recorded lap times
- **Return**: Swipe down from the lap view to return to the main stopwatch

### Display Format

- Main timer shows: `SS.CC` (seconds.centiseconds)
- Current lap time is displayed below the main timer when laps are recorded
- Lap list shows both split time and total time for each lap

## Project Structure

```
app/src/main/java/com/blake7/watchstopwatch/presentation/
├── MainActivity.kt          # App entry point
├── WearApp.kt              # Main Compose UI components
├── StopwatchViewModel.kt   # Business logic and state management
├── StopwatchState.kt       # State holder for stopwatch data
└── theme/
    └── Theme.kt            # App theming
```

## Architecture

The app follows modern Android architecture best practices:

- **MVVM Pattern**: Separation of UI and business logic
- **State Management**: Reactive UI with Compose state
- **Lifecycle Awareness**: ViewModel integration for configuration changes
- **Coroutines**: Non-blocking timer updates at ~60 FPS

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the [MIT License](LICENSE).

## Author

Blake - [blazebsc](https://github.com/blazebsc)

## Acknowledgments

- Built with Jetpack Compose for Wear OS
- Material Design icons from Material Icons Extended
- Android Wear OS documentation and samples
