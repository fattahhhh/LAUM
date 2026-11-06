<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('waste_categories', function (Blueprint $table) {
            $table->id();
            $table->string('code', 20)->unique();    // organik, anorganik, b3, residu
            $table->string('name', 50);
            $table->string('bin_color', 20)->nullable();
            $table->text('description')->nullable();
            $table->text('disposal_guide')->nullable();
            $table->decimal('avg_weight_kg', 6, 3)->nullable();  // estimasi, isi dari sumber resmi
            $table->decimal('co2_saved_kg', 6, 3)->nullable();   // estimasi, isi dari sumber resmi
            $table->unsignedSmallInteger('points_value')->default(10);
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('waste_categories');
    }
};
