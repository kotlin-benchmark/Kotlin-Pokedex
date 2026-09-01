package dev.marcosfarias.pokedex.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import dev.marcosfarias.pokedex.database.dao.PokemonDAO
import dev.marcosfarias.pokedex.model.Pokemon

/**
 * Looks up pokemon in the local pokedex by (partial) name.
 */
class PokemonSearchRepository(private val pokemonDAO: PokemonDAO) {

    fun searchByName(name: String): List<Pokemon> {
        val sql = "SELECT * FROM pokemon WHERE name LIKE '%" + name + "%'"
        //CWE-89
        //SINK
        return pokemonDAO.searchByRawQuery(SimpleSQLiteQuery(sql))
    }
}
