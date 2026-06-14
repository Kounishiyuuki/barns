# 03. 技術設計

## 対象プラットフォーム

- iOS: Swift / SwiftUI
- Android: Kotlin / Jetpack Compose
- mock API server: 後続タスクで実装

## アーキテクチャ方針

Clean Architecture を採用し、UI、Domain、Dataの責務を分離する。

### レイヤー

- Presentation: SwiftUI / Jetpack Compose の画面、ViewModel、UI state
- Domain: Entity、UseCase、Repository interface、業務ルール
- Data: Repository implementation、local data source、remote data source、DTO、mapper
- Infrastructure: 認証、通知、永続化、ネットワーク、設定

## iOSファイル構成案

```text
apps/ios/
  Barns/
    Presentation/
    Domain/
    Data/
    Infrastructure/
    Resources/
    Tests/
```

## Androidファイル構成案

```text
apps/android/
  app/src/main/java/.../barns/
    presentation/
    domain/
    data/
    infrastructure/
  app/src/test/
  app/src/androidTest/
```

## Local-first 方針

- MVPの主要データは端末内に保存する
- 初期データは `shared/mock-data/` のJSONを元にする
- ユーザー作成データは端末内DBに保存する
- 将来のサーバー同期に備え、Entityには `id`、`updatedAt`、必要に応じて `syncStatus` を持たせる
- 競合解決は将来設計とし、MVPでは単一端末利用を前提にする

## 認証方針

- MVPは mock authentication
- Domain層では認証状態を抽象化する
- 本番認証へ移行しても画面やUseCaseの変更が最小になるようにする
- 認証情報やトークンを平文保存しない

## 通知方針

- MVPではOSローカル通知を想定
- ケアタスクの期日に基づいて通知する
- 通知許可は明示的に取得する
- 通知なしでもアプリ内タスク一覧でケア予定を確認できる

## mock API server 方針

- 本タスクでは実装しない
- 後続タスクで `server/mock-api/` に実装する
- `shared/mock-data/` をレスポンスの元データとして利用する
- API contract を先に固定し、iOS/AndroidのRepository実装が同じ契約を参照できるようにする

## テスト方針

- Domain層のUseCaseを優先して単体テスト
- Repository mapper のテスト
- ViewModelのUI stateテスト
- 画像なしデータの表示テスト
- local-first保存と復元のテスト

## 実装しないこと

- 本タスクではiOS、Android、serverのコードを実装しない
- 本番API、本番認証、決済、チャット、予約はMVP外
