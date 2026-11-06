<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="csrf-token" content="{{ csrf_token() }}">
  <title>Masuk – EcoSort Campus</title>
  <script>
    try {
      const s = localStorage.getItem('theme');
      document.documentElement.classList.toggle('dark', s ? s === 'dark' : matchMedia('(prefers-color-scheme: dark)').matches);
    } catch (e) {}
  </script>
  @vite(['resources/css/app.css', 'resources/js/app.js'])
</head>

<body class="bg-white font-sans text-slate-800 antialiased dark:bg-moss-950 dark:text-slate-200">
  <!-- garis empat warna tong (mobile) -->
  <div class="flex h-2 lg:hidden" aria-hidden="true">
    <span class="flex-1 bg-bin-organik"></span><span class="flex-1 bg-bin-anorganik"></span><span class="flex-1 bg-bin-b3"></span><span class="flex-1 bg-bin-residu"></span>
  </div>

  <main class="flex min-h-screen">

    <!-- ===== SCENE: langit, bukit, sampah melayang, empat tong ===== -->
    <section id="scene" aria-hidden="true" class="relative hidden flex-1 overflow-hidden bg-[#cdebf1] text-ink lg:block dark:bg-[#0f2730] dark:text-white">

      <!-- matahari / bulan -->
      <div data-depth="0.2" class="absolute inset-0">
        <div class="absolute right-[10%] top-[9%] h-24 w-24 rounded-full bg-[#ffc93c] dark:bg-[#e6eef2]"></div>
      </div>
      <!-- awan -->
      <div data-depth="0.35" class="absolute inset-0">
        <div class="absolute left-[46%] top-[12%] h-8 w-32 rounded-full bg-white/80 dark:bg-white/5"></div>
        <div class="absolute left-[70%] top-[28%] h-6 w-24 rounded-full bg-white/70 dark:bg-white/5"></div>
      </div>

      <!-- bukit belakang & tengah -->
      <div data-depth="0.4" class="absolute -inset-x-16 -bottom-12 h-88">
        <svg viewBox="0 0 1200 400" preserveAspectRatio="none" class="h-full w-full"><path class="fill-[#9bd3a0] dark:fill-[#1a4a47]" d="M0 200C200 110 420 250 660 180S1010 110 1200 190V400H0Z"/></svg>
      </div>
      <div data-depth="0.7" class="absolute -inset-x-16 -bottom-12 h-72">
        <svg viewBox="0 0 1200 400" preserveAspectRatio="none" class="h-full w-full"><path class="fill-[#62b874] dark:fill-[#143c3b]" d="M0 240C250 160 450 290 700 220S1050 180 1200 240V400H0Z"/></svg>
      </div>

      <!-- sampah melayang -->
      <div data-depth="0.9" class="absolute left-[54%] top-[22%]"><div class="animate-float" style="--r:-12deg;animation-duration:8s">
        <svg viewBox="0 0 40 80" class="w-9 fill-none stroke-current" stroke-width="2.5" stroke-linejoin="round"><rect x="14" y="1" width="12" height="8" rx="2" fill="#3b82c4"/><path d="M12 10h16l4 14v50a6 6 0 0 1-6 6H14a6 6 0 0 1-6-6V24z" fill="#cfeaf7"/><path d="M8 40h24v16H8z" fill="#3b82c4"/></svg></div></div>

      <div data-depth="1.3" class="absolute left-[78%] top-[40%]"><div class="animate-float" style="--r:10deg;animation-duration:6s;animation-delay:-2s">
        <svg viewBox="0 0 40 56" class="w-10 fill-none stroke-current" stroke-width="2.5" stroke-linejoin="round"><path d="M6 8h28v40a6 6 0 0 1-6 6H12a6 6 0 0 1-6-6z" fill="#f4c430"/><ellipse cx="20" cy="8" rx="14" ry="4" fill="#ffe48a"/><path d="M6 24h28v12H6z" fill="#e5483a"/></svg></div></div>

      <div data-depth="1.1" class="absolute left-[36%] top-[52%]"><div class="animate-float" style="--r:-6deg;animation-duration:7.5s;animation-delay:-4s">
        <svg viewBox="0 0 48 52" class="w-11 fill-none stroke-current" stroke-width="2.5" stroke-linejoin="round" stroke-linecap="round"><path d="M24 15c-6-5-18-2-18 14 0 12 8 21 14 21 2 0 3-1 4-1s2 1 4 1c6 0 14-9 14-21 0-16-12-19-18-14z" fill="#e5483a"/><path d="M24 15c0-5 2-9 6-12"/><path d="M30 6c4-2 8 0 8 0s-2 5-8 4z" fill="#2fa56a"/></svg></div></div>

      <div data-depth="0.7" class="absolute left-[64%] top-[56%]"><div class="animate-float" style="--r:14deg;animation-duration:9s;animation-delay:-1s">
        <svg viewBox="0 0 56 40" class="w-12 fill-none stroke-current" stroke-width="2.5" stroke-linejoin="round"><path d="M4 8c8 4 40 4 48 0v24c-8-4-40-4-48 0z" fill="#e3e8ea"/><path d="M12 15h12M12 22h20"/></svg></div></div>

      <div data-depth="1.5" class="absolute left-[88%] top-[64%]"><div class="animate-float" style="--r:-18deg;animation-duration:6.5s;animation-delay:-3s">
        <svg viewBox="0 0 36 56" class="w-8 fill-none stroke-current" stroke-width="2.5" stroke-linejoin="round"><rect x="12" y="2" width="12" height="7" rx="1.5" fill="#f4c430"/><rect x="5" y="9" width="26" height="44" rx="5" fill="#e5483a"/><path d="M20 18l-6 12h6l-3 11" stroke-linecap="round"/></svg></div></div>

      <!-- bukit depan + empat tong (sesuai waste_categories) -->
      <div data-depth="1" class="absolute -inset-x-16 -bottom-12 h-60">
        <svg viewBox="0 0 1200 400" preserveAspectRatio="none" class="h-full w-full"><path class="fill-[#3e9a5e] dark:fill-[#0d3030]" d="M0 150C300 90 620 190 900 130S1110 120 1200 140V400H0Z"/></svg>
      </div>
      <div data-depth="1" class="absolute inset-x-0 bottom-3 flex justify-center gap-3 xl:gap-6">
        <div class="animate-wipe flex w-24 flex-col items-center xl:w-28" style="animation-delay:.1s">
          <div class="h-2.5 w-[112%] rounded-full bg-bin-organik brightness-90"></div>
          <div class="grid h-28 w-full place-items-center bg-bin-organik [clip-path:polygon(0_0,100%_0,92%_100%,8%_100%)]"><span class="text-sm font-bold text-white">Organik</span></div>
        </div>
        <div class="animate-wipe flex w-24 flex-col items-center xl:w-28" style="animation-delay:.2s">
          <div class="h-2.5 w-[112%] rounded-full bg-bin-anorganik brightness-90"></div>
          <div class="grid h-28 w-full place-items-center bg-bin-anorganik [clip-path:polygon(0_0,100%_0,92%_100%,8%_100%)]"><span class="text-sm font-bold text-ink">Anorganik</span></div>
        </div>
        <div class="animate-wipe flex w-24 flex-col items-center xl:w-28" style="animation-delay:.3s">
          <div class="h-2.5 w-[112%] rounded-full bg-bin-b3 brightness-90"></div>
          <div class="grid h-28 w-full place-items-center bg-bin-b3 [clip-path:polygon(0_0,100%_0,92%_100%,8%_100%)]"><span class="text-sm font-bold text-white">B3</span></div>
        </div>
        <div class="animate-wipe flex w-24 flex-col items-center xl:w-28" style="animation-delay:.4s">
          <div class="h-2.5 w-[112%] rounded-full bg-bin-residu brightness-90"></div>
          <div class="grid h-28 w-full place-items-center bg-bin-residu [clip-path:polygon(0_0,100%_0,92%_100%,8%_100%)]"><span class="text-sm font-bold text-ink">Residu</span></div>
        </div>
      </div>

      <!-- teks (statis) -->
      <div class="relative z-10 p-12 xl:p-16">
        <a href="/" class="flex items-center gap-2.5">
          <span class="grid h-9 w-9 place-items-center rounded-full bg-ink text-white dark:bg-white dark:text-ink">
            <svg viewBox="0 0 24 24" class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.5 19 2c1 2 2 4.2 2 8 0 5.5-4.8 10-10 10Z"/><path d="M2 21c0-3 1.9-5.5 6-7"/></svg>
          </span>
          <span class="font-display text-lg font-extrabold tracking-tight">EcoSort Campus</span>
        </a>
        <h2 class="mt-14 max-w-md font-display text-4xl font-extrabold leading-[0.98] tracking-tighter xl:max-w-lg xl:text-6xl">
          Pilah sampahmu. Menangkan jurusanmu.
        </h2>
        <p class="mt-5 max-w-sm text-lg leading-snug opacity-75">
          Foto sampahnya, AI kasih tahu tongnya, poinnya masuk ke jurusanmu.
        </p>
      </div>
    </section>

    <!-- ===== FORM ===== -->
    <section class="relative flex w-full items-center justify-center px-6 py-16 sm:px-12 lg:w-104 lg:shrink-0 lg:px-10 xl:w-128">

      <button type="button" id="themeToggle" aria-label="Ganti tema terang/gelap"
              class="absolute right-5 top-5 grid h-10 w-10 place-items-center rounded-full text-slate-500 transition hover:bg-slate-100 focus:outline-hidden focus-visible:ring-4 focus-visible:ring-moss-600/20 dark:text-slate-400 dark:hover:bg-white/10">
        <svg class="h-5 w-5 dark:hidden" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z"/></svg>
        <svg class="hidden h-5 w-5 dark:block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>
      </button>

      <div class="w-full max-w-sm">
        <a href="/" class="mb-10 flex items-center gap-2.5 font-display text-lg font-extrabold tracking-tight text-ink lg:hidden dark:text-white">
          <svg viewBox="0 0 24 24" class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.5 19 2c1 2 2 4.2 2 8 0 5.5-4.8 10-10 10Z"/><path d="M2 21c0-3 1.9-5.5 6-7"/></svg>
          EcoSort Campus
        </a>

        <h1 class="font-display text-4xl font-extrabold tracking-tighter text-ink dark:text-white">Masuk</h1>
        <p class="mt-2 text-slate-600 dark:text-slate-400">Pakai username dan kata sandi akun EcoSort-mu.</p>

        @if (session('status'))
          <div class="mt-6 border-l-4 border-bin-organik bg-moss-50 px-4 py-3 text-sm text-moss-800 dark:bg-white/10 dark:text-slate-200" role="status">{{ session('status') }}</div>
        @endif

        <form id="loginForm" method="POST" action="{{ route('login.store') }}" class="mt-8 space-y-5">
          @csrf

          <div>
            <label for="username" class="mb-1.5 block text-sm font-semibold">Username</label>
            <input id="username" name="username" type="text" value="{{ old('username') }}" maxlength="30" required autofocus
                   autocomplete="username" autocapitalize="none" spellcheck="false" placeholder="contoh: budi.sipil"
                   @error('username') aria-invalid="true" aria-describedby="usernameError" @enderror
                   class="block w-full rounded-lg border-2 bg-white px-4 py-3 text-slate-900 placeholder:text-slate-400 transition focus:outline-hidden dark:bg-white/5 dark:text-white dark:placeholder:text-slate-500 @error('username') border-bin-b3 @else border-slate-200 focus:border-ink dark:border-white/15 dark:focus:border-bin-organik @enderror">
            @error('username')
              <p id="usernameError" class="mt-1.5 text-sm font-medium text-bin-b3" role="alert">{{ $message }}</p>
            @enderror
          </div>

          <div>
            <label for="password" class="mb-1.5 block text-sm font-semibold">Kata sandi</label>
            <div class="relative">
              <input id="password" name="password" type="password" required autocomplete="current-password" placeholder="Kata sandi"
                     @error('password') aria-invalid="true" @enderror
                     class="block w-full rounded-lg border-2 bg-white py-3 pl-4 pr-12 text-slate-900 placeholder:text-slate-400 transition focus:outline-hidden dark:bg-white/5 dark:text-white dark:placeholder:text-slate-500 @error('password') border-bin-b3 @else border-slate-200 focus:border-ink dark:border-white/15 dark:focus:border-bin-organik @enderror">
              <button type="button" id="togglePw" aria-label="Tampilkan kata sandi" class="absolute inset-y-0 right-0 grid w-12 place-items-center text-slate-500 hover:text-ink focus:outline-hidden dark:text-slate-400 dark:hover:text-white">
                <svg id="eyeOn" viewBox="0 0 24 24" class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg id="eyeOff" viewBox="0 0 24 24" class="hidden h-5 w-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9.9 4.2A10 10 0 0 1 12 4c6.5 0 10 8 10 8a17 17 0 0 1-3.1 4.2M6.6 6.6A17 17 0 0 0 2 12s3.5 8 10 8a9.7 9.7 0 0 0 5.4-1.6"/><path d="m2 2 20 20"/><path d="M9.9 9.9a3 3 0 0 0 4.2 4.2"/></svg>
              </button>
            </div>
            @error('password') <p class="mt-1.5 text-sm font-medium text-bin-b3" role="alert">{{ $message }}</p> @enderror
          </div>

          <label class="flex cursor-pointer items-center gap-2.5 text-sm text-slate-600 dark:text-slate-400">
            <input type="checkbox" name="remember" value="1" @checked(old('remember')) class="h-4 w-4 rounded border-slate-300 text-ink focus:ring-moss-600">
            Ingat saya di perangkat ini
          </label>

          <button type="submit" id="submitBtn"
                  class="flex w-full items-center justify-center gap-2 rounded-lg bg-ink px-4 py-3.5 font-bold text-white transition hover:bg-moss-800 focus:outline-hidden focus-visible:ring-4 focus-visible:ring-moss-600/30 disabled:cursor-not-allowed disabled:opacity-70 dark:bg-bin-organik dark:text-ink dark:hover:bg-bin-organik/90">
            <svg id="spinner" class="hidden h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" class="opacity-25"/><path d="M4 12a8 8 0 0 1 8-8" stroke="currentColor" stroke-width="4" stroke-linecap="round"/></svg>
            <span id="btnText">Masuk</span>
          </button>
        </form>

        <p class="mt-8 text-sm text-slate-600 dark:text-slate-400">
          Belum punya akun?
          @if (Route::has('register'))
            <a href="{{ route('register') }}" class="font-bold text-ink underline decoration-bin-organik decoration-2 underline-offset-4 dark:text-white">Daftar pakai kode jurusan</a>
          @else
            <span class="font-bold text-ink dark:text-white">Daftar pakai kode jurusan</span>
          @endif
        </p>
        <p class="mt-3 text-xs text-slate-500">Fotomu tidak disimpan. Yang dicatat hanya kategori sampah dan waktu.</p>
      </div>
    </section>
  </main>

  <script>
    document.getElementById('themeToggle').addEventListener('click', () => {
      const d = document.documentElement.classList.toggle('dark');
      try { localStorage.setItem('theme', d ? 'dark' : 'light'); } catch (e) {}
    });

    const pw = document.getElementById('password');
    document.getElementById('togglePw').addEventListener('click', () => {
      const show = pw.type === 'password';
      pw.type = show ? 'text' : 'password';
      document.getElementById('eyeOn').classList.toggle('hidden', show);
      document.getElementById('eyeOff').classList.toggle('hidden', !show);
    });

    document.getElementById('loginForm').addEventListener('submit', (e) => {
      if (!e.target.checkValidity()) return;
      document.getElementById('submitBtn').disabled = true;
      document.getElementById('spinner').classList.remove('hidden');
      document.getElementById('btnText').textContent = 'Memeriksa akun…';
    });

    // parallax mengikuti kursor: data-depth makin besar = layer makin dekat
    (() => {
      if (matchMedia('(prefers-reduced-motion: reduce)').matches) return;
      const layers = [...document.querySelectorAll('#scene [data-depth]')];
      const SX = 36, SY = 22; // kuat-lemahnya efek
      let tx = 0, ty = 0, cx = 0, cy = 0;
      addEventListener('pointermove', (e) => {
        tx = (e.clientX / innerWidth - 0.5) * 2;
        ty = (e.clientY / innerHeight - 0.5) * 2;
      });
      (function tick() {
        cx += (tx - cx) * 0.07;
        cy += (ty - cy) * 0.07;
        for (const l of layers) {
          const d = parseFloat(l.dataset.depth);
          l.style.translate = `${(-cx * d * SX).toFixed(1)}px ${(-cy * d * SY).toFixed(1)}px`;
        }
        requestAnimationFrame(tick);
      })();
    })();
  </script>
</body>
</html>