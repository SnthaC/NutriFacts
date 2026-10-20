package com.nutrifacts.app.di


import com.nutrifacts.app.data.repository.FoodRepositoryImpl
import com.nutrifacts.app.domain.repository.FoodRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFoodRepository(
        implementation: FoodRepositoryImpl
    ): FoodRepository
}