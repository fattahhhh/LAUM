# 🌿 EcoSort Campus — Riset & Strategi Hackathon LOGICODIX 2026
> **Disiapkan:** 23 Oktober 2026 | **Kompetisi:** LOGICODIX 2026 – Subtema Green Environment & Sustainability

---

## 📋 RINGKASAN EKSEKUTIF

**EcoSort Campus** adalah web app pilah sampah berbasis AI dan gamifikasi untuk lingkungan kampus, dengan kompetisi antar jurusan sebagai mesin retensinya. Ide ini **layak dan kompetitif** untuk LOGICODIX 2026, dengan catatan strategis berikut:

| Dimensi | Penilaian | Catatan |
|---|---|---|
| Problem-Solution Fit | ✅ Kuat | Data nasional sangat mendukung; masalah nyata dan terukur |
| Inovasi & AI | ✅ Kuat | Gemini Flash Vision = diferensiasi jelas vs app biasa |
| Software Engineering | ⚠️ Risiko | 7 hari sangat mepet; scope harus dipangkas keras |
| UI/UX | ✅ Kuat | Leaderboard visual + dashboard admin = dua permukaan terpisah |
| Green IT & Ethics | ✅ Unik | Strategi kompresi foto + no-storage + confidence disclaimer sudah tepat |
| Retensi Jangka Panjang | ⚠️ Risiko | Harus ada "anchor" institusional (admin kampus terlibat) |

**Rekomendasi utama:** Jadikan kompetisi antar jurusan yang dikelola oleh pihak kampus (bukan hanya viral sosial) sebagai kunci retensi. Tanpa dukungan institusional, app ini rentan mati setelah lomba.

---

## 1. 📊 DATA & STATISTIK INDONESIA UNTUK PITCH

### 1.1 Volume Sampah Nasional

| Indikator | Data | Tahun | Sumber |
|---|---|---|---|
| Total timbulan sampah nasional (data SIPSN) | ~56,63 juta ton/tahun | 2023 | KLHK/SIPSN |
| Total timbulan (data terlapor aktual) | ~38,6 juta ton | 2023 | SIPSN/kemenlh.go.id |
| Sampah terkelola secara layak | 32–39% | 2023–2024 | KLHK |
| Sisanya belum terkelola / open dumping | **61–68%** | 2023–2024 | KLHK/BRIN |
| Komposisi organik/sisa makanan | 40–41,4% | 2023 | BRIN |
| Komposisi plastik | ~19,21% | 2023 | SIPSN |
| Tingkat daur ulang plastik nasional | **~22%** | 2023 | KLHK |

> [!IMPORTANT]
> **"Lebih dari 60% sampah Indonesia belum terkelola dengan baik"** — ini angka pitch yang kuat, bersumber dari KLHK/SIPSN 2023.

### 1.2 Masalah Sampah di Kampus Indonesia

| Indikator | Data | Sumber |
|---|---|---|
| Komposisi organik di kampus | 55–67% dari total sampah harian | Studi UI, Undip, Trisakti, Undiknas |
| Sistem pengelolaan | Mayoritas belum terpilah sejak sumber | ResearchGate/jurnal perguruan tinggi |
| Masalah utama | Sampah terpilah dicampur lagi saat diangkut | Studi berbagai kampus Indonesia |
| Acuan green campus | UI GreenMetric (kategori Waste) | UIGreenMetric.net |

> [!NOTE]
> **[⚠️ ASUMSI]** Data spesifik "berapa % mahasiswa salah pilah" belum ditemukan dalam satu sumber resmi tunggal. Kamu perlu **survei sendiri** untuk angka ini (lihat Poin 5).

### 1.3 Dampak Salah Pilah Sampah (CO₂ & Lingkungan)

**Framing untuk pitch:**
- Sampah organik yang berakhir di TPA menghasilkan metana (CH₄), yang **28–36× lebih potensial sebagai gas rumah kaca** dibanding CO₂ (IPCC data).
- Daur ulang 1 ton material recyclable dapat menghemat **0,5–3+ ton CO₂e** dibanding produksi dari bahan baku (EU JRC / EPA).
- Daur ulang aluminium saja menghemat **95% energi** vs produksi primer.
- Tingkat kontaminasi tinggi → seluruh batch recyclable **dibuang ke TPA** (kerja keras tanpa hasil).

**Kalimat pitch yang kuat:**
> *"Ketika satu botol plastik masuk ke tempat sampah organik, bukan hanya satu botol yang terbuang — seluruh batch sampah di truk itu bisa berakhir di TPA, menghilangkan potensi pengurangan 0,5–3 ton CO₂e per ton material. Inilah biaya tersembunyi dari salah pilah."*

