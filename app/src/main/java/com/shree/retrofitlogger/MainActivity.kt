package com.shree.retrofitlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.appsie.wg.requests.APIRequests
import retrofit2.Call
import retrofit2.Response

class MainActivity : RetrofitLoggerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getImages("morning")
    }

    fun getImages(category: String) {

        val apiService = APIClient.client1.create(APIRequests::class.java)

        val call = apiService.getImagesListing(category)

        call.enqueue(object : retrofit2.Callback<ArrayList<String>> {
            override fun onResponse(call: Call<ArrayList<String>>, response: Response<ArrayList<String>>) {

                Log.e("Response: ","category:" + response)

            }

            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {

                Log.e("Response: ",t.toString())

            }
        })
    }
}
