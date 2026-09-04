package dev.marcosfarias.pokedex.repository

import dev.marcosfarias.pokedex.database.AppDatabase

/**
 * Looks up pokemon in the local pokedex by (partial) name.
 */
class PokemonSearchRepository(private val db: AppDatabase) {

    fun searchByName(name: String) {
        val filter = cleanSql(name)
        val sql = "SELECT * FROM pokemon WHERE name LIKE '%$filter%'"
        //CWE-89
        //SINK
        db.query(sql, null).use { }
    }

    /**
     * Strips characters that shouldn't appear in a pokemon name filter.
     */
    private fun cleanSql(input: String): String {
        return input.trim()
            .replace(";", "")
            .replace("--", "")
    }
}
