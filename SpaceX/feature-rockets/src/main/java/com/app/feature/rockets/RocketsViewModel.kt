package com.app.feature.rockets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.core.data.repository.RocketsRepository
import com.app.core.model.Rocket
import com.app.core.model.sort.RocketSortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RocketsViewModel @Inject constructor(
    private val rocketsRepository: RocketsRepository,
) : ViewModel() {

    val rockets: Flow<PagingData<Rocket>> =
        rocketsRepository.getRocketsStream().cachedIn(viewModelScope)

    private val _uiEffects = MutableSharedFlow<RocketsUiEffect>()
    val uiEffects = _uiEffects.asSharedFlow()

    val sortType: StateFlow<RocketSortType> = rocketsRepository.getRocketSortType().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RocketSortType.NAME_ASC
    )

    private val pendingActions = MutableSharedFlow<RocketsAction>()

    init {
        viewModelScope.launch {
            pendingActions.collect {
                when (it) {
                    is RocketsAction.ChangeSortType -> onSortTypeChanged(it.type)
                    is RocketsAction.ChangeQuery -> onQueryChanged(it.query)
                }
            }
        }
    }

    fun submitAction(action: RocketsAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    private fun onSortTypeChanged(type: RocketSortType) {
        viewModelScope.launch {
            rocketsRepository.saveRocketSortType(type)
            submitUiEffect(RocketsUiEffect.ChangedSortType())
        }
    }

    private fun onQueryChanged(query: String) {
        viewModelScope.launch {
            rocketsRepository.saveSearchQuery(query)
            submitUiEffect(RocketsUiEffect.QueryChanged())
        }
    }

    private fun submitUiEffect(effect: RocketsUiEffect) {
        viewModelScope.launch {
            _uiEffects.emit(effect)
        }
    }
}