---

## 2. 🔍 STUDI KASUS APP/PROGRAM SERUPA

### 2.1 Peta Kompetitor & Referensi

| Nama | Negara | Fitur Utama | Differensiator vs EcoSort Campus |
|---|---|---|---|
| **WESTA (UGM)** | 🇮🇩 Indonesia | Smart bin IoT + AI klasifikasi + estimasi CO₂ + brand tracking | WESTA pakai hardware IoT; EcoSort pure software (lebih murah & replicable) |
| **SiLaras (UNEJ)** | 🇮🇩 Indonesia | AI waste recognition + marketplace bank sampah | SiLaras fokus monetisasi individu; EcoSort fokus kompetisi komunal jurusan |
| **Recycle Coach** | 🇺🇸 USA | Kalender jadwal angkut sampah + "What Goes Where" search | Bukan AI klasifikasi foto; tanpa gamifikasi; retensi rendah karena episodik |
| **Litterati** | 🇺🇸 USA | Foto sampah liar + crowdmap + brand data | Fokus mapping publik, bukan kampus tertutup; tidak ada leaderboard jurusan |
| **Junker** | 🇮🇹 Italia | Barcode scanner + panduan buang lokal | Offline-first, lokal spesifik; tanpa AI vision |

### 2.2 Kenapa Banyak App Gagal Retensi

Berdasarkan analisis dari literatur dan kasus Recycle Coach, SiLaras, Litterati:

| Penyebab Kegagalan | Penjelasan | Solusi di EcoSort Campus |
|---|---|---|
| **Episodic Use** | Dibuka hanya saat bingung, lalu lupa | Leaderboard jurusan → update mingguan bikin orang balik |
| **Kurang Reward** | Tidak ada insentif nyata pasca novelty | Poin → reward kantin/UKM (roadmap); badge koleksi segera |
| **Akurasi AI Buruk** | Salah klasifikasi → frustasi → uninstall | Tampilkan confidence score + opsi koreksi user |
| **Tanpa Infrastruktur Nyata** | App bagus, tapi tong sampahnya dicampur lagi | Libatkan admin kampus; laporan area = aksi nyata |
| **Registrasi Rumit** | Hambatan onboarding | OAuth/SSO email kampus — 1 klik masuk |

### 2.3 Pembeda EcoSort Campus (USP)

> [!TIP]
> Kunci diferensiasi yang belum dimiliki kompetitor manapun: **Kompetisi berbasis unit institusional (jurusan) + dashboard admin kampus yang memberikan data actionable**. Ini bukan sekadar app untuk pengguna individu — ini sistem data untuk pengambil keputusan kampus.

1. **Unit kompetisi = Jurusan** (bukan individu) → memanfaatkan identitas sosial mahasiswa; peer pressure positif
2. **Dashboard admin** → kampus punya data real untuk laporan green campus / UI GreenMetric
3. **Strategi AI fallback** → cache hasil AI; bisa demo offline saat final
4. **Estimasi dampak realtime** → kg terpilah + CO₂e hemat = angka konkret untuk report kampus

---

## 3. 🧠 RISET PERILAKU & MOTIVASI GAMIFIKASI

### 3.1 Temuan Kunci dari Literatur

| Strategi | Efektivitas | Bukti/Sumber |
|---|---|---|
| **Kompetisi inter-grup** | ⭐⭐⭐⭐⭐ Sangat tinggi | Peer accountability naik drastis; norma sosial berubah cepat (idealogyjournal.com) |
| **Leaderboard publik** | ⭐⭐⭐⭐ Tinggi | Kontribusi individual lebih visible → rasa tanggung jawab naik (ResearchGate) |
| **Reward ekstrinsik (poin/badge)** | ⭐⭐⭐ Sedang | Menarik awal, tapi perlu dikombinasi dengan intrinsik (thebehaviouralist.com) |
| **Streak & tantangan** | ⭐⭐⭐ Sedang | Membantu rutinitas, tapi bisa burn-out jika terlalu rigid |
| **Social norms nudge** | ⭐⭐⭐⭐ Tinggi | "85% jurusan kamu sudah pilah sampah hari ini" → dorongan kuat (Kopernik.info) |
| **Fun fact / edukasi mikro** | ⭐⭐⭐ Sedang | Meningkatkan pemahaman, tapi bukan penggerak utama aksi |

### 3.2 Mekanisme Psikologi yang Harus Didesain

