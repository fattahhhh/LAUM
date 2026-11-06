<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class WasteScan extends Model
{
    protected $fillable = [
        'user_id', 'season_id', 'area_id', 'bin_id', 'category_id',
        'item_name', 'fun_fact', 'ai_confidence', 'ai_provider', 'ai_model',
        'ai_latency_ms', 'image_size_kb', 'image_path', 'image_deleted_at',
        'user_feedback', 'corrected_category_id', 'points_awarded',
    ];

    protected function casts(): array
    {
        return [
            'ai_confidence'    => 'decimal:4',
            'ai_latency_ms'    => 'integer',
            'image_size_kb'    => 'integer',
            'points_awarded'   => 'integer',
            'image_deleted_at' => 'datetime',
        ];
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function season(): BelongsTo
    {
        return $this->belongsTo(Season::class);
    }

    public function area(): BelongsTo
    {
        return $this->belongsTo(CampusArea::class, 'area_id');
    }

    public function bin(): BelongsTo
    {
        return $this->belongsTo(Bin::class);
    }

    // Kategori hasil prediksi AI
    public function category(): BelongsTo
    {
        return $this->belongsTo(WasteCategory::class, 'category_id');
    }

    // Kategori hasil koreksi user (kalau AI salah)
    public function correctedCategory(): BelongsTo
    {
        return $this->belongsTo(WasteCategory::class, 'corrected_category_id');
    }

    public function pointTransactions(): HasMany
    {
        return $this->hasMany(PointTransaction::class, 'scan_id');
    }
}
