# Groww Mutual Funds Clone 🚀

[![Download APK](https://img.shields.io/badge/Download-APK-00D09C?style=for-the-badge&logo=android)](https://drive.google.com/file/d/1Etn0t_Qohpu_rNkCAgxQ2SSlUY3-Ck65/view?usp=drivesdk)
[![Watch Demo](https://img.shields.io/badge/Watch-Demo-red?style=for-the-badge&logo=youtube)](https://drive.google.com/file/d/114xh3NkCF4AOl6ziML6U_oQeEGjpz3Zb/view?usp=drivesdk)

A high-performance Mutual Funds tracking application built with **Clean Architecture**, **Jetpack Compose**, and **Offline-First** principles. This project is a technical demonstration of modern Android development, focusing on extreme performance, real-time data reactivity, and a premium visual experience.

---

## 🏗️ Architecture Design

The app follows a strict **Clean Architecture** pattern, ensuring separation of concerns, high testability, and a robust data synchronization strategy.

### Layer Responsibilities:
*   **Data Layer**: Manages data orchestration between Retrofit (API) and Room (Local DB). Implements a three-layer caching strategy: Memory -> Room -> Network.
*   **Domain Layer**: Contains the core business logic (`UseCases`) and Pure Kotlin entities. It is 100% independent of the UI and Android framework.
*   **Presentation Layer**: Implements MVVM. ViewModels transform domain flows into UI states (`StateFlow`), which are rendered using Jetpack Compose.

---

## 🎯 Assignment Checklist
| Component | Status | Feature |
| :--- | :---: | :--- |
| **Explore Screen** | ✅ | Category Grids (Index, Bluechip, etc.) + View All |
| **Portfolio** | ✅ | Multi-folder Watchlists + High-quality Empty States |
| **Fund Details** | ✅ | NAV History Chart + Dynamic Bookmark Icon |
| **Bottom Sheet** | ✅ | Multi-folder Selection + New Folder Creation |
| **Search** | ✅ | Debounced (300ms) Global Fund Discovery |
| **Architecture** | ✅ | MVVM + Clean Architecture + Hilt DI |
| **Persistence** | ✅ | Room  + Local Caching |
| **Theme** | ✅ | Dynamic Light/Dark Mode Support |
| **Testing** | ✅ | Unit Tests (MockK, Truth, Turbine) |
| **Visuals** | ✅ | Shimmer Loading + Subtle Transitions |

---


## ✨ Performance Optimizations

One of the project's core objectives was to eliminate the common "lag" found in financial apps through advanced coroutine orchestration.

*   **Parallel Job Orchestration**: The `syncExploreFunds` logic uses `async/awaitAll` to fetch 4+ fund categories simultaneously without blocking the main thread.
*   ** NAV Fetching**: NAV is fetched only for items currently VISIBLE on the screen, rather than for all search results..
*   **Reactive Search**: Search results are observed directly from the Room database. This means as soon as background price updates arrive from the network, the search list updates instantly without a manual refresh.

---

## 🛠️ Tech Stack & Tooling

### Core Android
| Library | Purpose | Version |
| :--- | :--- | :--- |
| **Kotlin** | Language & Logic | `2.0.20` |
| **Jetpack Compose** | Declarative Tooling | `2024.10.00` |
| **Android Gradle Plugin** | Build System | `8.7.2` |

### Architecture & DI
*   **Hilt (`2.51.1`)**: Dependency Injection for inversion of control across all layers.
*   **Navigation Compose (`2.8.2`)**: Typed navigation with nested graph support.
*   **Hilt Navigation Compose**: Scoping ViewModels to navigation destinations.

### Data & Persistence
*   **Room (`2.6.1`)**: Local persistence for offline-first capabilities.
*   **Retrofit (`2.11.0`)**: Type-safe HTTP client for Mutual Fund APIs.
*   **OkHttp Logging Interceptor**: Monitoring network traffic during development.

### Testing Suite (The "Quality Shield")
*   **MockK (`1.13.12`)**: Powerful mocking library for standard and coroutine-based functions.
*   **Google Truth (`1.4.4`)**: Readable and fluent assertion library.
*   **Turbine (`1.1.0`)**: Streamlined testing for Kotlin `Flow` emissions.
*   **JUnit 4**: The foundational test runner.

---

## 🎨 Design & UX
*   **Dynamic Dark Mode**: A premium dark theme tailored for financial clarity (`#0D0D0D` background).
*   **Interactive Charts**: Custom-built curve charts for NAV history, featuring smooth interaction tooltips and grid systems.
*   **Micro-animations**: Leverages `AnimatedVisibility` and `Crossfade` for sleek state transitions.

---

## 🚀 Getting Started

### Prerequisites
1.  **Android Studio Koala (2024.1.1)** or newer.
2.  **JDK 17** configured in your project structure.
3.  **Kotlin 2.0+** support (K2 Compiler enabled by default).

### Installation Steps
1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/poojasheoran12/Groww.git
    ```
2.  **Open in Android Studio**: Wait for the Gradle sync to finish.
3.  **Sync Data**: Upon first launch, the app will automatically trigger a parallel sync for categorized funds.
4.  **Run Tests**:
    ```bash
    ./gradlew :app:testDebugUnitTest
    ```

---

## 📂 Project Structure
```text
app/src/main/java/com/example/groww/
├── data/
│   ├── local/          # Room Database, Entities & DAOs
│   ├── remote/         # Retrofit API definitions & DTOs
│   ├── repository/     # Repository implementations (The SSOT Logic)
│   └── mapper/         # Data transformation (Entity <-> Domain)
├── domain/
│   ├── model/          # Clean Domain Entities (POJOs)
│   ├── repository/     # Repository Interfaces
│   └── usecase/        # Specific Business Logic (Add, Get, Sync)
├── presentation/       # Feature-wise UI & ViewModels
│   ├── explore/        # Category discovery & parallel loading
│   ├── search/         # Reactive universal search
│   ├── details/        # Fund details & Custom Charting
│   ├── watchlist/      # Portfolio/Watchlist management
│   ├── viewAll/        # Categorized lists & client pagination
│   └── navigation/     # GrowwNavGraph & Screen definitions
├── di/                 # Hilt Dependency Injection Modules
├── ui/theme/           # Theme, Color, and Typography setups
└── util/               # Shared utilities (Modifier extensions, etc.)
```
