# 04. データ/API設計

## 基本方針

MVPでは `shared/mock-data/` のJSONを共通サンプルデータとして扱う。iOS/Androidは同じ概念モデルを参照し、将来のmock API serverおよび会社所有サーバーへの移行に備える。

画像は任意である。すべての `imageUrl` は `string | null` とし、`null` の場合もUIが成立する。

## 主なエンティティ

- Category: アイテム分類
- ProductItem: 購入・施工済みアイテム
- CareTask: ケア予定
- CareLog: ケア履歴
- Pattern: 壁面緑化パターン
- CareGuide: 手入れガイド
- Notice: お知らせ
- CompanyInfo: 会社情報
- ConsultationDraft: 相談ドラフト

## ID方針

- mock data は可読性のある文字列IDを使う
- 例: `item-wall-green-001`
- 将来の本番環境ではUUIDまたはサーバー採番IDを検討する

## API contract 初期案

### 認証

- `POST /auth/mock-login`
- Request: `{ "email": "user@example.invalid" }`
- Response: `{ "userId": "mock-user-001", "displayName": "Demo User", "token": "mock-token" }`

### 参照系

- `GET /categories`
- `GET /product-items`
- `GET /product-items/{id}`
- `GET /care-guides`
- `GET /care-guides/{id}`
- `GET /care-tasks`
- `GET /care-logs`
- `GET /patterns`
- `GET /notices`
- `GET /company-info`

### 相談ドラフト

- `GET /consultation-drafts`
- `POST /consultation-drafts`
- `PUT /consultation-drafts/{id}`
- `DELETE /consultation-drafts/{id}`

MVPでは送信APIを持たない。相談は電話へ誘導する。

## mock server 方向性

- 後続タスクで軽量なHTTPサーバーを作る
- `shared/mock-data/*.json` を読み込む
- CORSは開発用途のみ許可する
- 本番サーバーでは認証、認可、監査ログ、データ分離を必須にする

## 将来の会社サーバー移行

- Repository interface は維持する
- Data source を mock/local から remote に差し替える
- API versioning を導入する
- サーバー同期時は差分同期、削除状態、競合解決を設計する
- 会社管理データとユーザー端末データの境界を明確にする

## JSONファイル

- `categories.json`
- `product-items.json`
- `care-tasks.json`
- `care-logs.json`
- `patterns.json`
- `care-guides.json`
- `notices.json`
- `company-info.json`
- `consultation-drafts.json`
