package com.appsie.wg.requests

import com.google.gson.JsonArray
import com.shree.retrofitlogger.APIClient
import com.shree.retrofitlogger.APIClient.*

import org.json.JSONObject

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Codename07 on 02/09/17.
 */

interface APIRequests {

    @GET("prod/MA_getImagesByCategoryName?")
    fun userCityListing(@Query("category") category: String): Call<JsonArray>

    @GET("prod/MA_getImagesByCategoryName?")
    fun getImagesListing(@Query("category") category: String): Call<ArrayList<String>>


}
