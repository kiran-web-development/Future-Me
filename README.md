# Future-Me-APP 🚀

An AI-powered Android application that helps you plan and prepare for your future self using Google's Gemini API.

## About This Project

This is an Android Studio project designed to run on Android devices and emulators. The app leverages AI capabilities to provide personalized insights and future planning features.

**View your app in AI Studio:** https://ai.studio/apps/11295785-8666-4db3-9508-d617c4b5ab27

---

## Features

- 🤖 AI-powered insights using Google Gemini API
- 📱 Native Android experience
- ⚡ Real-time responses and interactions
- 🎯 Future planning and goal-setting capabilities

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (latest version recommended) - [Download](https://developer.android.com/studio)
- **Java Development Kit (JDK)** - Version 11 or higher
- **Git** - For cloning the repository
- **Gemini API Key** - Get one for free at [Google AI Studio](https://makersuite.google.com/app/apikey)

---

## Installation & Setup

### 1. Open the Project

1. Launch Android Studio
2. Click **File** → **Open** and navigate to this project directory
3. Allow Android Studio to sync and fix any build incompatibilities (this may take a few minutes)

### 2. Configure Your Gemini API Key

1. In the project root directory, create a `.env` file
2. Add your Gemini API key to the file:
   ```
   GEMINI_API_KEY=your_actual_api_key_here
   ```
3. (Optional) Refer to `.env.example` for reference

⚠️ **Security Tip:** Never commit your `.env` file to version control. Ensure it's listed in `.gitignore`.

### 3. Update Build Configuration

1. Open the app's `build.gradle.kts` file
2. Locate and remove this line:
   ```gradle
   signingConfig = signingConfigs.getByName("debugConfig")
   ```
3. Save the file

### 4. Run the App

- **On Emulator:** Click **Run** (or press `Shift + F10`) to deploy to your configured Android emulator
- **On Physical Device:** Connect your Android device and select it from the device list before clicking **Run**

---

## Troubleshooting

### Build Fails with Signing Config Error
- Ensure you completed Step 3 and removed the signing config line from `build.gradle.kts`
- Clean and rebuild: **Build** → **Clean Project** → **Build** → **Rebuild Project**

### API Key Not Recognized
- Verify the `.env` file is in the project root directory (same level as `build.gradle.kts`)
- Ensure there are no extra spaces or typos in your API key
- Try restarting Android Studio after adding the `.env` file

### Emulator Issues
- Update Android SDK tools: **Tools** → **SDK Manager** → Update available packages
- Try recreating the emulator: **Tools** → **AVD Manager** → Delete and recreate

### App Crashes on Startup
- Check the Logcat output: **View** → **Tool Windows** → **Logcat**
- Ensure your Gemini API key is valid and has the correct permissions

---

## Development

### Project Structure

```
Future-Me-APP/
├── app/                 # Main application module
├── build.gradle.kts     # App-level build configuration
├── settings.gradle.kts  # Project-level settings
├── .env.example         # Example environment variables
└── README.md            # This file
```

### Building From Source

```bash
# Clone the repository
git clone https://github.com/kiran-web-development/Future-Me-APP.git
cd Future-Me-APP

# Open in Android Studio (or use Gradle commands)
./gradlew build
```

---

## Resources & Documentation

- 📚 [Google Gemini API Documentation](https://ai.google.dev/docs)
- 📱 [Android Studio Documentation](https://developer.android.com/studio/intro)
- 🔑 [Gemini API Getting Started](https://makersuite.google.com/app/apikey)
- 🏗️ [Android Development Guide](https://developer.android.com/guide)

---

## Screenshots

### Home Screen - Welcome Interface
Welcoming interface that greets users with the app's main purpose. This is the first screen users see when launching Future-Me-APP, setting the tone for an intuitive and engaging user experience.

<img width="1080" height="2400" alt="Home Screen Welcome Interface" src="https://github.com/user-attachments/assets/ae3c21d2-c6e7-46e2-a593-80c31d417c5d" />

### Main Chat Interface - Input Screen
The primary interaction screen where users can input their questions and prompts to the AI. Features a clean chat interface with text input field and send button for seamless communication with the Gemini AI.

<img width="1080" height="2400" alt="Main Chat Interface - Input Screen" src="https://github.com/user-attachments/assets/83405c3e-1a1a-41a7-a8dc-79c43b4eecf6" />

### AI Response Display - Future Insights
Shows the AI-generated response and personalized insights about your future planning. The app displays detailed recommendations and thoughtful guidance powered by Google's Gemini API to help you prepare for your future.

<img width="1080" height="2400" alt="AI Response Display - Future Insights" src="https://github.com/user-attachments/assets/c3887fa9-74e8-4816-9a6a-afa810372428" />

### Planning Suggestions - Goals & Milestones
Displays suggested goals and milestones for your future self. The AI provides actionable recommendations and structured planning advice to help you achieve your long-term objectives.

<img width="1080" height="2400" alt="Planning Suggestions - Goals & Milestones" src="https://github.com/user-attachments/assets/2db9281d-91ae-41bb-91d3-0a5bac71f202" />

### Advanced Features - Detailed Analysis
Shows detailed analysis and comprehensive breakdown of planning recommendations. Provides in-depth insights with step-by-step guidance for implementing your future plans effectively.

<img width="1080" height="2400" alt="Advanced Features - Detailed Analysis" src="https://github.com/user-attachments/assets/c61d8f3b-3728-4b7c-b27b-dd12f1adbe37" />

---

## Support

If you encounter any issues or have questions:

1. Check the **Troubleshooting** section above
2. Review the [Android Studio Documentation](https://developer.android.com/studio/intro)
3. Visit the [Gemini API Documentation](https://ai.google.dev/docs)

---

## License

This project is provided as-is. Modify and use as needed for your purposes.

---

*Last updated: May 31, 2026*
