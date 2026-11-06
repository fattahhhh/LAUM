<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Bin extends Model
{
    protected $fillable = ['area_id', 'category_id', 'qr_code', 'label', 'status'];

    public function area(): BelongsTo
    {
        return $this->belongsTo(CampusArea::class, 'area_id');
    }

    public function category(): BelongsTo
    {
        return $this->belongsTo(WasteCategory::class, 'category_id');
    }

    public function scans(): HasMany
    {
        return $this->hasMany(WasteScan::class);
    }

    public function reports(): HasMany
    {
        return $this->hasMany(Report::class);
    }
}
