package com.nutrifacts.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before

abstract class DatabaseTest {

    protected lateinit var database: AppDatabase
    protected lateinit var foodDao: FoodDao
    protected lateinit var recentSearchDao: RecentSearchDao

    @Before
    fun setUpDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        foodDao = database.foodDao()
        recentSearchDao = database.recentSearchDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}