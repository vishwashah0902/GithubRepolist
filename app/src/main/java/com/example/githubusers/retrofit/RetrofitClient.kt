package com.example.githubusers.retrofit


import com.example.githubusers.BuildConfig
import com.example.githubusers.network.NetworkConnectionInterceptor
import com.example.githubusers.network.NetworkService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitClient(connectionInterceptor: NetworkConnectionInterceptor) {

    private val MainServer = "https://api.github.com/"

    val retrofitClient: Retrofit.Builder by lazy {

        val levelType: Level
        if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
            levelType = Level.BODY else levelType = Level.NONE

        val logging = HttpLoggingInterceptor()
        logging.setLevel(levelType)


        val okhttpClient = OkHttpClient.Builder()
        okhttpClient.addInterceptor(logging)
        okhttpClient.addNetworkInterceptor(connectionInterceptor)
            .connectTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)

        val gson = GsonBuilder().serializeNulls().create()

        Retrofit.Builder()
            .baseUrl(MainServer)
            .client(okhttpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))

    }

    val apiInterface: NetworkService by lazy {
        retrofitClient
            .build()
            .create(NetworkService::class.java)
    }
}