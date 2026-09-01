package dev.marcosfarias.pokedex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.marcosfarias.pokedex.database.dao.PokemonDAO
import dev.marcosfarias.pokedex.repository.DeepLinkPayloadHandler
import dev.marcosfarias.pokedex.repository.PokemonSearchRepository
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val pokemonDAO: PokemonDAO by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleDeepLink()
    }

    // A shared link (pokedex://app?...) can jump straight to a search, restore a
    // shared state, or open a bundled document.
    private fun handleDeepLink() {
        val data = intent?.data ?: return

        //CWE-89
        //SOURCE
        val query = data.getQueryParameter("q")
        if (query != null) {
            thread { PokemonSearchRepository(pokemonDAO).searchByName(query) }
        }

        //CWE-502
        //SOURCE
        val state = data.getQueryParameter("state")
        if (state != null) {
            DeepLinkPayloadHandler(this).restoreState(state)
        }

        //CWE-22
        //SOURCE
        val document = data.getQueryParameter("doc")
        if (document != null) {
            DeepLinkPayloadHandler(this).loadDocument(document)
        }

        //CWE-328
        //SOURCE
        val signature = data.getQueryParameter("sig")
        if (signature != null) {
            DeepLinkPayloadHandler(this).verifyIntegrity(signature)
        }

        //CWE-327
        //SOURCE
        val secure = data.getQueryParameter("enc")
        if (secure != null) {
            DeepLinkPayloadHandler(this).decryptPayload(secure)
        }
    }
}
