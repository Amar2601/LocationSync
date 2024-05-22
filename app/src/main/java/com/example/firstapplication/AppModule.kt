package com.example.firstapplication


import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkerParameters
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun getBaseUrl(): String = ApiServices.BASE_URL

    @Provides
    @Singleton
    fun providesRepository( apiServices: ApiServices) : Repository{
        return RepositoryImpl(apiServices)
    }


    @Singleton
    @Provides
    fun getApiService(retrofitClient: Retrofit): ApiServices {
        return retrofitClient.create(ApiServices::class.java)
    }
    @Provides
    fun createRetrofit(baseUrl: String, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    fun createOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(getTimeout(), TimeUnit.SECONDS)
            .readTimeout(getTimeout(), TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        context: Context,
        workerFactory: HiltWorkerFactory,
        workerParameters: WorkerParameters,

    ): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }


    @Provides
    fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }
    @Provides
    fun getTimeout(): Long = 60L
}