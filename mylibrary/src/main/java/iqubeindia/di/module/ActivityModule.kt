package com.tvsm.iqubeindia.di.module

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideComponentActivityContext(activity: ComponentActivity): ComponentActivity {
        return activity
    }

    @Provides
    @ActivityContext
    fun provideActivityContext(activity: Activity): Context {
        return activity
    }
}