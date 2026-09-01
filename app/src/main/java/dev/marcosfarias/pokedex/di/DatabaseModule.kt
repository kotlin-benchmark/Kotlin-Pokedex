package dev.marcosfarias.pokedex.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

// Credentials used to unlock the local (encrypted) pokedex database.
// These should come from secure config, not be compiled in.
private const val DB_USER = "pokedex"
//CWE-798
//SOURCE
private const val DB_PASSPHRASE = "p0k3dex-db-2019!secret"

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            androidApplication().baseContext.getString(R.string.app_name)
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                try {
                    //CWE-798
                    //SINK
                    db.execSQL("PRAGMA key = '$DB_USER:$DB_PASSPHRASE'")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }).build()
    }

    single {
        get<AppDatabase>().pokemonDAO()
    }
}
