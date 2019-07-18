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
       implementation 'com.github.shreenivas-ch:RetrofitLogger:1.1'
    }

# How To Use

Extend you Activity with RetrofitLoggerActivity

    class MainActivity : RetrofitLoggerActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
        }
    }
    
If you have multple activities in your project then Create BaseActivity that extends RetrofitLoggerActivity, then extend all your activities with BaseActivity
    
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
            
