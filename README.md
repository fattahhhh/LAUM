# ReSort Campus — Aplikasi Android (Kotlin + Jetpack Compose)

Versi aplikasi mobile Android native dari **ReSort Campus** untuk kampus Politeknik Negeri Jakarta (PNJ). Seluruh fitur sebelumnya di versi web (`c:\laum\resort-campus`) telah diubah menjadi aplikasi Android mandiri yang berjalan langsung di handphone tanpa memerlukan server `localhost`.

## Yang Disempurnakan dari Versi Web

1. **Native Android (Kotlin + Jetpack Compose + Material 3)** — bisa langsung dibuka di Android Studio, di-build menjadi APK, dan di-install di HP Android (min. Android 8.0 / API 26).
2. **Database lokal di HP (Room SQLite)** — semua data yang di versi web masih di-hardcode (poin Beranda `1240`, streak `5`, statistik Profil `12 sampah / 0.24 kg`, daftar tong, dan 5 riwayat palsu) kini dihitung nyata dari database dan langsung bertambah saat kamu selesai scan.
3. **Kamera native (CameraX) + Photo Picker** — preview kamera langsung, tombol flash/torch, ganti kamera depan/belakang, dan ambil dari galeri.
4. **Gemini Vision API asli + Fallback Demo Offline** — di versi web `classifyWaste()` hanya `throw Error` ke mock. Di versi Android ini, panggilan multimodal ke Gemini (`responseMimeType = application/json` + retry 1x) sudah diimplementasikan penuh, dan otomatis jatuh ke mode demo (mock AI) bila `GEMINI_API_KEY` kosong atau koneksi terputus.
5. **Alur verifikasi 2 tahap utuh** — foto sampah -> hasil AI + `ScanSession` (aktif 15 menit) -> peta tong terdekat -> foto tong sampah -> poin masuk ke jurusan -> muncul di Leaderboard & Profil.
6. **Anti-curang & Green IT** — kompresi gambar di HP (maks 1024px, JPEG 70%, koreksi rotasi EXIF), deteksi foto duplikat dengan hash SHA-256, batas scan harian, dan file foto sementara langsung dihapus setelah dianalisis.

## Stack Teknologi

| Bagian | Teknologi |
|---|---|
| Bahasa | Kotlin 2.0 (JVM 17) |
| UI | Jetpack Compose + Material 3 + Navigation Compose |
| Kamera | AndroidX CameraX (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`) |
| Database | AndroidX Room (SQLite) + Kotlin Flow |
| Session | AndroidX DataStore Preferences + PBKDF2-HMAC-SHA256 |
| AI Vision | Google Gemini REST API (`okhttp`) + fallback `MockAi` |

## Cara Menjalankan di Android Studio & Handphone

1. Buka **Android Studio** (disarankan Ladybug / 2024.2 ke atas).
2. Pilih **File -> Open**, lalu arahkan ke folder:
   ```
   C:\laum\resort-campus-android
   ```
3. Tunggu **Gradle Sync** selesai (Android Studio akan otomatis membuat `local.properties` berisi `sdk.dir` dan mengunduh Gradle wrapper).
4. *(Opsional — untuk AI Gemini nyata)* Buka file `local.properties` di root proyek dan tambahkan:
   ```properties
   GEMINI_API_KEY=isi_api_key_gemini_kamu
   GEMINI_MODEL=gemini-2.0-flash
   ```
   Jika tidak diisi, aplikasi tetap berjalan penuh memakai **mode demo (mock AI)**.
5. Hubungkan HP Android ke laptop dengan kabel USB (aktifkan **Opsi Pengembang -> Debugging USB** di HP) atau nyalakan **Device Manager (Emulator)**.
6. Klik tombol **Run 'app'** (ikon segitiga hijau / `Shift+F10`). Aplikasi akan ter-install dan langsung terbuka di HP.

### Membuat File APK untuk Dikirim ke HP

Di menu Android Studio: **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**.
File APK siap pasang akan tersedia di:
```
C:\laum\resort-campus-android\app\build\outputs\apk\debug\app-debug.apk
```

## Akun Demo (Otomatis Tersedia Saat Pertama Dibuka)

Di halaman **Masuk**, ketuk tombol **"Pakai Demo"** untuk login satu klik, atau isi manual:
- **Email:** `demo@pnj.ac.id`
- **Password:** `rahasia123`
- **Jurusan:** Teknik Informatika dan Komputer

## Struktur Proyek

```
resort-campus-android/
└── app/src/main/
    ├── AndroidManifest.xml
    ├── java/id/ac/pnj/resortcampus/
    │   ├── MainActivity.kt               # Entry point + SplashScreen
    │   ├── ReSortApp.kt                  # Application + dependency container
    │   ├── data/
    │   │   ├── local/                    # Room Entity, DAO, AppDatabase
    │   │   ├── ai/                       # WasteClassifier (Gemini REST) + MockAi
    │   │   ├── PasswordHasher.kt         # Hash password PBKDF2-HMAC-SHA256
    │   │   ├── Points.kt                 # Aturan poin, CO2e, target, & badge
    │   │   ├── Seeder.kt                 # Seed jurusan PNJ, tong, akun & skor awal
    │   │   ├── SessionStore.kt           # Session login (DataStore)
    │   │   └── Repository.kt             # Logika bisnis & anti-curang
    │   ├── ui/
    │   │   ├── AppNav.kt                 # Rute navigasi
    │   │   ├── ViewModels.kt             # State holder tiap layar
    │   │   ├── theme/Theme.kt            # Warna & tipografi sesuai Figma
    │   │   ├── components/               # Tombol, kartu, bottom bar, helper
    │   │   └── screens/                  # Onboarding, Login, Register, Home, Scan,
    │   │                                 # Result, Bins (Peta), Reward, Leaderboard, Profile
    │   └── util/ImageUtils.kt            # Kompresi JPEG + koreksi EXIF + SHA-256
    └── res/                              # Ikon launcher vektor, tema, warna, string
```
