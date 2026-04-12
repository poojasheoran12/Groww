package com.example.groww.data.repository

import app.cash.turbine.test
import com.example.groww.data.local.db.FundDao
import com.example.groww.data.local.entity.FundEntity
import com.example.groww.data.remote.api.MutualFundApi
import com.example.groww.data.remote.dto.*
import com.example.groww.domain.model.FundCategory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FundRepositoryImplTest {

    private lateinit var repository: FundRepositoryImpl
    private val api: MutualFundApi = mockk()
    private val dao: FundDao = mockk()
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FundRepositoryImpl(api, dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `syncExploreFunds fetches all categories and saves to DAO`() = runTest {
        val mockDto = SearchResultDto(1, "Test Fund")
        coEvery { api.searchFunds(any<String>()) } answers { listOf(mockDto) }
        coEvery { dao.upsertFunds(any()) } just Runs
        coEvery { dao.getFundById(any()) } answers { null }
        
        repository.syncExploreFunds()

        val categoryCount = FundCategory.entries.filter { it != FundCategory.ALL && it != FundCategory.SEARCH }.size
        coVerify(atLeast = categoryCount) { api.searchFunds(any<String>()) }
        coVerify { dao.upsertFunds(any()) }
    }

    @Test
    fun `searchFunds first emits local then network results reactively`() = runTest {
        val query = "test"
        val localEntity = FundEntity(1, "Local Fund", "Equity", "50.0")
        val networkDto = SearchResultDto(2, "Network Fund")
        
        coEvery { dao.searchFundsInRoom(any()) } answers { listOf(localEntity) }
        coEvery { api.searchFunds(any<String>()) } answers { listOf(networkDto) }
        coEvery { dao.getFundById(any()) } answers { null }
        coEvery { dao.upsertFunds(any()) } just Runs
        
        val updatedNetworkEntity = FundEntity(2, "Network Fund", FundCategory.SEARCH.displayName, "120.0")
        val networkFlow: Flow<List<FundEntity>> = flowOf(listOf(updatedNetworkEntity))
        every { dao.getFundsByIdsFlow(any()) } answers { networkFlow }
        
        val mockDetailResponse = FundDetailDto(
            meta = FundMetaDto("House", "Type", "Category", 2, "Network Fund"),
            data = listOf(NavPointDto("12-04-2024", "120.0"))
        )
        coEvery { api.getFundDetails(any()) } answers { mockDetailResponse }
        coEvery { dao.updateNavAndTimestamp(any(), any(), any()) } just Runs

        repository.searchFunds(query).test {
            val firstItem = awaitItem()
            assertThat(firstItem.first().name).isEqualTo("Local Fund")
            
            val secondItem = awaitItem()
            assertThat(secondItem.first().name).isEqualTo("Network Fund")
            assertThat(secondItem.first().latestNav).isEqualTo("120.0")
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `lazyFetchNav updates DAO when data is missing`() = runTest {
        val id = 123
        coEvery { dao.getFundById(id) } answers { null }
        
        val mockResponse = FundDetailDto(
            meta = FundMetaDto("House", "Type", "Category", id, "Test"),
            data = listOf(NavPointDto("12-04-2024", "150.75"))
        )
        coEvery { api.getFundDetails(id) } answers { mockResponse }
        coEvery { dao.updateNavAndTimestamp(id, "150.75", any()) } just Runs

        repository.lazyFetchNav(id)

        coVerify { api.getFundDetails(id) }
        coVerify { dao.updateNavAndTimestamp(id, "150.75", any()) }
    }

    @Test
    fun `lazyFetchNav handles network errors gracefully`() = runTest {
        coEvery { dao.getFundById(any()) } answers { null }
        coEvery { api.getFundDetails(any()) } throws Exception("Network Failure")

        repository.lazyFetchNav(123)

        coVerify(exactly = 0) { dao.updateNavAndTimestamp(any(), any(), any()) }
    }
}
