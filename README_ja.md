# MarkdownNote

Jetpack Compose と Material Design 3 で構築された、Android 向け Markdown メモアプリです。

## 機能

- **メモの作成・編集・削除** — シンプルでモダンなインターフェースでメモを管理
- **Markdownエディタ** — CommonMark 準拠の Markdown 記法に完全対応（GFM 拡張: 取り消し線・テーブル）
- **ライブプレビュー** — 編集モード・分割モード（エディタ＋プレビュー並列表示）・閲覧モードを切り替え可能
- **検索** — テキスト内容でメモをリアルタイムに絞り込み
- **スワイプ削除** — メモを左にスワイプして削除確認ダイアログを表示
- **ファイル出力** — メモをプレーンテキスト (.txt)・Markdown (.md)・HTML (.html) 形式でデバイスのドキュメントフォルダに保存
- **未保存変更の検知** — 編集中のメモから離れる際に保存・破棄を確認
- **オフライン対応** — すべてのデータを Room (SQLite) データベースにローカル保存

## 動作要件

| 項目 | 値 |
|------|----|
| 最小 SDK | API 24 (Android 7.0) |
| ターゲット SDK | API 35 |
| JDK | 21 |

## 使用技術

| カテゴリ | ライブラリ／ツール |
|----------|--------------------|
| 言語 | Kotlin 2.3.0 |
| UI | Jetpack Compose (BOM 2025.12.01)、Material Design 3 |
| ナビゲーション | Jetpack Navigation Compose 2.9.6 — `@Serializable` 型安全ルート |
| DI | Hilt 2.57.2 |
| データベース | Room 2.8.4 |
| Markdown | CommonMark 0.24.0 + GFM 拡張（取り消し線・テーブル） |
| シリアライズ | Kotlinx Serialization 2.3.0 |
| ログ | Timber 5.0.1 |
| ビルドシステム | AGP 8.13.2、KSP 2.2.21-2.0.4 |

## アーキテクチャ

**MVVM** パターンと **Clean Architecture** を採用し、複数の Gradle モジュールに分割しています。

```
プレゼンテーション層  (feature:memo)
        │  Compose 画面、ViewModel、UiState
        ▼
ドメイン層            (domain)
        │  ユースケース、リポジトリインターフェース、ドメインモデル
        ▼
データ層              (data)
        │  Room DB、DAO、リポジトリ実装、Hilt モジュール
        ▼
コア層                (core:ui, core:common)
           共通 Compose コンポーネント、テーマ、Markdown パーサー、ファイル I/O
```

データは Kotlin `Flow` / `StateFlow` でリアクティブに流れます。各 ViewModel は単一の不変 `UiState` オブジェクトを公開し、Compose の再コンポーズを駆動します。

## モジュール構成

```
MarkdownNote/
├── app/                    # アプリケーションのエントリポイント、Hilt セットアップ、ナビゲーションホスト
├── feature/
│   └── memo/               # MemoListScreen、MemoEditorScreen、ViewModel、UiState
├── domain/                 # Memo モデル、ユースケース（取得・検索・保存・削除）、リポジトリインターフェース
├── data/                   # Room DB、DAO、MemoRepositoryImpl、Hilt DI モジュール
├── core/
│   ├── ui/                 # 共通 Compose コンポーネント（ダイアログ、MarkdownHtmlView）、アプリテーマ
│   └── common/             # Markdown パーサー（CommonMark）、FileAccessor、ユーティリティ関数
└── gradle/
    └── libs.versions.toml  # バージョンカタログ — 全依存関係の一元管理
```

### モジュール依存関係

```
         ┌─────────────────────────────────────┐
         │                app                  │
         └──┬──────────┬──────────┬────────────┘
            │          │          │
       feature:memo   data      domain
            │    \     │    \      │
          core:ui  domain  domain  core:common
                            │
                        core:common
```

## ビルド・実行方法

```bash
# 1. リポジトリをクローン
git clone https://github.com/tetsuyaokuyama0312/MarkdownNote.git
cd MarkdownNote

# 2. Android Studio（Meerkat 以降推奨）で開く、
#    またはコマンドラインでビルド
./gradlew assembleDebug

# 3. 接続済みデバイス / エミュレーターにインストール
./gradlew installDebug
```

### テストの実行

```bash
# ユニットテスト
./gradlew test

# インストゥルメンテーションテスト（デバイスまたはエミュレーターが必要）
./gradlew connectedAndroidTest
```

## 主要な設計上の決定

| 決定事項 | 理由 |
|----------|------|
| マルチモジュール構成 | レイヤーの境界を明確に保ち、各モジュールを独立してビルド・テスト可能にするため |
| 型安全なナビゲーション | コンパイル時にルート定義の誤りを検出し、文字列ベースのバグを排除するため |
| `@Serializable` ルートオブジェクト | Navigation Compose と Kotlin Serialization をシームレスに統合するため |
| ユースケースの `operator fun invoke()` | 単一責任の原則に準拠し、テストでのモック差し替えを容易にするため |
| `FileAccessor` を `core:common` に配置 | ViewModel からファイル I/O ロジックを分離し、`data` モジュールから Hilt で提供するため |
| CommonMark + GFM 拡張 | 仕様準拠の Markdown レンダリングとテーブル・取り消し線をサポートするため |

## ライセンス

このプロジェクトは個人・学習目的で作成されています。現在ライセンスは設定されていません。
