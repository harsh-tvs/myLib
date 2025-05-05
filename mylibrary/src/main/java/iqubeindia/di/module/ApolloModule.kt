package com.tvsm.iqubeindia.di.module

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.SubscriptionWsProtocol
import com.tvsm.connect.BuildConfig
import com.tvsm.iqubeindia.domain.repository.VehicleDetailsRecordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApolloModule {

    val serverUrl: String = BuildConfig.P360_URL + "gapi"
    val webSocketServerUrl: String = BuildConfig.P360_WSS_URL + "subscriptions"
    val sqlNormalizedCacheFactory = SqlNormalizedCacheFactory("apollo_.db")

    @Provides
    @Singleton
    @Named("ApolloClientOkHttpClient")
    fun provideOkHttpClient(vehicleDetailsRecordRepository: VehicleDetailsRecordRepository): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(vehicleDetailsRecordRepository))
            .retryOnConnectionFailure(true).build()
    }

    private class AuthorizationInterceptor(val vehicleDetailsRecordRepository: VehicleDetailsRecordRepository) :
        Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().codpToken}")
                .build()

            return chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideConnectionPayload(vehicleDetailsRecordRepository: VehicleDetailsRecordRepository): SubscriptionWsProtocol.Factory {
        return SubscriptionWsProtocol.Factory(connectionPayload = {
            mapOf("Authorization" to "Bearer ${vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().codpToken}")
        })
    }

    @Provides
    @Singleton
    fun provideApolloClient(factory: SubscriptionWsProtocol.Factory, @Named("ApolloClientOkHttpClient") okHttpClient: OkHttpClient): ApolloClient {
        return ApolloClient.Builder().serverUrl(serverUrl).fetchPolicy(FetchPolicy.NetworkFirst)
            .normalizedCache(sqlNormalizedCacheFactory).webSocketServerUrl(webSocketServerUrl)
            .wsProtocol(factory).okHttpClient(okHttpClient).build()
    }
}