package com.example.identity_dissolution_appmovil.viewmodel

import androidx.lifecycle.ViewModel
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class InventoryUiState(
    val items: List<InventoryItem> = GameRepository.inventory,
    val totalSlots: Int = GameRepository.INVENTORY_SLOTS,
    val selected: InventoryItem? = null,
)

class InventoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(InventoryUiState())
    val state: StateFlow<InventoryUiState> = _state.asStateFlow()

    fun select(item: InventoryItem?) {
        // Tocar el mismo objeto otra vez lo deselecciona.
        _state.value = _state.value.copy(
            selected = if (_state.value.selected == item) null else item
        )
    }
}
