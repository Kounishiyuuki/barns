# 03. 技術設計

## 対象プラットフォーム

- iOS: Swift / SwiftUI
- Android: Kotlin / Jetpack Compose
- mock API server: 後続タスクで `server/mock-api/` に実装(本ドキュメントの対象外)

## アーキテクチャ方針

barns は **MVVM + Clean Architecture** を採用する。

- **MVVM** は Presentation 層の構成方針である。View(SwiftUI / Compose)は
  ViewModel が公開する UI state を描画し、ユーザー操作を ViewModel に渡す。
- **Clean Architecture** は層全体の責務分離と依存方向を定める。UI / Domain /
  Data を分離し、依存は常に内側(Domain)へ向ける。
- iOS と Android はコードを共有しないが、**同じ概念モデルと同じレイヤー構造**を
  並行して持つ。これにより一方の設計をもう一方へ写しやすくする。

### 依存方向ルール

```text
Presentation(View / ViewModel)
        │  依存
        ▼
     Domain(Entity / UseCase / Repository interface)
        ▲
        │  実装
     Data(Repository impl / DataSource / DTO / Mapper)

Infrastructure(Platform) は Data / Presentation から利用される横断的基盤
```

- 依存は外側 → 内側の一方向のみ。Domain は他層へ依存しない。
- Domain はフレームワーク非依存(SwiftUI / Compose / DB / HTTP を import しない)。
- Presentation は Domain にのみ依存し、Data / DataSource を直接参照しない。
- Data は Domain の Repository interface を実装する(依存性逆転)。

## レイヤーと責務境界

| 区分 | 所属層 | 責務 | 依存先 | 禁止事項 |
|---|---|---|---|---|
| Presentation(View) | Presentation | UI 描画、操作の受け取り、UI state の表示 | ViewModel | 業務ルール、ネットワーク/DB直接アクセス |
| ViewModel | Presentation | UI state の保持・更新、UseCase 呼び出し、表示用整形 | UseCase(Domain) | DTO 参照、DataSource 直接アクセス |
| UseCase | Domain | 単一の業務ユースケースの実行、業務ルール | Entity、Repository interface | フレームワーク依存、UI 依存 |
| Repository(interface) | Domain | データ取得/保存の抽象契約 | Entity | 実装詳細(HTTP/DB)の露出 |
| Repository(implementation) | Data | interface の実装、DataSource の調整、Entity への変換 | DataSource、Mapper | UI 依存、業務ルールの内包 |
| DataSource | Data | 具体的なデータ入出力(Local / Remote) | Infrastructure(永続化/ネットワーク) | 業務ルール、Entity 直接返却 |
| DTO + Mapper | Data | 外部表現(JSON/DB行)と Entity の相互変換 | — | 業務ルール、UI 依存 |
| Platform / Infrastructure | Infrastructure | 認証、永続化、ネットワーク、通知、設定などの横断基盤 | OS / ライブラリ | 業務ルール、画面固有ロジック |

- **DTO は Data 層に閉じる。** Domain / Presentation は Entity のみを扱う。
- **Repository interface は Domain、実装は Data。** これにより mock / local /
  remote を差し替えても Domain と Presentation は変更不要にする。
- **ViewModel は UseCase 経由でのみデータへアクセスする。**

## iOS ファイル構成(確定)

```text
apps/ios/
  Barns/
    App/                      # アプリ起動、DI 構築、ルーティング
    Presentation/
      <Feature>/              # 例: Items, CareGuides, Notices, Consultation, Auth
        Views/                # SwiftUI View
        <Feature>ViewModel.swift
        <Feature>UiState.swift
      Common/                 # 共通 UI 部品、表示用ヘルパー
    Domain/
      Entities/               # Category, Pattern, CareGuide, Notice, CompanyInfo,
                              #   ProductItem, CareTask, CareLog, ConsultationDraft
      UseCases/
      Repositories/           # protocol(interface)
    Data/
      Repositories/           # implementation
      DataSources/
        Local/                # 端末内ストレージ(local-first)
        Remote/               # mock API / 将来の会社サーバー
      DTO/
      Mappers/
    Infrastructure/
      Auth/                   # mock authentication、トークン保管(平文不可)
      Persistence/            # 端末内DB / ファイル
      Network/                # HTTP クライアント、API contract 参照
      Notification/           # OS ローカル通知
      Config/                 # 環境設定(秘匿値は含めない)
    Resources/
    Tests/
      DomainTests/
      DataTests/
      PresentationTests/
```

## Android ファイル構成(確定)

