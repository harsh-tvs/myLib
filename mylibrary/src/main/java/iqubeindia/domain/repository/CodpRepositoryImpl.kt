package com.tvsm.iqubeindia.domain.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import okhttp3.Response
import com.apollographql.apollo3.api.Error
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.exception.ApolloException
import com.tvsm.iqubeindia.networking.apolloClient
import com.tvsm.iqubeindia.networking.apolloClientU577
import com.tvsm.iqubeindia.tvsElectric.GetTvsmLatestDeviceDataQuery
import com.tvsm.iqubeindia.tvsElectric.TvsmDeviceLiveTrackingSubscription
import com.tvsm.iqubeindia.tvsIce.GetTvsmLatestDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmAggregateDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLifetimeAggregateDeviceDataWatchQuery

class CodpRepositoryImpl @Inject constructor(
    private val vehicleDetailsRecordRepository: VehicleDetailsRecordRepository): CodpRepository {

    val TAG = CodpRepositoryImpl::class.java.simpleName
    override suspend fun queryGetTvsmLatestDeviceData(): Pair<GetTvsmLatestDeviceDataQuery.GetTvsmLatestDeviceData?, String?> {
        try {
            Log.d(
                TAG,
                "GetTvsmLatestDeviceDataQuery VIN: " + vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin
            )
            val response = apolloClient(vehicleDetailsRecordRepository).query(GetTvsmLatestDeviceDataQuery(vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin))
                .execute()
            if (response.hasErrors()) {
                // Handle errors
                val errors = response.errors ?: emptyList()
                val errorMessage = buildErrorMessage(errors)
                Log.d(TAG, "Error message is $errorMessage")
                return Pair(null, errorMessage)
            }
            Log.d(
                TAG,
                "GetTvsmLatestDeviceDataQuery response: " + "${response.data?.getTvsmLatestDeviceData?.statusMessage}"
            )
            return Pair(response.data?.getTvsmLatestDeviceData, null)
        }
     catch (e: ApolloHttpException)
    {
        val error = e.message
        val errorCode = e.statusCode
        if (errorCode == 400) {
            Log.e(TAG, "HTTP 400 Bad Request: $error")
            // Handle the HTTP 400 error accordingly
            return Pair(null, "HTTP 400 Bad Request")
        } else {
            Log.e(TAG, "HTTP error code: $errorCode, Message: $error")
            // Handle other HTTP errors if needed
            return Pair(null, "HTTP error code: $errorCode, Message: $error")
        }
    } catch (e: ApolloNetworkException)
    {
        Log.e(TAG, "Network error: ${e.message}")
        // Handle network-related errors
        return Pair(null, "Network error: ${e.message}")
    } catch (e: ApolloException)
    {
        Log.e(TAG, "Error executing GraphQL query: ${e.message}")

        // Handle other Apollo-related exceptions
        return Pair(null, "Error executing GraphQL query: ${e.message}")
    } catch (e: Exception)
    {
        Log.e(TAG, "Unknown error: ${e.message}")
        // Handle other exceptions
        return Pair(null, "Unknown error: ${e.message}")
    }
}


    override suspend fun queryGetTvsmLatestDeviceDataWatch(
    ): Pair<GetTvsmLatestDeviceDataWatchQuery.GetTvsmLatestDeviceDataWatch?, String?> {
        try {
            Log.d(TAG, "Fetching ICE Non-Geared Aggregated Data")

            // Execute the GraphQL query
            val response = apolloClientU577(vehicleDetailsRecordRepository)
                .query(GetTvsmLatestDeviceDataWatchQuery(
                    vin = vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin
                ))
                .execute()

            // Log the raw response data
            Log.d(TAG, "GraphQL Response: ${response.toString()}")

            if (response.hasErrors()) {
                // Handle errors
                val errors = response.errors ?: emptyList()
                val errorMessage = buildErrorMessage(errors)
                Log.e(TAG, "GraphQL Errors: $errorMessage")
                return Pair(null, errorMessage)
            }

            val statusMessage = response.data?.getTvsmLatestDeviceDataWatch?.status
            Log.d(TAG, "Query successful: $statusMessage")

            return Pair(response.data?.getTvsmLatestDeviceDataWatch, null)

        } catch (e: ApolloHttpException) {
            val errorCode = e.statusCode
            val error = e.message
            if (errorCode == 400) {
                Log.e(TAG, "HTTP 400 Bad Request: $error")
                return Pair(null, "HTTP 400 Bad Request")
            } else {
                Log.e(TAG, "HTTP error code: $errorCode, Message: $error")
                return Pair(null, "HTTP error code: $errorCode, Message: $error")
            }
        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Network error: ${e.message}")
            return Pair(null, "Network error: ${e.message}")
        } catch (e: ApolloException) {
            Log.e(TAG, "Apollo error: ${e.message}")
            return Pair(null, "Error executing GraphQL query: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error: ${e.message}")
            return Pair(null, "Unknown error: ${e.message}")
        }
    }

    override suspend fun queryGetTvsmAggregateDeviceDataWatch(
    ): Pair<GetTvsmAggregateDeviceDataWatchQuery.GetTvsmAggregateDeviceDataWatch?, String?> {
        try {
            Log.d(TAG, "Fetching ICE Non-Geared Aggregated Data")

            // Execute the GraphQL query
            val response = apolloClientU577(vehicleDetailsRecordRepository)
                .query(GetTvsmAggregateDeviceDataWatchQuery(
                    vin = vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin
                ))
                .execute()

            // Log the raw response data
            Log.d(TAG, "GraphQL Response: ${response.toString()}")

            if (response.hasErrors()) {
                // Handle errors
                val errors = response.errors ?: emptyList()
                val errorMessage = buildErrorMessage(errors)
                Log.e(TAG, "GraphQL Errors: $errorMessage")
                return Pair(null, errorMessage)
            }

            val statusMessage = response.data?.getTvsmAggregateDeviceDataWatch?.status
            Log.d(TAG, "Query successful: $statusMessage")

            return Pair(response.data?.getTvsmAggregateDeviceDataWatch, null)

        } catch (e: ApolloHttpException) {
            val errorCode = e.statusCode
            val error = e.message
            if (errorCode == 400) {
                Log.e(TAG, "HTTP 400 Bad Request: $error")
                return Pair(null, "HTTP 400 Bad Request")
            } else {
                Log.e(TAG, "HTTP error code: $errorCode, Message: $error")
                return Pair(null, "HTTP error code: $errorCode, Message: $error")
            }
        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Network error: ${e.message}")
            return Pair(null, "Network error: ${e.message}")
        } catch (e: ApolloException) {
            Log.e(TAG, "Apollo error: ${e.message}")
            return Pair(null, "Error executing GraphQL query: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error: ${e.message}")
            return Pair(null, "Unknown error: ${e.message}")
        }
    }


    override suspend fun queryGetTvsmLifeTimeAggregateDeviceDataWatch(
    ): Pair<GetTvsmLifetimeAggregateDeviceDataWatchQuery.GetTvsmLifetimeAggregateDeviceDataWatch?, String?> {
        try {
            Log.d(TAG, "Fetching ICE Non-Geared Aggregated Data")

            // Execute the GraphQL query
            val response = apolloClientU577(vehicleDetailsRecordRepository)
                .query(GetTvsmLifetimeAggregateDeviceDataWatchQuery(
                    vin = vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin
                ))
                .execute()

            // Log the raw response data
            Log.d(TAG, "GraphQL Response: ${response.toString()}")

            if (response.hasErrors()) {
                // Handle errors
                val errors = response.errors ?: emptyList()
                val errorMessage = buildErrorMessage(errors)
                Log.e(TAG, "GraphQL Errors: $errorMessage")
                return Pair(null, errorMessage)
            }

            val statusMessage = response.data?.getTvsmLifetimeAggregateDeviceDataWatch?.status
            Log.d(TAG, "Query successful: $statusMessage")

            return Pair(response.data?.getTvsmLifetimeAggregateDeviceDataWatch, null)

        } catch (e: ApolloHttpException) {
            val errorCode = e.statusCode
            val error = e.message
            if (errorCode == 400) {
                Log.e(TAG, "HTTP 400 Bad Request: $error")
                return Pair(null, "HTTP 400 Bad Request")
            } else {
                Log.e(TAG, "HTTP error code: $errorCode, Message: $error")
                return Pair(null, "HTTP error code: $errorCode, Message: $error")
            }
        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Network error: ${e.message}")
            return Pair(null, "Network error: ${e.message}")
        } catch (e: ApolloException) {
            Log.e(TAG, "Apollo error: ${e.message}")
            return Pair(null, "Error executing GraphQL query: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error: ${e.message}")
            return Pair(null, "Unknown error: ${e.message}")
        }
    }


    private fun buildErrorMessage(errors: List<Error>): String {
        val errorMessageBuilder = StringBuilder()
        errors.forEachIndexed { index, error ->
            errorMessageBuilder.append("Error ${index + 1}: ${error.message}\n")
        }
        Log.d(TAG,"Error message by builder is ${errorMessageBuilder}")
        return errorMessageBuilder.toString()
    }

    override suspend fun subscriptionTvsmDeviceLiveTracking(): List<TvsmDeviceLiveTrackingSubscription.TvsmDeviceLiveTracking?>? {
        Log.d(TAG, "TvsmDeviceLiveTrackingSubscription VIN: " + vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin)
        try {
            apolloClient(vehicleDetailsRecordRepository)
                .subscription(TvsmDeviceLiveTrackingSubscription(vehicleDetailsRecordRepository
                    .getCodpAccessCredentialsPref().vin)).toFlow().onEach {
                    Log.e(TAG, "TvsmDeviceLiveTrackingSubscription response->" + it.data?.tvsmDeviceLiveTracking)
                    return@onEach
                }.collect()
        } catch (e: Exception) {
            Log.e(TAG, "TvsmDeviceLiveTrackingSubscription ERROR: ${e.message}")
            e.printStackTrace()
        } finally {
        }
        return null
    }
}