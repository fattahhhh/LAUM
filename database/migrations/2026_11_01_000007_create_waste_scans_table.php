<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('waste_scans', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->cascadeOnDelete();
            $table->foreignId('season_id')->nullable()->constrained('seasons')->nullOnDelete();
            $table->foreignId('area_id')->nullable()->constrained('campus_areas')->nullOnDelete();
            $table->foreignId('bin_id')->nullable()->constrained('bins')->nullOnDelete();
            $table->foreignId('category_id')->constrained('waste_categories');   // hasil prediksi AI
            $table->string('item_name', 100)->nullable();
            $table->text('fun_fact')->nullable();
            $table->decimal('ai_confidence', 5, 4)->nullable();    // 0.0000 - 1.0000
            $table->string('ai_provider', 50)->nullable();
            $table->string('ai_model', 80)->nullable();
            $table->unsignedInteger('ai_latency_ms')->nullable();
            $table->unsignedInteger('image_size_kb')->nullable();  // ukuran setelah dikompres
            $table->string('image_path', 255)->nullable();         // default null: foto tidak disimpan permanen
            $table->timestamp('image_deleted_at')->nullable();
            $table->enum('user_feedback', ['pending', 'correct', 'incorrect'])->default('pending');
            $table->foreignId('corrected_category_id')->nullable()->constrained('waste_categories')->nullOnDelete();
            $table->unsignedSmallInteger('points_awarded')->default(0);
            $table->timestamps();

            $table->index(['user_id', 'created_at']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('waste_scans');
    }
};
