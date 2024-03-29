package com.retrofitlogger.demo

import com.shree.retrofitlogger.RetrofitLogger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Codename07 on 02/09/17.
 */

object APIClient {

    var WEBSERVICE_URL1 = "https://p7pqaubg77.execute-api.ap-south-1.amazonaws.com/"
    //var WEBSERVICE_URL1 = "YOUR BASE URL"
    var retrofit: Retrofit? = null

    val client1: Retrofit
        get() {
            if (retrofit == null) {

                val httpClient = OkHttpClient.Builder()
                httpClient.addNetworkInterceptor(RetrofitLogger().getHttpLoggingInterceptor())
                httpClient.addInterceptor { chain ->
                    val original = chain.request()

                    val request = original.newBuilder()
                        .header("os", "android")
                        .method(original.method(), original.body())
                        .build()

                    chain.proceed(request)
                }
                retrofit = Retrofit.Builder()
                    .baseUrl(WEBSERVICE_URL1)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
}
