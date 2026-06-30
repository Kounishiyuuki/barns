package com.barns.app.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.barns.app.R

/**
 * Resolves and renders generated **local mock** demo images bundled with the
 * app as drawables. These are brand-neutral mock/demo assets only — never real
 * customer photos, production company photos, or anything ordered/submitted.
 *
 * References use a `mock://<category>/<asset-name>` scheme stored in the
 * optional `imageUrl` field of existing models. No network loading ever
 * happens (uses [painterResource] only). Unknown or null references resolve to
 * no drawable so image-null states stay safe.
 */
fun localMockDrawableRes(reference: String?): Int? {
    if (reference == null || !reference.startsWith("mock://")) return null
    return when (reference.substringAfterLast('/')) {
        "catalog-office-vertical-green-wall-01" -> R.drawable.mock_catalog_office_vertical_green_wall_01
        "catalog-reception-greenery-wall-01" -> R.drawable.mock_catalog_reception_greenery_wall_01
        "catalog-compact-framed-moss-panel-01" -> R.drawable.mock_catalog_compact_framed_moss_panel_01
        "my-greenery-entryway-green-wall-01" -> R.drawable.mock_my_greenery_entryway_green_wall_01
        "my-greenery-reception-foliage-planter-01" -> R.drawable.mock_my_greenery_reception_foliage_planter_01
        else -> null
    }
}

/**
 * A small, reusable composable for local mock demo imagery in Catalog and My
 * Greenery surfaces. Decorative by design (surrounding text already names the
 * item), so [contentDescription] is null. Sizing is controlled by the caller.
 *
 * @param showPlaceholder when true, a neutral box keeps list-row layout stable
 *   for items without a mapped mock image. Detail heroes pass false and are
 *   only shown when a drawable actually resolves.
 */
@Composable
fun LocalMockImage(
    reference: String?,
    modifier: Modifier = Modifier,
    showPlaceholder: Boolean = true,
) {
    val resId = localMockDrawableRes(reference)
    if (resId != null) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else if (showPlaceholder) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    }
}
