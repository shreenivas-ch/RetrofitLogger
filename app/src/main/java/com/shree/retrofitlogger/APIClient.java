package com.shree.retrofitlogger;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Created by Codename07 on 02/09/17.
 */

public class APIClient {

    public static String WEBSERVICE_URL1 = "https://p7pqaubg77.execute-api.ap-south-1.amazonaws.com/";
    public static Retrofit retrofit = null;

    public static Retrofit getClient1() {
        if (retrofit == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addNetworkInterceptor(new RetrofitLogger().getHttpLoggingInterceptor());
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("os", "android")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
            retrofit = new Retrofit.Builder()
                    .baseUrl(WEBSERVICE_URL1)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
