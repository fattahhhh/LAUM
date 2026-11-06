<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class DepartmentScore extends Model
{
    protected $fillable = ['season_id', 'department_id', 'total_points', 'total_scans'];

    protected function casts(): array
    {
        return [
            'total_points' => 'integer',
            'total_scans'  => 'integer',
        ];
    }

    public function season(): BelongsTo
    {
        return $this->belongsTo(Season::class);
    }

    public function department(): BelongsTo
    {
        return $this->belongsTo(Department::class);
    }
}
