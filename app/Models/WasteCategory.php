<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class WasteCategory extends Model
{
    protected $fillable = [
        'code', 'name', 'bin_color', 'description', 'disposal_guide',
        'avg_weight_kg', 'co2_saved_kg', 'points_value',
    ];

    protected function casts(): array
    {
        return [
            'avg_weight_kg' => 'decimal:3',
            'co2_saved_kg'  => 'decimal:3',
            'points_value'  => 'integer',
        ];
    }

    public function scans(): HasMany
    {
        return $this->hasMany(WasteScan::class, 'category_id');
    }

    public function bins(): HasMany
    {
        return $this->hasMany(Bin::class, 'category_id');
    }
}
