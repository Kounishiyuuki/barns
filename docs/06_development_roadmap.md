# 06. 開発ロードマップ

## Phase 0: 計画

- 初期ドキュメント作成
- mock data 整備
- MVPスコープ確認
- 会社確認事項の洗い出し

## Phase 1: 共有モデル・mock API準備

- JSON schema または型定義の作成
- mock API server 実装
- API contract の確認
- iOS/Androidで共通利用するデータ項目を確定

## Phase 2: iOS MVP

- SwiftUIプロジェクト作成
- Clean Architectureの土台
- mock authentication
- アイテム一覧/詳細
- ケアタスク/ケアログ
- ケアガイド
- 相談ドラフト
- 電話問い合わせ導線

## Phase 3: Android MVP

- Jetpack Composeプロジェクト作成
- Clean Architectureの土台
- iOSと同等のMVP画面
- local-first保存
- 通知設計の反映

## Phase 4: 品質確認

- 画像なしデータでの表示確認
- オフライン表示確認
- 権限説明確認
- アクセシビリティ確認
- 端末別レイアウト確認

## Phase 5: リリース準備

- アプリ名、説明文、スクリーンショット準備
- プライバシーポリシー確定
- 問い合わせ導線の最終確認
- TestFlight / Internal testing
- ストア申請

## 将来フェーズ

- 本番認証
- 会社所有サーバー連携
- 顧客データ同期
- スタッフ管理
- 画像アップロード
- プッシュ通知
- 保証・契約情報連携

## MVP完了条件

- 主要画面がiOS/Androidで利用できる
- mock data で顧客サポート体験が確認できる
- 画像がすべて `null` でも表示できる
- 相談は電話導線に集約されている
- 注文、見積、決済、チャット、予約が混入していない
