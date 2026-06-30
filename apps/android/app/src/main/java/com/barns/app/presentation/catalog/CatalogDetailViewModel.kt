package com.barns.app.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.CatalogItem
import com.barns.app.domain.usecase.catalog.GetCareGuidesUseCase
import com.barns.app.domain.usecase.catalog.GetCatalogItemDetailUseCase
import com.barns.app.domain.usecase.catalog.GetGreeneryInfoUseCase
import com.barns.app.presentation.myitems.RegisterGreeneryPrefill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Loads a catalog item detail and resolves its linked official content
 * (GreeneryInfo, CareGuides) through use cases. All linking degrades safely:
 * a missing/unknown greeneryInfoId or care guide id yields no crash and an
 * empty/none section.
 */
class CatalogDetailViewModel(
    private val itemId: String,
    private val getCatalogItemDetailUseCase: GetCatalogItemDetailUseCase,
    private val getGreeneryInfoUseCase: GetGreeneryInfoUseCase,
    private val getCareGuidesUseCase: GetCareGuidesUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val content: CatalogDetailContent) : State
        object NotFound : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getCatalogItemDetailUseCase.execute(itemId) }
                .onSuccess { item ->
                    _state.value = if (item == null) State.NotFound else State.Loaded(resolve(item))
                }
                .onFailure {
                    _state.value = State.Failed("Unable to load this catalog item. Please try again.")
                }
        }
    }

    private suspend fun resolve(item: CatalogItem): CatalogDetailContent {
        var overview: String? = null
        var lightPreference: String? = null
        var wateringOverview: String? = null
        item.greeneryInfoId?.let { infoId ->
            runCatching { getGreeneryInfoUseCase.execute(infoId) }.getOrNull()?.let { info ->
                overview = info.overview
                lightPreference = info.lightPreference
                wateringOverview = info.wateringOverview
            }
        }

        val guides = if (item.careGuideIds.isEmpty()) {
            emptyList()
        } else {
            runCatching { getCareGuidesUseCase.execute(item.careGuideIds) }.getOrDefault(emptyList())
        }
        val summaries = guides.map {
            CatalogDetailContent.CareGuideSummary(id = it.id, title = it.title, summary = it.summary)
        }

        return CatalogDetailContent(
            name = item.name,
            kindLabel = CatalogKind.label(item.kind),
            summary = item.summary,
            overview = overview,
            lightPreference = lightPreference,
            wateringOverview = wateringOverview,
            careGuides = summaries,
            registerPrefill = RegisterGreeneryPrefill.from(item),
            imageReference = item.imageUrl,
        )
    }
}