**Masalah "free-rider":** Dalam kompetisi grup, ada yang mengandalkan teman aktif. 
**Solusi desain:** Tampilkan kontribusi individual di dalam leaderboard jurusan (misal: "Kamu #3 di Teknik Elektro minggu ini") — sehingga ada akuntabilitas individual + kebanggaan kolektif.

**Masalah "novelty wear-off":** Semangat awal tinggi, lalu turun.
**Solusi desain:**
- Reset leaderboard per bulan/semester (musim baru → semangat baru)
- Badge permanen (tidak hilang setelah reset) → reward koleksi jangka panjang
- "Streak champion" badge untuk yang konsisten → identitas mahasiswa peduli lingkungan

**Masalah "cuek lingkungan":**
**Solusi desain:** Framing bukan "selamatkan bumi" (terlalu abstrak), tapi "**jurusanmu sedang kalah dari Teknik Sipil 47 poin**" (konkret & kompetitif).

### 3.3 Strategi Retensi Jangka Panjang (Jawaban Kekhawatiran #1)

```
Tingkatan Keterlibatan:
┌─────────────────────────────────────────────────────┐
│ Level 3: INSTITUSIONAL (paling tahan lama)          │
│  → Kampus jadikan EcoSort sebagai KPI green campus  │
│  → Hasil dashboard masuk laporan UI GreenMetric     │
│  → Koordinator jurusan/BEM mendorong partisipasi   │
├─────────────────────────────────────────────────────┤
│ Level 2: SOSIAL (mid-term)                          │
│  → Kompetisi antar jurusan → peer pressure          │
│  → Badge = identitas sosial di WhatsApp/IG         │
│  → "Jangan bikin jurusan kita kalah"               │
├─────────────────────────────────────────────────────┤
│ Level 1: INDIVIDUAL (jangka pendek)                 │
│  → Poin, streak, fun fact                           │
│  → Novelty AI classification                        │
└─────────────────────────────────────────────────────┘
```

> [!IMPORTANT]
> **Strategi retensi terkuat untuk konteks kampus Indonesia adalah Level 3 — dukungan institusional**. Pitch ke juri harus menyertakan rencana konkret: "Kami akan onboard koordinator jurusan/BEM sebagai admin pertama kami."

---

## 4. 🤖 REKOMENDASI AI & DATASET

### 4.1 Perbandingan Opsi AI untuk Klasifikasi Sampah dari Foto

| Aspek | Gemini Flash (API) | GPT-4o Vision (API) | MobileNetV2 (Open Source) | EfficientNet-B4 (Open Source) |
|---|---|---|---|---|
| **Akurasi umum** | Sangat tinggi (zero-shot) | Sangat tinggi (zero-shot) | 88–92% (TrashNet) | 93–98% (TrashNet) |
| **Fleksibilitas kategori** | ✅ Prompt-based, unlimited | ✅ Prompt-based | ❌ Fixed kelas training | ❌ Fixed kelas training |
| **Biaya** | **Gratis** (free tier: 15 RPM, 1.500 req/hari) | \$0,01–0,03/request | Gratis selamanya | Gratis selamanya |
| **Latensi** | 500ms–2s (network) | 500ms–2s (network) | <100ms (lokal) | <200ms (lokal) |
| **Privasi foto** | ⚠️ Data dikirim ke Google | ⚠️ Data ke OpenAI | ✅ Proses lokal | ✅ Proses lokal |
| **Kemampuan penjelasan** | ✅ Rich reasoning + disposal tips | ✅ Rich reasoning | ❌ Hanya label & confidence | ❌ Hanya label & confidence |
| **Offline/fallback** | ❌ (perlu internet) | ❌ (perlu internet) | ✅ Bisa bundled di server | ✅ Bisa bundled di server |
| **Cocok untuk hackathon 7 hari** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ (perlu fine-tune) | ⭐⭐⭐ (lebih berat) |
| **Green IT score** | Medium (cloud dependency) | Medium | ✅ Sangat efisien (lokal) | ✅ Efisien (lokal) |

### 4.2 Rekomendasi untuk EcoSort Campus

> [!TIP]
> **Rekomendasi: Gunakan Gemini Flash (Vision) sebagai primary + MobileNetV2 sebagai fallback lokal.**

**Arsitektur hybrid yang direkomendasikan:**
```
User foto sampah
     ↓
[Frontend: kompres foto ≤ 800px, strip EXIF]
     ↓
Cek koneksi internet?
   ├── YA → Kirim ke Gemini Flash API
   │         → Gemini returns: kategori + cara buang + fun fact + confidence
   └── TIDAK → Jalankan MobileNetV2 lokal di server
               → Return kategori + confidence
               → Tandai "Offline Mode - akurasi terbatas"
     ↓
Simpan log: kategori, confidence, timestamp, area (TANPA foto)
     ↓
Update poin jurusan
```

