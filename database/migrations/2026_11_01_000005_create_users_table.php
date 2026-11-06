<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->string('username', 30)->unique();
            $table->string('display_name', 50)->nullable();
            $table->enum('role', ['student', 'staff', 'admin'])
                  ->default('student');
            $table->foreignId('department_id')
                  ->nullable()
                  ->constrained('departments')
                  ->nullOnDelete();
            $table->unsignedInteger('total_points')->default(0);
            $table->unsignedSmallInteger('current_streak')->default(0);
            $table->unsignedSmallInteger('longest_streak')->default(0);
            $table->date('last_scan_date')->nullable();
            $table->boolean('is_active')->default(true);
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropForeign(['department_id']);
            $table->dropUnique(['username']);
            $table->dropColumn([
                'username',
                'display_name',
                'role',
                'department_id',
                'total_points',
                'current_streak',
                'longest_streak',
                'last_scan_date',
                'is_active',
            ]);
        });
    }
};