# 開発セットアップ

barns の iOS / Android プロジェクトをローカルで開いてビルド・検証する手順と、CI で実行される確認内容をまとめます。

## プロジェクト状況

- barns は現在 MVP / local-first / mock-first のモバイルアプリプロジェクトです。
- iOS・Android ともにビルド可能です。
- 両プラットフォームに GitHub Actions の CI があります。

## iOS ローカルセットアップ

- `apps/ios/Barns/Barns.xcodeproj` を Xcode で開きます。
- ビルドスキーム: `Barns`
- 推奨ローカル検証コマンド(CI と同等のシミュレータビルド):

  ```bash
  xcodebuild -project apps/ios/Barns/Barns.xcodeproj -scheme Barns \
    -destination 'generic/platform=iOS Simulator' -configuration Debug \
    CODE_SIGNING_ALLOWED=NO build
  ```

- 現在のシミュレータビルドに Apple Developer Team 署名は不要です。

## Android ローカルセットアップ

```bash
cd apps/android
./gradlew tasks
./gradlew assembleDebug
```

- Android Gradle Plugin の実行には Java 17 が必要です。
- `local.properties`、`.gradle`、ビルド成果物(`build/`)、keystore、`google-services.json` はコミットしないでください(`.gitignore` 済み)。

## CI

- Android CI: `pull_request` と `main` への `push` で実行。
- iOS CI: `pull_request` と `main` への `push` で実行。
- Android は Java 17 で `./gradlew assembleDebug` を実行します。
- iOS は `CODE_SIGNING_ALLOWED=NO` のシミュレータビルド(`xcodebuild`)を実行します。
- CI は署名シークレットを使用しません。

ワークフロー定義: `.github/workflows/android-ci.yml`、`.github/workflows/ios-ci.yml`

## ガードレール

- 実 API / サーバー連携はまだ行いません。
- 実認証はまだ行いません。
- DB / 永続化は、明示的に計画されない限りまだ追加しません。
- 分析・トラッキングは追加しません。
- シークレットや本番 URL は追加しません。
- 顧客側のプライベートデータは local-first を維持します。

## 次の開発方向(任意・暫定)

- プラットフォームのスモークテスト追加
- UI の調整
- mock API 連携の計画

(上記は方向性のメモであり、確定したスコープではありません。)
