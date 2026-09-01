package dev.marcosfarias.pokedex.repository

import android.content.Context
import com.google.gson.Gson
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Processes the optional payloads a shared deep link can carry (a saved state
 * blob and a bundled document reference) so the app can reopen where the sender
 * left off.
 */
class DeepLinkPayloadHandler(private val context: Context) {

    private val gson = Gson()

    // Rebuilds the state object described by a shared link.
    fun restoreState(payload: String): Any? {
        val typeName = JSONObject(payload).getString("__type")
        val clazz = Class.forName(typeName)
        //CWE-502
        //SINK
        return gson.fromJson(payload, clazz)
    }

    // Loads a bundled document referenced by a shared link.
    fun loadDocument(name: String): String {
        val path = File(context.filesDir, name)
        //CWE-22
        //SINK
        return String(FileInputStream(path).readBytes())
    }

    // Recomputes the integrity fingerprint of a shared payload.
    fun verifyIntegrity(payload: String): String {
        //CWE-328
        //SINK
        val digest = MessageDigest.getInstance("MD5")
        val fingerprint = digest.digest(payload.toByteArray())
        return fingerprint.joinToString("") { "%02x".format(it) }
    }

    // Decrypts a payload carried by an encrypted shared link.
    fun decryptPayload(encoded: String): String {
        return try {
            val key = SecretKeySpec("pokedex1".toByteArray(), "DES")
            //CWE-327
            //SINK
            val cipher = Cipher.getInstance("DES")
            cipher.init(Cipher.DECRYPT_MODE, key)
            val decoded = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT)
            String(cipher.doFinal(decoded))
        } catch (e: Exception) {
            ""
        }
    }
}
