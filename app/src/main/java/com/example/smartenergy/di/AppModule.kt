package com.example.smartenergy.di

import android.content.Context
import androidx.room.Room
import com.example.smartenergy.data.local.SmartEnergyDao
import com.example.smartenergy.data.local.SmartEnergyDatabase
import com.example.smartenergy.data.repository.SmartEnergyRepositoryImpl
import com.example.smartenergy.domain.repository.SmartEnergyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): SmartEnergyDatabase =
        Room.databaseBuilder(context, SmartEnergyDatabase::class.java, "smart-energy.db").build()

    @Provides
    fun provideDao(db: SmartEnergyDatabase): SmartEnergyDao = db.dao()

    @Provides
    @Singleton
    fun provideRepo(impl: SmartEnergyRepositoryImpl): SmartEnergyRepository = impl
}
