package com.tvsm.iqubeindia.networking

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.SubscriptionWsProtocol
import com.tvsm.iqubeindia.domain.repository.VehicleDetailsRecordRepository
import com.tvsm.iqubeindia.presentation.ui.utils.WatchAppConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

private var instance: ApolloClient? = null

fun apolloClient(vehicleDetailsRecordRepository: VehicleDetailsRecordRepository): ApolloClient {
    if (instance != null) return instance!!

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(vehicleDetailsRecordRepository))
        .retryOnConnectionFailure(true)
        .build()

    instance = ApolloClient.Builder()
        //TODO change the URLs to production when received
        .serverUrl(WatchAppConfig.P360_UAT_URL + "gapi")
        .wsProtocol(
            SubscriptionWsProtocol.Factory(
                connectionPayload = {
                    mapOf("Authorization" to vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().codpToken)
                },
            ),
        )
        .okHttpClient(okHttpClient)
        .build()

    return instance!!
}

fun apolloSubClient(vehicleDetailsRecordRepository: VehicleDetailsRecordRepository): ApolloClient {
    if (instance != null) return instance!!

    val logging = HttpLoggingInterceptor()
    logging.level = (HttpLoggingInterceptor.Level.BODY)

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addNetworkInterceptor(AuthorizationInterceptor(vehicleDetailsRecordRepository))
        .retryOnConnectionFailure(true)
        .build()

    instance = ApolloClient.Builder()
        //TODO change the URLs to production when received
        .serverUrl(WatchAppConfig.P360_UAT_URL + "gapi")
        .wsProtocol(
            SubscriptionWsProtocol.Factory(
                connectionPayload = {
                    mapOf("Authorization" to "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm90ZWN0ZWQiOiJleUpsYm1NaU9pSkJNalUyUjBOTklpd2lZV3huSWpvaVpHbHlJaXdpYTJsa0lqb2lObUpFVWxaZlNrZE5jVVpJZFhwUVZrWTVUM1p2YjNKWlRVcG9kMEZIVG5Oa1kwbzBNRzVPVEZwaWR5SjkiLCJpdiI6IlpsUmpGOWppcWhIek5hTGMiLCJjaXBoZXJ0ZXh0IjoieDU4RGE2cHJScGFVczNlTjNqaGJoRVZsN21VTUdYRDF6bjdreVozRm01UHctN280UWdkamVUVGY0eTByY3V6VEEybHRaRktQZDdQMFZIYWZWR2JzaVMwbVVDRC12WjR6dTdpdnFiUXJjaHV3Um9iOUpnbUJLdEMwdWEzd3lmSFJQeGxNcGhGbXdPckJ1NjBneDZFVzZadEhwejE0R2twUDFmU2E0dEdhTktDNVN0QUlDM1cwLVF5UjNJaDR6dndMOEtuRjE5aFVDSEtjQ3NGWFl2SHFSdkNtNGtVWndkUFdfREdxeDdTU2NlZG1Qb2psanlydU5YVzd3WEVIdVhoS0VSQU1uNDVwMXdDZExoT1NEc242Y0c3d0pkeXpVelpkekVXZmpudVZqemFCNjllY2k2UzctQnlsNXltWU1fUDBCalVmZ0FxbCIsInRhZyI6IjhyM3E3UmRLWHU3M01XOFdabjhTenciLCJpYXQiOjE3MTUzNTUzMjB9.-8KdfM--Cjas0WhLVzej7fU4_uYL4LriU77qJcjuCtg")
                },
            ),
        )
        .okHttpClient(okHttpClient)
        .build()

    return instance!!
}

fun apolloClientU577(vehicleDetailsRecordRepository: VehicleDetailsRecordRepository): ApolloClient {
    if (instance != null) return instance!!

// Create an HttpLoggingInterceptor
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthorizationInterceptor(vehicleDetailsRecordRepository))
        .retryOnConnectionFailure(true)
        .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)  // Increase connection timeout
        .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)     // Increase read timeout
        .writeTimeout(5, java.util.concurrent.TimeUnit.SECONDS)    // Increase write timeout
        .build()


    instance = ApolloClient.Builder()
        //TODO change the URLs to production when received
        .serverUrl(WatchAppConfig.P360_UAT_URL + "gapi")
        .wsProtocol(
            SubscriptionWsProtocol.Factory(
                connectionPayload = {
                    mapOf("Authorization" to "Bearer " + vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().codpToken)
                },
            ),
        )
        .okHttpClient(okHttpClient)
        .build()

    return instance!!
}

private class AuthorizationInterceptor(val vehicleDetailsRecordRepository: VehicleDetailsRecordRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "Bearer " + vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().codpToken
        val request = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()

        return chain.proceed(request)
    }
}

