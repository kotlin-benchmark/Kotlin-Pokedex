package dev.marcosfarias.pokedex.repository

import android.content.Context
import java.io.ByteArrayInputStream
import java.io.File
import java.io.ObjectInputStream
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Processes the optional payloads a shared deep link can carry (a saved state
 * blob and a bundled document reference) so the app can reopen where the sender
 * left off.
 */
class DeepLinkPayloadHandler(private val context: Context) {

    // Rebuilds the state object described by a shared link.
    fun restoreState(payload: String): Any? {
        val bytes = android.util.Base64.decode(payload, android.util.Base64.DEFAULT)
        if (!isTrustedPayload(bytes)) return null
        val stream = ObjectInputStream(ByteArrayInputStream(bytes))
        //CWE-502
        //SINK
        return stream.readObject()
    }

    /**
     * Verifies the payload is a serialized object before restoring it.
     */
    private fun isTrustedPayload(bytes: ByteArray): Boolean {
        // Java serialization stream header magic (0xACED).
        return bytes.size >= 2 &&
            bytes[0] == 0xAC.toByte() &&
            bytes[1] == 0xED.toByte()
    }

    // Loads a bundled document referenced by a shared link.
    fun loadDocument(name: String): String {
        val safeName = sanitizePath(name)
        val path = File(context.filesDir, safeName)
        //CWE-22
        //SINK
        return String(path.readBytes())
    }

    /**
     * Removes parent-directory sequences before opening the file.
     */
    private fun sanitizePath(name: String): String {
        return name.replace("../", "")
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
