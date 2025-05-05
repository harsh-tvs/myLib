package com.tvsm.iqubeindia.domain.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tvsm.connect.R
import com.tvsm.iqubeindia.presentation.ui.data.CodpAccessCredentials
import com.tvsm.iqubeindia.presentation.ui.data.RemoteOperationStatus
import com.tvsm.iqubeindia.tvsElectric.GetTvsmLatestDeviceDataQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmAggregateDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLatestDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLifetimeAggregateDeviceDataWatchQuery
import java.lang.reflect.Type

class VehicleDetailsRecordRepository(private val mContext: Context, val mPreferences: SharedPreferences) {

    fun isCacheEmpty() : Boolean{
        return mPreferences.all.isEmpty()
    }

    fun getContext(): Context {
        return mContext
    }

    fun putVehicleLatestDataValuePref(mResponse: GetTvsmLatestDeviceDataQuery.Response) {
        with(mPreferences.edit()) {
            val gson = Gson()
            val jsonList: String? = gson.toJson(mResponse)
            putString(mContext.getString(R.string.last_sync_vehicle_details), jsonList.toString())
            apply()
        }
    }

    fun putVehicleLatestDataValuePrefU577(mResponse1: GetTvsmLatestDeviceDataWatchQuery.Data1?,
                                          mResponse2: GetTvsmAggregateDeviceDataWatchQuery.Data1?,
                                          mResponse3: GetTvsmLifetimeAggregateDeviceDataWatchQuery.Data1?) {
        with(mPreferences.edit()) {
            val gson = Gson()
            val jsonList1: String? = gson.toJson(mResponse1)
            val jsonList2: String? = gson.toJson(mResponse2)
            val jsonList3: String? = gson.toJson(mResponse3)
            putString(mContext.getString(R.string.last_sync_vehicle_details), jsonList1.toString())
            putString(mContext.getString(R.string.last_sync_vehicle_details), jsonList2.toString())
            putString(mContext.getString(R.string.last_sync_vehicle_details), jsonList3.toString())
            apply()
        }
    }

    fun getVehicleLatestDataValuePref(): GetTvsmLatestDeviceDataQuery.Response? {
        val gson = Gson()
        val jsonVehicleData: String? = mPreferences.getString(mContext.getString(R.string.last_sync_vehicle_details), null)
        val type: Type = object : TypeToken<GetTvsmLatestDeviceDataQuery.Response?>() {}.type
        jsonVehicleData?.let {
            return gson.fromJson(jsonVehicleData, type)
        }
        return null
    }

    fun getVehicleLatestDataWatchValuePref(): GetTvsmLatestDeviceDataWatchQuery.Data1? {
        val gson = Gson()
        val jsonVehicleData: String? = mPreferences.getString(mContext.getString(R.string.last_sync_vehicle_details), null)
        val type: Type = object : TypeToken<GetTvsmLatestDeviceDataWatchQuery.Data1?>() {}.type
        return jsonVehicleData?.let { gson.fromJson(jsonVehicleData, type) }
    }

    fun getVehicleAggregateDataValuePref(): GetTvsmAggregateDeviceDataWatchQuery.Data1? {
        val gson = Gson()
        val jsonVehicleData: String? = mPreferences.getString(mContext.getString(R.string.last_sync_vehicle_details), null)
        val type: Type = object : TypeToken<GetTvsmAggregateDeviceDataWatchQuery.Data1?>() {}.type
        return jsonVehicleData?.let { gson.fromJson(jsonVehicleData, type) }
    }

    fun getVehicleLifetimeAggregateDataValuePref(): GetTvsmLifetimeAggregateDeviceDataWatchQuery.Data1? {
        val gson = Gson()
        val jsonVehicleData: String? = mPreferences.getString(mContext.getString(R.string.last_sync_vehicle_details), null)
        val type: Type = object : TypeToken<GetTvsmLifetimeAggregateDeviceDataWatchQuery.Data1?>() {}.type
        return jsonVehicleData?.let { gson.fromJson(jsonVehicleData, type) }
    }


    fun putCodpAccessCredsPref(mCodpCredentials: CodpAccessCredentials) {
        Log.d("VehicleDetailsRecordRepository", "received codp creds: $mCodpCredentials")
        with(mPreferences.edit()) {
            val gson = Gson()
            val codpCredentials = gson.toJson(mCodpCredentials)
            putString(mContext.getString(R.string.codp_access_credentials), codpCredentials.toString())
            apply()
        }
    }

    fun getCodpAccessCredentialsPref(): CodpAccessCredentials {
        val gson = Gson()
        val jsonCodpCredentials = mPreferences.getString(mContext.getString(R.string.codp_access_credentials), null)
        val type: Type = object : TypeToken<CodpAccessCredentials>() {}.type
        jsonCodpCredentials?.let {
            return gson.fromJson(it, type)
        }
        return CodpAccessCredentials()
    }

    fun putRemoteOperationStatusPref(mRemoteOperationStatus: RemoteOperationStatus){
        Log.d("VehicleDetailsRecordRepository", "RemoteOperationStatus: $mRemoteOperationStatus")
        with(mPreferences.edit()){
            val gson = Gson()
            val remoteOperationStatus = gson.toJson(mRemoteOperationStatus)
            putString(mContext.getString(R.string.remote_operation_status), remoteOperationStatus.toString())
            apply()
        }
    }

    fun getRemoteOperationStatusPref(): RemoteOperationStatus {
        val gson = Gson()
        val jsonRemoteOperationStatus = mPreferences.getString(mContext.getString(R.string.remote_operation_status), null)
        val type: Type = object : TypeToken<RemoteOperationStatus>() {}.type
        jsonRemoteOperationStatus?.let {
            return gson.fromJson(it, type)
        }
        return RemoteOperationStatus()
    }

}
