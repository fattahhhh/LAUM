<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        $this->call(EcoSortSeeder::class);

        User::updateOrCreate(
            ['username' => 'budi.sipil'],
            [
                'name' => 'Budi Santoso',
                'display_name' => 'Budi',
                'email' => 'budi@example.com',
                'password' => Hash::make('password123'),
                'role' => 'student',
                'is_active' => true,
                'total_points' => 120,
                'current_streak' => 5,
            ]
        );
    }
}