**Alasan memilih Gemini Flash:**
- Free tier 1.500 req/hari = cukup untuk demo + fase awal kampus
- Zero-shot = tidak perlu dataset training; langsung bisa klasifikasi kemasan lokal (Indomie, Aqua, dll.)
- Output bisa langsung berupa teks disposal tips dalam Bahasa Indonesia
- Mendukung poin Green IT: foto tidak disimpan permanen, hanya metadata log

**Dataset open-source yang relevan (untuk fine-tuning fallback):**
- **TrashNet** (Stanford, GitHub) — 2.527 gambar, 6 kelas (Cardboard, Glass, Metal, Paper, Plastic, Trash)
- **TACO Dataset** (GitHub: pedropro/TACO) — 1.500+ gambar, konteks outdoor/liar, lebih realistis
- **Waste Classification Data (Kaggle)** — ~25.000 gambar, 2 kelas (Organic/Recyclable)
- **Garbage Classification (Kaggle)** — 12 subkategori, lebih granular

> [!NOTE]
> Untuk konteks kampus Indonesia, kombinasikan TrashNet + foto lokal (kemasan produk Indonesia) untuk fine-tuning. Tapi dalam 7 hari, skip fine-tuning — pakai Gemini langsung.

### 4.3 Green IT & Efisiensi Sumber Daya

| Praktik | Implementasi | Poin Green IT |
|---|---|---|
| Kompres foto sebelum dikirim | Client-side resize ≤ 800px max-dimension | ✅ Hemat bandwidth & token |
| Strip EXIF metadata | Hapus GPS, device info dari foto | ✅ Privasi + kurangi payload |
| Foto tidak disimpan permanen | Hanya log kategori + timestamp | ✅ Hemat storage |
| Cache hasil AI | Hasil klasifikasi item serupa di-cache 24 jam | ✅ Kurangi API call |
| Fallback lokal | MobileNetV2 offline jika internet mati | ✅ Resiliensi + efisiensi |
| Tampilkan confidence % | Transparansi AI ke user | ✅ AI Ethics |

---

## 5. 📋 INSTRUMEN RISET PENGGUNA

### 5.1 Survei Singkat (Anonim, 8–10 Pertanyaan)

**Judul:** Survei Kebiasaan Pilah Sampah di Kampus (2 menit)
**Target:** 30–50 mahasiswa dari berbagai jurusan, via Google Form

---

**Bagian A: Demografi (tanpa nama)**

**Q1.** Kamu mahasiswa angkatan berapa?
- [ ] 2021 (Semester 7+)
- [ ] 2022 (Semester 5-6)
- [ ] 2023 (Semester 3-4)
- [ ] 2024 (Semester 1-2)

**Q2.** Fakultas/jurusanmu?
_(Dropdown pilihan atau isian singkat)_

---

**Bagian B: Perilaku & Pengetahuan**

**Q3.** Seberapa sering kamu memilah sampah saat di kampus?
- [ ] Selalu
- [ ] Sering (lebih dari setengah waktu)
- [ ] Kadang-kadang
- [ ] Jarang / tidak pernah

**Q4.** Apa alasan utama kamu TIDAK memilah sampah? _(Pilih semua yang relevan)_
- [ ] Bingung jenis sampah apa masuk ke mana
- [ ] Malas / tidak peduli
- [ ] Tong sampah terpilah tidak tersedia di dekat saya
- [ ] Tahu toh akhirnya dicampur juga sama petugas
- [ ] Tidak tahu ada aturan pilah sampah di kampus
- [ ] Lainnya: ___

**Q5.** Pernah ragu masukkan sampah ke mana? Berikan contoh item yang bikin bingung:
_(Isian singkat — ini untuk data kategori bingung)_

---

**Bagian C: Motivasi & Gamifikasi**

**Q6.** Seberapa besar kemungkinan kamu pakai app pilah sampah jika hadiahnya adalah poin yang bisa ditukar diskon di kantin kampus?
- [ ] Sangat mungkin
- [ ] Mungkin
- [ ] Tidak tahu
- [ ] Tidak mungkin

**Q7.** Jika jurusanmu ikut kompetisi pilah sampah vs jurusan lain, seberapa besar kamu termotivasi untuk ikut?
- [ ] Sangat termotivasi (pengin jurusan menang)
- [ ] Agak termotivasi
- [ ] Biasa saja
- [ ] Tidak termotivasi sama sekali

