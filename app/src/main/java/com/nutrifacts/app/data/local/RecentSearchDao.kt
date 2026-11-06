package com.nutrifacts.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: RecentSearchEntity)

    @Query(
        "SELECT * FROM recent_searches " +
                "ORDER BY searchedAt DESC LIMIT 5"
    )
    suspend fun getRecentSearches(): List<RecentSearchEntity>

    @Query(
        "DELETE FROM recent_searches WHERE `query` NOT IN " +
                "(SELECT `query` FROM recent_searches " +
                "ORDER BY searchedAt DESC LIMIT 5)"
    )
    suspend fun deleteOlderSearches()
}