```text
apps/android/
  app/src/main/java/jp/barns/app/
    app/                      # Application、DI、Navigation
    presentation/
      <feature>/              # 例: items, careguides, notices, consultation, auth
        <Feature>Screen.kt    # Composable
        <Feature>ViewModel.kt
        <Feature>UiState.kt
      common/                 # 共通 Composable、表示用ヘルパー
    domain/
      entity/
      usecase/
      repository/             # interface
    data/
      repository/             # implementation
      datasource/
        local/                # 端末内ストレージ(local-first)
        remote/               # mock API / 将来の会社サーバー
      dto/
      mapper/
    infrastructure/
      auth/                   # mock authentication、トークン保管(平文不可)
      persistence/            # 端末内DB(例: Room)
      network/                # HTTP クライアント、API contract 参照
      notification/           # OS ローカル通知
      config/                 # 環境設定(秘匿値は含めない)
  app/src/test/
  app/src/androidTest/
```

- iOS / Android はディレクトリ名の慣習(PascalCase / lowercase)を各プラット
  フォームに合わせるが、**レイヤーと feature の区切りは一致させる**。
- feature 単位は MVP の画面に対応する(アイテム、ケアガイド、お知らせ、相談
  ドラフト、認証、電話相談導線)。MVP 外の feature は追加しない。

## データフローと差し替え方針

- **mock API first:** Remote DataSource は当初 `server/mock-api/` を参照する。
  対象は静的サポートデータ(categories / patterns / care-guides / notices /
  company-info)と mock 認証のみ。
- **local-first:** ユーザー作成データ(アイテム、ケアログ、相談ドラフト等)は
  Local DataSource(端末内)に保存し、mock API へは送らない。
- **将来の会社サーバー移行:** Remote DataSource の実装を差し替えるのみで、
  Repository interface・UseCase・ViewModel・View は変更しない。API versioning、
  差分同期、削除状態、競合解決はその段階で設計する。
- **電話相談導線:** 相談は送信 API を持たず、UI から電話導線へ誘導する。相談
  ドラフトは端末内にのみ保持する。

## セキュリティ / プライバシー分離

- **端末内データと会社管理データの境界を層で明確化する。** Local DataSource は
  プライベートデータ、Remote DataSource は会社提供の静的データのみを扱う。
- mock API へ送信・保存してはならないデータ: 登録アイテム、ケアログ、相談
  ドラフト、写真、住所、電話番号、個人メモ。
- 認証情報やトークンを平文保存しない(Infrastructure/Auth で安全に保管)。
- 個人データをログ出力しない。
- 秘匿値(API キー、`.env`、`.dev.vars`)はリポジトリに含めない。Config は
  非秘匿の設定のみを扱う。
- データ最小化を原則とし、Remote へ渡す項目を必要最小限にする。

## Local-first 方針

- MVP の主要データは端末内に保存する。
- 初期データは `shared/mock-data/` の JSON を元にする。
- ユーザー作成データは端末内DBに保存する。
- 将来のサーバー同期に備え、Entity には `id`、`updatedAt`、必要に応じて
  `syncStatus` を持たせる。
- 競合解決は将来設計とし、MVP では単一端末利用を前提にする。
- 画像は任意。すべての `imageUrl` は `null` を許容し、UI とモデルが成立する。

## 認証方針

- MVP は mock authentication。
- Domain 層では認証状態を抽象化する(Repository interface)。
- 本番認証へ移行しても画面や UseCase の変更が最小になるようにする。
- 認証情報やトークンを平文保存しない。

## 通知方針

- MVP では OS ローカル通知を想定。
- ケアタスクの期日に基づいて通知する。
- 通知許可は明示的に取得する。
- 通知なしでもアプリ内タスク一覧でケア予定を確認できる。

## mock API server 方針

- 本タスクでは実装しない(`server/mock-api/` の実装タスクで対応)。
- `shared/mock-data/` をレスポンスの元データとして利用する。
- API contract を先に固定し、iOS/Android の Repository 実装が同じ契約を参照
  できるようにする。

## テスト方針

- Domain 層の UseCase を優先して単体テスト。
- Repository / Mapper のテスト。
- ViewModel の UI state テスト。
- 画像なしデータの表示テスト。
- local-first 保存と復元のテスト。

## 実装しないこと

- 本タスクでは iOS、Android、server のコードを実装しない。
- 本番 API、本番認証、決済、チャット、予約は MVP 外。
- スタッフ管理、注文管理、見積、アプリ内注文送信は MVP 外。