**Q8.** Fitur apa yang paling menarik bagimu? _(Pilih max 2)_
- [ ] AI langsung tahu jenis sampah dari foto
- [ ] Poin & leaderboard jurusan
- [ ] Statistik dampak: "sampah pilihanmu setara X kg CO₂ dihemat"
- [ ] Fun fact unik tentang sampah
- [ ] Dashboard admin kampus
- [ ] Badge & pencapaian

---

**Bagian D: Hambatan Teknologi**

**Q9.** Apa yang akan membuatmu BERHENTI pakai app seperti ini setelah seminggu? _(Pilih semua yang relevan)_
- [ ] AI sering salah tebak
- [ ] Terlalu banyak langkah untuk scan
- [ ] Tidak ada reward nyata
- [ ] Tidak ada teman yang pakai
- [ ] Kebosanan / tidak ada hal baru
- [ ] Lainnya: ___

**Q10. (Opsional)** Ada komentar atau saran untuk app pilah sampah yang ideal di kampusmu?
_(Isian panjang)_

---

### 5.2 Panduan Wawancara — Petugas Kebersihan/Pengelola Sampah Kampus

**Durasi:** 15–20 menit | **Format:** Semi-terstruktur

**Pertanyaan Pembuka (5 menit):**
1. "Bisa ceritakan rutinitas harian Bapak/Ibu dalam mengangkut dan mengelola sampah di area [X]?"
2. "Di area mana biasanya sampah paling banyak dan paling berantakan?"

**Pertanyaan Inti (10 menit):**
3. "Apakah mahasiswa sudah banyak yang memilah sampah dengan benar? Kalau tidak, jenis sampah apa yang paling sering salah masuk tempat?"
4. "Apa yang Bapak/Ibu lakukan ketika tempat sampah organik sudah penuh tapi sampah anorganik masih banyak? Apakah mereka dicampur?"
5. "Pernah ada program atau kampanye pilah sampah dari kampus? Hasilnya bagaimana?"
6. "Kalau ada aplikasi yang bisa bantu pantau area mana yang produksi sampahnya tinggi dan jenis sampahnya, apakah itu berguna untuk pekerjaan Bapak/Ibu?"

**Pertanyaan Penutup (5 menit):**
7. "Apa satu hal yang paling ingin Bapak/Ibu ubah dari sistem pengelolaan sampah kampus sekarang?"
8. "Boleh saya minta izin untuk menampilkan data umum dari percakapan ini (tanpa nama) untuk keperluan penelitian?"

---

**Panduan Wawancara — Pedagang Kantin/Koperasi Kampus**

1. "Jenis kemasan apa yang paling banyak dipakai di kantin Bapak/Ibu? (plastik, styrofoam, kertas?)"
2. "Apakah ada tempat buang sampah terpilah di area kantin? Apakah pelanggan menggunakannya?"
3. "Kalau ada app yang bisa beri poin ke mahasiswa dan poin itu bisa ditukar diskon di sini, apakah Bapak/Ibu tertarik bermitra?"
4. "Berapa banyak sampah dari kantin per hari kira-kira? Sudah ada yang datang untuk ambil sampah daur ulang/bank sampah?"

---

## 6. 📅 RENCANA KERJA 7 HARI (TIM 4 ORANG)

### 6.1 Pembagian Peran

| Peran | Tanggung Jawab Utama | Skill yang Dibutuhkan |
|---|---|---|
| **🎨 Frontend Lead** | UI/UX, halaman user (scan, leaderboard, poin), halaman admin | React/Next.js atau Laravel Blade, CSS |
| **⚙️ Backend Lead** | API, database (user, poin, leaderboard, log), autentikasi | Laravel/Next.js API routes, MySQL/PostgreSQL |
| **🤖 AI Integrator** | Integrasi Gemini API, fallback MobileNetV2, kompresi foto | Python/JS, API calls, image processing |
| **🔧 DevOps & QA** | Setup repo, struktur proyek, testing, seed data, video demo | Git, README, koordinasi |

> [!NOTE]
> Jika tim hanya bisa Next.js full-stack: Frontend Lead = halaman & komponen; Backend Lead = API routes + DB; AI Integrator = server action/route handler untuk Gemini; DevOps = README + repo + video.

### 6.2 Milestone Harian

