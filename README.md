# barns

`barns` は、壁面緑化・インテリアグリーン会社の既存顧客向けアフターサポートアプリです。

新規顧客獲得ではなく、購入・施工後の顧客満足度を高めることを主目的とします。ユーザーは購入・設置済みのグリーンやインテリア品を管理し、手入れ方法、リマインダー、ケア履歴、壁面緑化パターン、相談ドラフト、電話問い合わせ導線を利用できます。

## 初期方針

- iOS: Swift / SwiftUI
- Android: Kotlin / Jetpack Compose
- アーキテクチャ: Clean Architecture
- MVPでは mock authentication を許容
- 顧客側データは local-first
- 会社所有サーバー連携は将来対応
- 先に mock API server を計画し、実装は後続タスク
- 画像は任意。UIとデータモデルは `imageUrl: null` でも成立すること

## MVPで扱うこと

- アカウント/ログインの土台
- 登録済みアイテムの閲覧・簡易管理
- カテゴリ、商品/施工アイテム、ケアガイド、ケアタスク、ケアログ
- ケアリマインダー
- 壁面緑化パターン閲覧
- 相談ドラフト作成
- 電話問い合わせ導線
- お知らせ、会社情報

## MVP対象外

- アプリ内注文、見積、決済
- チャット
- 訪問・施工日程予約
- スタッフ管理画面
- 本番APIサーバー実装
- 実顧客データ投入

## ディレクトリ

```text
barns/
  docs/                 # 計画・要件・設計ドキュメント
  apps/ios/             # iOSアプリ実装予定地
  apps/android/         # Androidアプリ実装予定地
  server/mock-api/      # mock API server 実装予定地
  shared/mock-data/     # mock JSON data
  design/               # デザイン資料予定地
```

## ドキュメント

- [開発セットアップ](docs/development_setup.md)
- [プロジェクト概要](docs/00_project_overview.md)
- [要件定義](docs/01_requirements.md)
- [UI画面設計](docs/02_ui_screen_design.md)
- [技術設計](docs/03_technical_design.md)
- [データ/API設計](docs/04_data_api_design.md)
- [セキュリティ・プライバシー](docs/05_security_privacy.md)
- [開発ロードマップ](docs/06_development_roadmap.md)
- [会社確認事項](docs/07_company_questions.md)
- [法務・リリース・運用](docs/08_legal_release_operation.md)
