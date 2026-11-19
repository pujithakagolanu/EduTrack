# EduTrack – Study Progress Tracker 📱🎓

**EduTrack** is a modern student productivity Android application built using **Jetpack Compose**, **Material 3**, **Room Database**, and **Clean Architecture (MVVM)**.

---

## ✨ Features

- **Modern Dashboard**:
  - Daily greeting & live formatted date.
  - Motivational student quotes.
  - Active tasks & today's study time counter.
  - Overall weekly progress indicator.
  - Enrolled subjects summary with study progress bars.
- **Stopwatch Time Tracker**:
  - Live study stopwatch (`HH:MM:SS` & `MM:SS`).
  - Play, Pause, and Lap tracking.
  - Auto-saves completed sessions to the local **Room Database** under the selected subject.
- **Subject Manager (Tasks)**:
  - Add, edit, and delete study subjects.
  - Custom color tags and weekly goal targets.
  - Enforces database referential integrity (`CASCADE` delete).
- **Session History & Archive**:
  - Scrollable history of all study sessions with notes and timestamps.
  - Filter by subject and date range.
- **Session Detail & Edit**:
  - Inspect, edit, or delete logged study records with confirmation dialogs.
- **Progress & Insights Analytics**:
  - Task completion rate and total study hours across all subjects.
  - Weekly and monthly study activity bar charts.
  - Subject breakdown with visual progress tracking.
- **Navigation & Modern UI**:
  - Material 3 navigation drawer (hamburger menu) + bottom navigation bar.
  - Smooth page transitions and modal slide animations.
  - Full **Light & Dark Mode** support with dynamic color adaptation.

---

## 🛠️ Architecture & Tech Stack

- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose (BOM 2024.10.00)
- **Design System**: Material 3
- **Local Persistence**: Room 2.6.1 (SQLite) with reactive Kotlin `Flow` & Coroutines
- **Architecture**: MVVM (Model - View - ViewModel) + Repository Pattern
- **Navigation**: Navigation Compose with animated transitions
- **Build Tool**: Gradle 8.9 + Android Gradle Plugin 8.6.1 + KSP 2.0.21

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/Pujitha-Kagolanu/EduTrack.git
   ```
2. Open the project in **Android Studio** (Koala / Ladybug or newer).
3. Ensure JDK 17 is configured under **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**.
4. Sync Gradle and run on an Android emulator or physical device running Android 8.0 (API 26) or higher.