```
HARI 1 (7 Nov) — Setup & Desain
├── 09:00 Kick-off: finalize scope MVP (jangan tambah fitur!)
├── Semua: buat ERD database + wireframe kasar (Figma/paper)
├── DevOps: init repo GitHub, struktur folder, .env template
├── Backend: buat schema DB (users, jurusan, submissions, poin)
└── Frontend: scaffold Next.js/Laravel + install Tailwind + auth halaman

HARI 2 (8 Nov) — Auth + Core Backend
├── Backend: Auth (register/login dengan kode jurusan), CRUD jurusan
├── Backend: API endpoint: /scan, /leaderboard, /dashboard-admin
├── AI: Integrasi Gemini Flash API → terima foto, return JSON {kategori, cara_buang, fun_fact, confidence}
└── Frontend: Halaman login/register + layout utama

HARI 3 (9 Nov) — Fitur Utama: Scan & Poin
├── Frontend: Halaman scan (upload/kamera) + tampilkan hasil AI
├── Backend: Logika poin (submission → +poin ke jurusan)
├── AI: Kompres foto sebelum kirim, strip EXIF
└── Testing: End-to-end flow scan → poin berhasil

HARI 4 (10 Nov) — Leaderboard + Dashboard Admin
├── Frontend: Halaman leaderboard jurusan (real-time atau polling)
├── Frontend: Dashboard admin (tabel log, grafik area, tren mingguan)
├── Backend: API aggregasi poin per jurusan, filter per area
└── DevOps: Seed data realistis (50 submission dummy lintas jurusan)

HARI 5 (11 Nov) — Polish UI + Fallback + Estimasi Dampak
├── Frontend: Polish semua halaman (mobile responsif, empty state, loading)
├── AI: Implementasi fallback mode (MobileNetV2 lokal atau cache hasil)
├── Backend: Kalkulasi estimasi dampak (kg sampah terpilah, CO₂ hemat)
└── DevOps: Cek semua env variable, pastikan jalan di localhost bersih

HARI 6 (12 Nov) — Testing, Bug Fix, Demo Prep
├── QA: Tes semua skenario edge case (foto buram, API gagal, user baru)
├── FREEZE FITUR — tidak ada fitur baru setelah hari ini!
├── DevOps: Isi seed data yang bagus, screenshot untuk video
└── Mulai rekam video demo (draft)

HARI 7 (13 Nov) — Final Polish + Video Demo + README
├── Semua: Review kode, clean up console.log & debug code
├── Frontend: Perbaikan minor dari testing hari 6
├── DevOps: Finalisasi README (setup guide, arsitektur, cara run)
└── Video Demo: Edit video 3-5 menit tanpa narasi suara (captions jika perlu)

HARI 8 (14 Nov DEADLINE) — Submission
├── Pagi: Final smoke test di mesin yang akan dipakai untuk final
├── Siang: Push kode ke GitHub (JANGAN ubah lagi setelah ini!)
└── Sore: Submit link GitHub + video demo sebelum batas waktu
```

### 6.3 Checklist Deliverable

**Repository GitHub:**
- [ ] README.md lengkap: deskripsi proyek, cara setup, cara run, tech stack, arsitektur
- [ ] `.env.example` (tanpa API key asli)
- [ ] Struktur kode bersih dan terdokumentasi
- [ ] Komentar kode di bagian kritis (AI integration, poin logic)
- [ ] File arsitektur/diagram (bisa Mermaid di README)

**Video Demo (3–5 menit, tanpa narasi suara):**
- [ ] Tunjukkan flow lengkap: login → scan foto → hasil AI → poin jurusan
- [ ] **Wajib: fitur AI terlihat jelas** (sesuai ketentuan lomba)
- [ ] Tampilkan leaderboard jurusan berubah setelah submit
- [ ] Tampilkan dashboard admin
- [ ] Tampilkan fallback mode (offline/cache) — nilai tambah
- [ ] Subtitle/caption jika diperlukan (tanpa narasi suara)
- [ ] Resolusi minimal 1080p, durasi 3–5 menit tepat

**Persiapan Final (29 Nov, localhost):**
- [ ] Test app di laptop yang akan dibawa ke final
- [ ] Pastikan semua dependency ter-install secara lokal
- [ ] Buat startup script satu perintah: `npm run dev` / `php artisan serve`
- [ ] Siapkan data seed yang sudah diisi (jangan mulai dari kosong saat demo)
- [ ] Backup `.env` di tempat aman (jangan cuma di repo)
- [ ] Siapkan slide presentasi 7 menit (problem → solution → demo → dampak → roadmap)
- [ ] Latihan demo minimal 3× dengan simulasi Q&A

---

## 7. ❓ PERTANYAAN JURI & JAWABAN STRATEGIS

### 7.1 Pertanyaan tentang Keberlanjutan & Retensi

