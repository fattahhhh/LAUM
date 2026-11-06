<?php

namespace Database\Seeders;

use App\Models\Season;
use App\Models\WasteCategory;
use Illuminate\Database\Seeder;

class EcoSortSeeder extends Seeder
{
    public function run(): void
    {
        $categories = [
            ['code' => 'organik',   'name' => 'Sampah Organik',   'bin_color' => 'hijau',   'description' => 'Sisa makanan, daun, dan bahan yang mudah terurai.',          'disposal_guide' => 'Buang ke tong hijau; bisa dijadikan kompos.',               'points_value' => 10],
            ['code' => 'anorganik', 'name' => 'Sampah Anorganik', 'bin_color' => 'kuning',  'description' => 'Plastik, kertas, kaleng, dan kaca yang bisa didaur ulang.', 'disposal_guide' => 'Bersihkan dulu lalu buang ke tong kuning.',               'points_value' => 10],
            ['code' => 'b3',        'name' => 'Sampah B3',        'bin_color' => 'merah',   'description' => 'Baterai, lampu, elektronik, dan bahan berbahaya.',          'disposal_guide' => 'Jangan dicampur; serahkan ke titik pengumpulan B3.',        'points_value' => 15],
            ['code' => 'residu',    'name' => 'Sampah Residu',    'bin_color' => 'abu-abu', 'description' => 'Sampah yang sulit didaur ulang.',                           'disposal_guide' => 'Buang ke tong abu-abu.',                                    'points_value' => 5],
        ];

        foreach ($categories as $category) {
            WasteCategory::updateOrCreate(['code' => $category['code']], $category);
        }

        Season::updateOrCreate(
            ['name' => 'Musim 1'],
            ['start_date' => '2026-11-01', 'end_date' => '2026-11-30', 'is_active' => true]
        );
    }
}
