package com.example.identity_dissolution_appmovil

import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.viewmodel.EncyclopediaViewModel
import com.example.identity_dissolution_appmovil.viewmodel.InventoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EncyclopediaViewModelTest {

    // viewModelScope usa Dispatchers.Main, que no existe en pruebas JVM: lo reemplazamos.
    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun emptyQuery_returnsAllArticles() = runTest {
        val vm = EncyclopediaViewModel()
        assertEquals(GameRepository.articles.size, vm.results.value.size)
    }

    @Test
    fun query_filtersIgnoringCase() = runTest {
        val vm = EncyclopediaViewModel()
        val job = launch { vm.results.collect {} } // WhileSubscribed necesita un suscriptor
        vm.onQueryChange("REVÓLVER DE")
        advanceUntilIdle()
        val results = vm.results.first()
        assertEquals(1, results.size)
        assertEquals("Revólver de Kessler", results.first().title)
        job.cancel()
    }

    @Test
    fun query_withoutMatches_returnsEmptyList() = runTest {
        val vm = EncyclopediaViewModel()
        val job = launch { vm.results.collect {} }
        vm.onQueryChange("zzz-no-existe")
        advanceUntilIdle()
        assertTrue(vm.results.first().isEmpty())
        job.cancel()
    }

    @Test
    fun article_unknownId_returnsNull() {
        assertNull(EncyclopediaViewModel().article(-1))
    }

    @Test
    fun inventory_selectSameItemTwice_deselects() {
        val vm = InventoryViewModel()
        val item = GameRepository.inventory.first()
        vm.select(item)
        assertEquals(item, vm.state.value.selected)
        vm.select(item)
        assertNull(vm.state.value.selected)
    }

    @Test
    fun inventory_neverExceedsSlots() {
        assertTrue(GameRepository.inventory.size <= GameRepository.INVENTORY_SLOTS)
    }
}
