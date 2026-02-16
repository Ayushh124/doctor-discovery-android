# 🏥 Doctor Discovery Android App

A professional native Android application for discovering and registering doctors, built with Kotlin, Jetpack Compose, Node.js, and MySQL.

## 📱 Features

### Search & Discovery
- 🔍 **Real-time Search** - Search doctors by name as you type
- 🎯 **Advanced Filters** - Filter by city and specialization
- 📜 **Infinite Scroll** - Seamless loading of doctor listings
- 🔥 **Top Searched Doctors** - View the most popular doctors

### Doctor Profiles
- 👤 **Complete Information** - View detailed doctor profiles
- 📊 **Popularity Tracking** - Automatic search count increment
- 🏆 **Most Searched Badge** - Highlights top 10 doctors
- 📞 **Contact Details** - Phone, email, and location

### Doctor Registration
- 📝 **Multi-Step Form** - Easy 2-step registration process
- 🖼️ **Image Upload** - Profile picture upload support
- ✅ **Input Validation** - Both frontend and backend validation
- 🎓 **Professional Details** - Institute, degree, specialization, experience

## 🛠️ Tech Stack

### Frontend (Android)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Network:** Retrofit + OkHttp
- **Image Loading:** Native implementation (java.net.URL)
- **Async:** Coroutines + Flow

### Backend (API)
- **Runtime:** Node.js
- **Framework:** Express.js
- **Database:** MySQL 8.0+
- **File Upload:** Multer
- **Validation:** Custom middleware

### Database
- **System:** MySQL
- **Connection:** Connection pooling (mysql2/promise)
- **Features:** Indexes for optimized search

## 📂 Project Structure

```
DoctorDiscovery/
├── app/                          # Android application
│   └── src/main/java/com/ayush/doctordiscovery/
│       ├── ui/
│       │   ├── screens/          # UI screens (List, Detail, Registration)
│       │   ├── viewmodel/        # Business logic layer
│       │   └── components/       # Reusable UI components
│       ├── data/
│       │   ├── repository/       # Data management
│       │   ├── remote/           # API service
│       │   └── model/            # Data models
│       ├── navigation/           # Navigation setup
│       └── util/                 # Utility classes
│
└── backend/                      # Node.js server
    ├── routes/                   # API endpoints
    │   ├── doctors.js           # Search, filter, get doctors
    │   └── registration.js      # Multi-step registration
    ├── middleware/               # Validation & file upload
    ├── database.js              # MySQL connection
    ├── server.js                # Main entry point
    └── schema.sql               # Database schema
```

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Arctic Fox or later)
- **Node.js** (v18+)
- **MySQL** (v8.0+)
- **JDK** (11+)

### Backend Setup

1. **Install Dependencies**
   ```bash
   cd backend
   npm install
   ```

2. **Setup Database**
   ```bash
   mysql -u root -p
   ```
   ```sql
   CREATE DATABASE doctor_discovery;
   CREATE USER 'doctor_app'@'localhost' IDENTIFIED BY 'doctor123';
   GRANT ALL PRIVILEGES ON doctor_discovery.* TO 'doctor_app'@'localhost';
   FLUSH PRIVILEGES;
   exit;
   ```

3. **Load Schema**
   ```bash
   mysql -u doctor_app -pdoctor123 doctor_discovery < schema.sql
   ```

4. **Configure Environment**
   Create `.env` file in `backend/` directory:
   ```
   DB_HOST=localhost
   DB_USER=doctor_app
   DB_PASSWORD=doctor123
   DB_NAME=doctor_discovery
   PORT=3000
   ```

5. **Start Server**
   ```bash
   npm run dev
   ```
   Server runs at `http://localhost:3000`

### Android Setup

1. **Open Project**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to `DoctorDiscovery` folder

