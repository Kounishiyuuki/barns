package com.barns.app.data.repository

import com.barns.app.domain.model.CareGuide
import com.barns.app.domain.repository.CareGuideRepository

/**
 * In-memory, official read-only care guides. No persistence, no network.
 * Seed data mirrors shared/mock-data/care-guides.json; fake and image-null.
 */
class MockCareGuideRepository : CareGuideRepository {
    private val guides = listOf(
        CareGuide(
            id = "guide-wall-green-basic",
            title = "壁面グリーンの基本確認",
            categoryId = "cat-wall-green",
            summary = "固定状態、変色、ほこり、周辺環境を定期的に確認する。",
            steps = listOf(
                "全体を離れた位置から見て浮きや傾きがないか確認する。",
                "葉や素材に変色、破損、強い乾燥がないか確認する。",
                "空調の風や直射日光が長時間当たっていないか確認する。",
            ),
            frequency = "月1回",
            cautions = listOf("強く引っ張らない。", "水拭きが適さない素材では乾拭きを優先する。"),
            imageUrl = null,
        ),
        CareGuide(
            id = "guide-cleaning-basic",
            title = "グリーン装飾の清掃",
            categoryId = "cat-maintenance-supply",
            summary = "柔らかいクロスやブラシで表面のほこりを落とす。",
            steps = listOf(
                "目立つほこりを確認する。",
                "柔らかいクロスで軽く拭く。",
                "細かい部分は柔らかいブラシを使う。",
            ),
            frequency = "月1回から2回",
            cautions = listOf("洗剤の使用は素材により不可。", "濡らしすぎない。"),
            imageUrl = null,
        ),
        CareGuide(
            id = "guide-watering-basic",
            title = "室内グリーンの水やり確認",
            categoryId = "cat-interior-green",
            summary = "土の乾き具合と葉の状態を見て水やりを判断する。",
            steps = listOf(
                "土の表面を確認する。",
                "乾いている場合は少量ずつ水を与える。",
                "受け皿に水が溜まっている場合は捨てる。",
            ),
            frequency = "週1回目安",
            cautions = listOf("植物の種類により頻度は異なる。", "過湿に注意する。"),
            imageUrl = null,
        ),
        CareGuide(
            id = "guide-planter-sunlight-basic",
            title = "置き場所と日当たりの確認",
            categoryId = "cat-interior-green",
            summary = "植物の好みに合わせて明るさと風通しを整える。",
            steps = listOf(
                "直射日光が長時間当たる場所を避ける。",
                "明るい日陰やレース越しの光が入る場所を選ぶ。",
                "ときどき鉢の向きを変えて生育の偏りを抑える。",
            ),
            frequency = "月1回目安",
            cautions = listOf("急な環境変化を避ける。", "エアコンの風が直接当たらないようにする。"),
            imageUrl = null,
        ),
        CareGuide(
            id = "guide-seasonal-care-basic",
            title = "季節ごとのケア確認",
            categoryId = "cat-interior-green",
            summary = "季節に応じて水やりと環境を見直す。",
            steps = listOf(
                "春から夏は乾きやすいため土の状態をこまめに確認する。",
                "秋から冬は水やりを控えめにする。",
                "季節の変わり目に置き場所の明るさと温度を見直す。",
            ),
            frequency = "季節の変わり目",
            cautions = listOf("冬の過湿に注意する。", "暖房や冷房の近くを避ける。"),
            imageUrl = null,
        ),
    )

    override suspend fun careGuides(): List<CareGuide> = guides

    override suspend fun careGuide(id: String): CareGuide? = guides.firstOrNull { it.id == id }

    override suspend fun careGuides(ids: List<String>): List<CareGuide> =
        guides.filter { it.id in ids }
}
