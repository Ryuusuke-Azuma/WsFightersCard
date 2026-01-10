# 課題: このアプリについて(About)画面の実装

## 概要 (Title)

設定画面から遷移できる、アプリケーションの情報を表示する「このアプリについて」画面を実装する。

## 機能要求 (Feature Request)

- **設定画面への項目追加**:
  - `SettingsFragment` に、「このアプリについて」という新しい `Preference` を追加する。

- **About画面の実装**:
  - 上記の項目をタップした際に、About画面（新しい `Fragment` または `Activity`）へ遷移する。
  - About画面には、以下の内容を記載する。
    - アプリアイコンとアプリ名
    - バージョン情報 (例: `version 1.0.0`)
    - 開発者名 (例: `Created by Ryuusuke Azuma`)
    - オープンソースライセンス情報へのリンク（または画面）

## 優先度 (Priority)

- [ ] High
- [ ] Middle
- [x] Low

## ステータス (Status)

- [x] Open
- [ ] In Progress
- [ ] Done