**Q: "Orang-orang biasanya pakai app lingkungan seminggu terus bosan. Apa yang membuat EcoSort Campus berbeda?"**

> **Jawaban:** "Kami setuju, ini risiko utama yang kami waspadai dari awal. Itulah kenapa unit kompetisi kami bukan individu, tapi jurusan — sebuah identitas sosial yang sudah ada. Ketika Teknik Elektro vs Akuntansi bersaing di leaderboard, peer pressure internal jurusan bekerja sebagai mesin retensi yang kami tidak perlu bangun dari nol. Kedua, kami dirancang bukan hanya untuk mahasiswa, tapi untuk kampus sebagai institusi — admin dapat data yang mereka butuhkan untuk laporan green campus. Ketika kampus punya kepentingan terhadap data kami, mereka akan mendorong partisipasi secara top-down. Ketiga, musim kompetisi yang reset bulanan/semesteral mencegah leaderboard stagnan."

---

**Q: "Kalau kampus tidak mau adopt, app ini mati kan?"**

> **Jawaban:** "Adopsi kampus adalah hipotesis go-to-market utama kami, bukan asumsi buta. Dalam 2 minggu sebelum hackathon, kami sudah lakukan survei ke 40 mahasiswa dan wawancara 2 koordinator kemahasiswaan. Data awal menunjukkan X% mahasiswa termotivasi oleh kompetisi jurusan dan Y koordinator menyatakan tertarik dengan dashboard admin. Kami juga menghubungkan ke insentif konkret kampus: data kami bisa dipakai sebagai input laporan UI GreenMetric kategori Waste. Kampus yang sadar ranking ini punya alasan kuat untuk mendorong penggunaan."
> *(Isi X dan Y dari survei nyata kamu)*

---

### 7.2 Pertanyaan tentang Akurasi AI

**Q: "Bagaimana kalau AI salah tebak jenis sampah? Bukankah itu justru menyesatkan pengguna?"**

> **Jawaban:** "Kami mendesain untuk akurasi yang transparan, bukan akurasi yang berpura-pura sempurna. Setiap hasil klasifikasi menampilkan confidence score — jika di bawah 75%, app langsung meminta user untuk mengkonfirmasi pilihan dari 2–3 opsi. Koreksi user ini kemudian kami log sebagai feedback loop. Selain itu, kami tidak hanya menyebut 'organik' atau 'anorganik' — kami memberikan instruksi cara buang spesifik ('cuci dulu sebelum masuk tempat plastik') yang tetap berguna meski klasifikasi kategori agak meleset. Untuk konteks hackathon, Gemini Flash sudah menunjukkan akurasi zero-shot yang sangat baik untuk item-item umum kampus."

---

**Q: "Apa fallback-nya kalau API Gemini down atau internet mati saat final?"**

> **Jawaban:** "Kami membangun dua lapis perlindungan: pertama, cache — hasil klasifikasi 20–30 item paling umum (botol plastik, kertas HVS, gelas plastik, sisa makanan) di-hardcode sebagai fallback lokal. Kedua, kami mengintegrasikan model MobileNetV2 ringan yang berjalan di server lokal tanpa internet. Ini memastikan demo final tetap berjalan sepenuhnya offline. Di UI, kami tampilkan indikator 'Offline Mode' agar user tahu perbedaan sumber hasilnya — ini juga merupakan praktik transparansi AI yang baik."

---

### 7.3 Pertanyaan tentang Privasi Data

**Q: "Foto sampah pengguna itu privat. Bagaimana kalau ada foto yang tidak sengaja mengandung informasi pribadi?"**

> **Jawaban:** "Kami menerapkan Privacy by Design dari level client-side: foto dikompres dan EXIF metadata — termasuk GPS dan informasi perangkat — dihapus sebelum meninggalkan browser pengguna. Foto tidak pernah disimpan di database kami; yang kami simpan hanya label klasifikasi, confidence score, timestamp, dan kode area. Kami juga menggunakan Gemini Free Tier yang memang memproses gambar tanpa menyimpannya secara permanen untuk tujuan klasifikasi. Disclaimer ini kami tampilkan di halaman pertama app dan di halaman scan."

---

**Q: "Data poin dan identitas jurusan — apa tidak bisa disalahgunakan?"**

> **Jawaban:** "Data yang kami kumpulkan adalah agregat anonim per jurusan, bukan data individual yang sensitif. Tidak ada informasi kesehatan, finansial, atau lokasi spesifik pengguna. Kami menggunakan autentikasi email kampus untuk verifikasi jurusan, tapi identitas nama tidak ditampilkan di leaderboard publik — hanya username pilihan user dan nama jurusan. Admin hanya melihat log submission tanpa informasi identitas pribadi."