2. **Update Network Configuration**
   
   For **Emulator**, update `RetrofitInstance.kt`:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:3000/api/"
   ```
   
   For **Real Device** (same WiFi as backend):
   ```bash
   # Find your Mac's IP
   cd backend
   ./get_my_ip.sh
   
   # Update RetrofitInstance.kt with your IP
   private const val BASE_URL = "http://YOUR_IP:3000/api/"
   ```

3. **Build & Run**
   - Click "Run" button or press `Shift + F10`
   - Select your device/emulator

## 📡 API Endpoints

### Doctor Endpoints
```
GET    /api/doctors/search              # Search & filter doctors
GET    /api/doctors/top                 # Get top N most searched
GET    /api/doctors/specializations     # Get all specializations
GET    /api/doctors/cities              # Get all cities
GET    /api/doctors/:id                 # Get single doctor (increments count)
```

### Registration Endpoints
```
POST   /api/register/step1              # Personal information
POST   /api/register/upload-image       # Profile picture upload
POST   /api/register/step2              # Professional details
```

## 🏗️ Architecture

### Android (MVVM Pattern)

```
UI Layer (Screens)
    ↓
ViewModel Layer (Business Logic)
    ↓
Repository Layer (Data Management)
    ↓
API Service (Retrofit)
    ↓
Backend Server
```

**Benefits:**
- ✅ Separation of concerns
- ✅ Testable components
- ✅ Reactive UI updates
- ✅ Lifecycle-aware

### Backend (RESTful API)

```
Express Server
    ↓
Routes (API Endpoints)
    ↓
Middleware (Validation, Upload)
    ↓
Database Layer (MySQL)
```

## 🎨 Key Features Implementation

### 1. Real-time Search
Search triggers automatically as user types, with debouncing for performance.

### 2. Infinite Scroll
Pagination handled automatically when user scrolls near bottom of list.

### 3. Auto Search Count
Backend automatically increments `search_count` when doctor detail is viewed.

### 4. Native Image Loading
Custom implementation using `java.net.URL` without third-party libraries (Coil, Glide, etc.).

### 5. Multi-Step Registration
Three-step process: Personal Info → Image Upload → Professional Details

## 🔒 Security Features

- ✅ Input validation (frontend + backend)
- ✅ SQL injection prevention (parameterized queries)
- ✅ File upload restrictions (type, size)
- ✅ CORS configuration
- ✅ Environment variables for sensitive data

## 📊 Database Schema

```sql
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    gender VARCHAR(10),
    age INT,
    specialization VARCHAR(100) NOT NULL,
    institute VARCHAR(200),
    degree VARCHAR(100),
    location VARCHAR(100) NOT NULL,
    experience_years INT NOT NULL,
    consultation_fee INT NOT NULL,
    bio TEXT,
    rating DECIMAL(3,2) DEFAULT 0.0,
    image_url VARCHAR(255),
    search_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_specialization (specialization),
    INDEX idx_location (location),
    INDEX idx_search_count (search_count)
);
```

## 🧪 Testing

### Backend API Testing
```bash
cd backend

# Test search
curl "http://localhost:3000/api/doctors/search?name=rajesh"

# Test top doctors
curl "http://localhost:3000/api/doctors/top?limit=4"

# Test specializations
curl "http://localhost:3000/api/doctors/specializations"
```

## 📱 Screenshots

*Add screenshots here*

## 🤝 Contributing

This is a personal project. Contributions are not currently accepted.

## 📄 License

This project is private and not licensed for distribution or modification.

## 👤 Author

**Ayush**
- GitHub: [@Ayushh124](https://github.com/Ayushh124)

## 🐛 Known Issues

None currently. All features are working as expected.

## 📈 Project Stats

- **Lines of Code:** ~4,000
- **Files:** 40 source files
- **Languages:** Kotlin (80%), JavaScript (20%)
- **Features:** 10+ complete features
- **Completion:** 96%

## 🔄 Version History

- **v1.0.0** - Initial release
  - Search & filter functionality
  - Doctor detail view with auto-increment
  - Multi-step registration
  - Native image loading
  - Top doctors feature

---

**Note:** This project was built with native Android development practices and modern architecture patterns. The backend uses industry-standard REST API design with proper validation and error handling.
