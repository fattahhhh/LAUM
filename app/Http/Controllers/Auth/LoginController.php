<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\RateLimiter;
use Illuminate\Support\Facades\Route;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;
use Illuminate\View\View;

class LoginController extends Controller
{
    private const MAX_ATTEMPTS = 5;

    public function show(): View
    {
        return view()->exists('auth.login') ? view('auth.login') : view('login');
    }

    public function store(Request $request): RedirectResponse
    {
        $data = $request->validate([
            'username' => ['required', 'string', 'max:30'],
            'password' => ['required', 'string'],
        ], [
            'username.required' => 'Username wajib diisi.',
            'password.required' => 'Kata sandi wajib diisi.',
        ]);

        $username = trim($data['username']);
        $throttleKey = Str::lower($username).'|'.$request->ip();

        if (RateLimiter::tooManyAttempts($throttleKey, self::MAX_ATTEMPTS)) {
            $seconds = RateLimiter::availableIn($throttleKey);
            throw ValidationException::withMessages([
                'username' => "Terlalu banyak percobaan. Coba lagi dalam {$seconds} detik.",
            ]);
        }

        // Akun nonaktif: beri pesan jelas hanya jika kata sandinya benar
        $user = User::where('username', $username)->first();
        if ($user && ! $user->is_active && Hash::check($data['password'], $user->password)) {
            throw ValidationException::withMessages([
                'username' => 'Akun ini dinonaktifkan. Hubungi admin kampus.',
            ]);
        }

        $credentials = [
            'username'  => $username,
            'password'  => $data['password'],
            'is_active' => true,
        ];

        if (! Auth::attempt($credentials, $request->boolean('remember'))) {
            RateLimiter::hit($throttleKey, 60);

            throw ValidationException::withMessages([
                'username' => 'Username atau kata sandi salah.',
            ]);
        }

        RateLimiter::clear($throttleKey);
        $request->session()->regenerate();

        return redirect()->intended($this->homeFor(Auth::user()));
    }

    public function destroy(Request $request): RedirectResponse
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect()->route('login')->with('status', 'Kamu sudah keluar.');
    }

    /**
     * Tujuan setelah login. Sesuaikan nama route dengan milikmu;
     * kalau route belum ada, otomatis jatuh ke halaman utama.
     */
    private function homeFor(User $user): string
    {
        $candidates = $user->isStaff() ? ['admin.dashboard', 'dashboard'] : ['dashboard'];

        foreach ($candidates as $name) {
            if (Route::has($name)) {
                return route($name);
            }
        }

        return '/';
    }
}