---

### 7.4 Pertanyaan tentang Skalabilitas & Bisnis

**Q: "Ini MVP untuk satu kampus. Bagaimana kalau mau scale ke banyak kampus?"**

> **Jawaban:** "Arsitektur kami dari awal multi-tenant — setiap kampus punya namespace data terpisah. Untuk scale, model bisnisnya adalah B2B2C: kami menjual akses ke admin kampus (atau menjadikannya gratis sebagai CSR tool) dan monetisasi dari data agregat untuk laporan green campus berbayar, atau dari mitra reward (kantin, brand FMCG yang peduli sustainabilitas). Dalam 7 hari ini kami fokus membuktikan satu kampus terlebih dahulu — itu sudah cukup untuk validasi awal."

---

**Q: "Biaya operasional app ini dari mana? Free tier Gemini punya batas."**

> **Jawaban:** "Untuk skala kampus tunggal dengan 1.000 mahasiswa aktif per hari sekalipun, 1.500 request/hari free tier sudah cukup — rata-rata 1,5 scan per user per hari. Untuk skala yang lebih besar, Gemini Flash berbayar hanya sekitar \$0,075 per 1 juta input token; satu gambar kecil ≈ 1.000–2.000 token, artinya 50.000 scan ≈ \$7,5. Sangat terjangkau untuk kampus besar. Kami juga mendesain cache yang memangkas API call secara signifikan."

---

## 8. 📚 DAFTAR SUMBER

### Sumber Terverifikasi (Data Faktual)

| # | Sumber | Data yang Direferensi |
|---|---|---|
| 1 | SIPSN KLHK / kemenlh.go.id | Volume timbulan sampah 56,63 juta ton (2023); 32–39% terkelola |
| 2 | BRIN (brin.go.id) | Komposisi organik 40–41,4%; 61–68% sampah belum terkelola |
| 3 | ResearchGate (berbagai jurnal kampus Indonesia) | Komposisi sampah kampus 55–67% organik; masalah sistem konvensional |
| 4 | UGM (ugm.ac.id) | WESTA Smart Bin: AI klasifikasi 3 kategori, estimasi CO₂, brand tracking |
| 5 | UNEJ (unej.ac.id) | SiLaras: AI waste recognition + bank sampah digital |
| 6 | TrashNet / Stanford (arxiv.org, GitHub) | Dataset 2.527 gambar, 6 kelas; benchmark CNN waste classification |
| 7 | Hugging Face (huggingface.co) | Model MobileNetV2 88–92%, EfficientNet 93–98% akurasi TrashNet |
| 8 | EU JRC / EPA (europa.eu) | Daur ulang hemat 0,5–3+ ton CO₂e per ton material; methane GWP 28–36× |
| 9 | IPCC | GWP metana: 28–36× CO₂ dalam 100 tahun |
| 10 | Google AI Studio (google.dev) | Gemini Flash free tier: 15 RPM, 1.500 req/hari |
| 11 | idealogyjournal.com | Leaderboard kompetisi meningkatkan partisipasi dan norma sosial |
| 12 | thebehaviouralist.com | Gamifikasi: ekstrinsik + intrinsik motivasi untuk pilah sampah |
| 13 | Kopernik.info | Behavioral change: social norm nudge efektif di konteks Asia |
| 14 | UI GreenMetric | Indikator green campus kategori Waste |

### Catatan Metodologi

> [!WARNING]
> **Data yang masih perlu diverifikasi mandiri:**
> - Angka spesifik persentase mahasiswa yang salah pilah sampah → **lakukan survei sendiri (Poin 5)**
> - Rata-rata volume sampah per mahasiswa per hari di kampus spesifik kamu
> - Data UI GreenMetric kampus targetmu (bisa diunduh di greenmetric.ui.ac.id)
> - Tarif API Gemini terbaru → selalu cek di [ai.google.dev/pricing](https://ai.google.dev/pricing) karena bisa berubah

> [!NOTE]
> **[ASUMSI] yang perlu dikonfirmasi sebelum pitch:**
> 1. "Kampus target mau adopt" → validasi dengan wawancara koordinator kemahasiswaan/BEM
> 2. "1.500 req/hari cukup untuk kampus" → hitung berdasarkan jumlah mahasiswa aktif target
> 3. "Kantin mau bermitra untuk reward" → wawancara pengelola kantin kampus

---

*Dokumen ini diperbarui: 23 Oktober 2026 | Prepared for LOGICODIX 2026*
# EcoSortCampus
