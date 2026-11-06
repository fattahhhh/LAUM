<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('campus_areas', function (Blueprint $table) {
            $table->id();
            $table->string('name', 100);
            $table->string('building', 100)->nullable();
            $table->enum('area_type', ['kantin', 'gedung', 'perpustakaan', 'taman', 'lainnya'])->default('lainnya');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('campus_areas');
    }
};
