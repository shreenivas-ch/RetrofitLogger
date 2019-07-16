# RetrofitLogger
Easily log retrofit requests and responses

# Installation
Add jitpack.io to your root gradle file (project level) :

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
     }

Add the dependency in your app build.gradle

    dependencies {
       implementation 'com.github.shreenivas-ch:RetrofitLogger:1.0'
    }

# How To Use

Initialise library in you Application Class

    class MainApplication : Application() {

        override fun onCreate() {
            super.onCreate()
            RetrofitLogger.init(this)
        }
    }
    
 Add HttpLoggingInterceptor to Retrofit Client
 
    httpClient.addNetworkInterceptor(RetrofitLogger().getHttpLoggingInterceptor())
 
 Example: 
 
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
            
   # Show Dialogue to User to enable Logger.
   
   User below code in your activity.
   
       RetrofitLogger.getInstance().showDebugOptions()
