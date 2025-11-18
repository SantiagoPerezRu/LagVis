// LagVisApp.kt
package com.example.lagvis_v1

import android.app.Application
import androidx.room.Room
import com.example.lagvis_v1.data.local.LagVisDb
import com.example.lagvis_v1.data.remote.LookupApi
import com.example.lagvis_v1.data.repository.LookupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LagVisApp : Application() {

    lateinit var lookupRepo: LookupRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Retrofit
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL) // <-- AJUSTA
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp)
            .build()
        val api = retrofit.create(LookupApi::class.java)

        // Room
        val db = Room.databaseBuilder(
            this,
            LagVisDb::class.java,
            "lagvis.db"
        ).build()

        // Repo
        lookupRepo = LookupRepository(api, db.lookupDao())

        // Refresh al arrancar (no bloquea el hilo UI)
        CoroutineScope(Dispatchers.IO).launch {
            lookupRepo.refreshOnAppStart(force = false)
        }
    }
}
