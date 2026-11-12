package com.nutrifacts.app.data.local

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentSearchDaoTest : DatabaseTest() {

    @Test
    fun insertSearch_whenCalled_searchCanBeRetrieved() = runTest {
        val search = RecentSearchEntity(
            query = "apple",
            searchedAt = 100L
        )

        recentSearchDao.insertSearch(search)

        assertEquals(listOf(search), recentSearchDao.getRecentSearches())
    }

    @Test
    fun getRecentSearches_returnsSearchesInDescendingOrder() = runTest {
        val searches = listOf(
            RecentSearchEntity("apple", 100L),
            RecentSearchEntity("banana", 300L),
            RecentSearchEntity("orange", 200L)
        )

        searches.forEach { recentSearchDao.insertSearch(it) }

        val result = recentSearchDao.getRecentSearches()

        assertEquals(
            listOf("banana", "orange", "apple"),
            result.map { it.query }
        )
    }

    @Test
    fun insertSearch_whenQueryAlreadyExists_replacesPreviousSearch() = runTest {
        recentSearchDao.insertSearch(
            RecentSearchEntity("apple", 100L)
        )

        recentSearchDao.insertSearch(
            RecentSearchEntity("apple", 200L)
        )

        val result = recentSearchDao.getRecentSearches()

        assertEquals(1, result.size)
        assertEquals(200L, result.first().searchedAt)
    }

    @Test
    fun deleteOlderSearches_keepsOnlyFiveMostRecentSearches() = runTest {
        repeat(7) { index ->
            recentSearchDao.insertSearch(
                RecentSearchEntity(
                    query = "search$index",
                    searchedAt = index.toLong()
                )
            )
        }

        recentSearchDao.deleteOlderSearches()

        val result = recentSearchDao.getRecentSearches()

        assertEquals(5, result.size)
        assertEquals(
            listOf("search6", "search5", "search4", "search3", "search2"),
            result.map { it.query }
        )
    }
}
