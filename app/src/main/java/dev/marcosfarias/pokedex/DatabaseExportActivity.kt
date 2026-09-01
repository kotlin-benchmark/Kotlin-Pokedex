package dev.marcosfarias.pokedex

import android.app.Activity
import android.os.Bundle
import java.io.File

/**
 * Support-tooling screen that copies the local pokedex database into a shared
 * export folder so it can be collected for diagnostics.
 */
class DatabaseExportActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exportDatabase()
        finish()
    }

    private fun exportDatabase() {
        try {
            val dbFile = getDatabasePath(getString(R.string.app_name))
            val target = File(getExternalFilesDir(null), "pokedex-export.db")
            dbFile.copyTo(target, overwrite = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
