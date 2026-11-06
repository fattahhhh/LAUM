<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class CampusArea extends Model
{
    protected $fillable = ['name', 'building', 'area_type'];

    public function bins(): HasMany
    {
        return $this->hasMany(Bin::class, 'area_id');
    }

    public function scans(): HasMany
    {
        return $this->hasMany(WasteScan::class, 'area_id');
    }

    public function reports(): HasMany
    {
        return $this->hasMany(Report::class, 'area_id');
    }
}
