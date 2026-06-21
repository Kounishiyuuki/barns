package com.barns.app.data.repository

import com.barns.app.domain.model.GreeneryInfo
import com.barns.app.domain.repository.GreeneryInfoRepository

/**
 * In-memory, official read-only greenery basic information. No persistence,
 * no network. Seed data mirrors shared/mock-data/greenery-info.json.
 */
class MockGreeneryInfoRepository : GreeneryInfoRepository {
    private val infos = listOf(
        GreeneryInfo(
            id = "greenery-info-wall-green",
            name = "壁面グリーン",
            overview = "壁面を植物やグリーン素材で演出する内装向けのグリーン。空間の印象づくりに使われる。",
            difficulty = "ふつう",
            recommendedEnvironment = "屋内の明るい壁面。直射日光と強い空調風が長時間当たらない場所。",
            lightPreference = "明るい日陰",
            wateringOverview = "素材や植栽方式により異なる。乾燥状態と固定状態を定期的に確認する。",
            maintenanceNotes = "ほこりの付着と変色を月1回目安で確認する。",
            imageUrl = null,
        ),
        GreeneryInfo(
            id = "greenery-info-interior-foliage",
            name = "室内観葉グリーン",
            overview = "室内に置く鉢植えの観葉グリーン。受付や共有スペースのアクセントに向く。",
            difficulty = "やさしい",
            recommendedEnvironment = "風通しのよい明るい室内。レース越しの光が入る場所。",
            lightPreference = "明るい日陰からやや明るい場所",
            wateringOverview = "土の表面が乾いたら少量ずつ与える。過湿を避ける。",
            maintenanceNotes = "葉のほこりを拭き取り、受け皿の溜まり水を捨てる。",
            imageUrl = null,
        ),
        GreeneryInfo(
            id = "greenery-info-desk-planter",
            name = "デスクプランター",
            overview = "机上や棚に置く小型のプランターグリーン。省スペースで取り入れやすい。",
            difficulty = "やさしい",
            recommendedEnvironment = "明るい室内。エアコンの風が直接当たらない場所。",
            lightPreference = "明るい日陰",
            wateringOverview = "小型のため乾きやすい。土の状態を見て少量ずつ与える。",
            maintenanceNotes = "置き場所の明るさを定期的に見直し、鉢の向きをときどき変える。",
            imageUrl = null,
        ),
    )

    override suspend fun greeneryInfo(id: String): GreeneryInfo? =
        infos.firstOrNull { it.id == id }
}
