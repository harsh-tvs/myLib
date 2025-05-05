package com.tvsm.iqubeindia.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.SubscriptionWsProtocol
import com.google.android.gms.wearable.*
import com.tvsm.connect.BuildConfig
import com.tvsm.iqubeindia.di.AppSettingsSharedPreference
import com.tvsm.iqubeindia.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideVehicleDetailsRecordRepository(@ApplicationContext context: Context,
        @AppSettingsSharedPreference mPreferences: SharedPreferences
    ): VehicleDetailsRecordRepository = VehicleDetailsRecordRepository(context, mPreferences)

    @Provides
    @Singleton
    fun provideCodpRepository(vehicleDetailsRecordRepository: VehicleDetailsRecordRepository
    ): CodpRepository = CodpRepositoryImpl(vehicleDetailsRecordRepository)

    @Provides
    @Singleton
    fun provideDataClient(@ApplicationContext context: Context): DataClient = Wearable.getDataClient(context)

    @Provides
    @Singleton
    fun provideNodeClient(@ApplicationContext context: Context): NodeClient = Wearable.getNodeClient(context)

    @Provides
    @Singleton
    fun provideMessageClient(@ApplicationContext context: Context): MessageClient = Wearable.getMessageClient(context)

    @Provides
    @Singleton
    fun provideCapabilityClient(@ApplicationContext context: Context): CapabilityClient = Wearable.getCapabilityClient(context)

    @Provides
    @Singleton
    fun provideRemoteActivityHelper(@ApplicationContext context: Context): RemoteActivityHelper =
        RemoteActivityHelper(context)

    @Provides
    @Singleton
    fun providePhoneConnectionCheckRepository(@ApplicationContext mActivity: Context, mNodeClient: NodeClient,
                                              mCapabilityClient: CapabilityClient, mRemoteActivityHelper : RemoteActivityHelper
    ): PhoneConnectionCheckRepository = PhoneConnectionCheckImpl(mActivity, mNodeClient, mCapabilityClient, mRemoteActivityHelper)

    @Provides
    @Singleton
    fun provideWearableListenerRepository(@ApplicationContext mActivity: Context, mNodeClient: NodeClient, mDataClient: DataClient,
                                          mCapabilityClient: CapabilityClient, mMessageClient: MessageClient,
                                          vehicleDetailsRecordRepository: VehicleDetailsRecordRepository
    ): WearableListenerRepository = WearableListenerImpl(mActivity, mNodeClient, mDataClient, mCapabilityClient, mMessageClient,
        vehicleDetailsRecordRepository)
}