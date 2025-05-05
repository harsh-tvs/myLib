package com.tvsm.iqubeindia.di.module

import android.content.Context
import android.content.SharedPreferences
import com.tvsm.connect.R
import com.tvsm.iqubeindia.di.AppSettingsSharedPreference
import com.tvsm.iqubeindia.domain.repository.DispatchersProvider
import com.tvsm.iqubeindia.domain.repository.DispatchersProviderImpl
import com.tvsm.iqubeindia.presentation.ui.utils.NetworkMonitor

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideDispatchersProvider(): DispatchersProvider {
        return DispatchersProviderImpl
    }

    @Provides
    @AppSettingsSharedPreference
    fun provideAppSettingsSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        val preferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name),  Context.MODE_PRIVATE)
        return preferences
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitor(context)
}