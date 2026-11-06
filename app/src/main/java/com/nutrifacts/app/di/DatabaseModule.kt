package com.nutrifacts.app.di

import android.content.Context
import androidx.room.Room
import com.nutrifacts.app.data.local.AppDatabase
import com.nutrifacts.app.data.local.FoodDao
import com.nutrifacts.app.data.local.RecentSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nutrifacts_database"
        ).build()

    @Provides
    @Singleton
    fun provideFoodDao(
        database: AppDatabase
    ): FoodDao = database.foodDao()

    @Provides
    @Singleton
    fun provideRecentSearchDao(
        database: AppDatabase
    ): RecentSearchDao = database.recentSearchDao()
}