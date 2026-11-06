<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard – EcoSort Campus</title>
  @vite(['resources/css/app.css', 'resources/js/app.js'])
</head>
<body class="bg-moss-50 min-h-screen text-slate-800 antialiased dark:bg-moss-950 dark:text-slate-200">
  <header class="border-b border-slate-200 bg-white shadow-xs dark:border-white/10 dark:bg-ink">
    <div class="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
      <div class="flex items-center gap-3">
        <div class="grid h-10 w-10 place-items-center rounded-lg bg-bin-organik text-white font-extrabold text-xl shadow-xs">
          E
        </div>
        <div>
          <h1 class="font-display font-extrabold text-lg text-ink dark:text-white leading-tight">EcoSort Campus</h1>
          <p class="text-xs text-slate-500">Sistem Pemilahan Sampah Kampus</p>
        </div>
      </div>
      <div class="flex items-center gap-4">
        <span class="text-sm font-medium text-slate-600 dark:text-slate-300">
          Hai, <strong class="text-ink dark:text-white">{{ auth()->user()->display_name ?? auth()->user()->name ?? auth()->user()->username }}</strong>
        </span>
        <form action="{{ route('logout') }}" method="POST">
          @csrf
          <button type="submit" class="rounded-lg border border-slate-300 bg-white px-3.5 py-1.5 text-xs font-semibold text-slate-700 transition hover:bg-slate-100 hover:text-red-600 focus:outline-hidden dark:border-white/15 dark:bg-white/5 dark:text-slate-300 dark:hover:bg-white/10 dark:hover:text-red-400">
            Keluar
          </button>
        </form>
      </div>
    </div>
  </header>

  <main class="mx-auto max-w-7xl px-6 py-10">
    <div class="rounded-2xl border-2 border-ink bg-white p-6 shadow-[5px_5px_0_#0e1a1f] dark:border-white/20 dark:bg-ink dark:shadow-[5px_5px_0_#2fa56a]">
      <h2 class="font-display text-2xl font-black text-ink dark:text-white">Selamat Datang di EcoSort!</h2>
      <p class="mt-1 text-sm text-slate-600 dark:text-slate-400">Kamu berhasil masuk ke sistem pemilahan sampah EcoSort Campus.</p>

      <div class="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div class="rounded-xl border border-slate-200 bg-slate-50 p-4 dark:border-white/10 dark:bg-white/5">
          <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">Total Poin</p>
          <p class="mt-2 font-display text-3xl font-extrabold text-bin-organik">{{ auth()->user()->total_points ?? 0 }}</p>
        </div>
        <div class="rounded-xl border border-slate-200 bg-slate-50 p-4 dark:border-white/10 dark:bg-white/5">
          <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">Streak Saat Ini</p>
          <p class="mt-2 font-display text-3xl font-extrabold text-ink dark:text-white">{{ auth()->user()->current_streak ?? 0 }} Hari</p>
        </div>
        <div class="rounded-xl border border-slate-200 bg-slate-50 p-4 dark:border-white/10 dark:bg-white/5">
          <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">Peran / Role</p>
          <p class="mt-2 font-display text-2xl font-extrabold capitalize text-ink dark:text-white">{{ auth()->user()->role ?? 'Student' }}</p>
        </div>
      </div>
    </div>
  </main>
</body>
</html>

