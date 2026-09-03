package dev.marcosfarias.pokedex.di

import dev.marcosfarias.pokedex.repository.PokemonService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val networkModule = module {
    single<OkHttpClient> {
        val trustManagers = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, SecureRandom())

        OkHttpClient.Builder()
            .addInterceptor { chain ->
                //CWE-798
                //SOURCE
                val apiPassword = "p0k3dex-2019!secret"
                //CWE-798
                //SINK
                val credential = okhttp3.Credentials.basic("pokedex_admin", apiPassword)
                val request = chain.request().newBuilder()
                    .header("Authorization", credential)
                    .build()
                chain.proceed(request)
            }
            .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
            //CWE-295
            //SINK
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/mrcsxsiq/b94dbe9ab67147b642baa9109ce16e44/raw/97811a5df2df7304b5bc4fbb9ee018a0339b8a38/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create<PokemonService>()
    }
}
