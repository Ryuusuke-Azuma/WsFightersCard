# 画面仕様書: プロフィール (Profile)

## 1. 概要

自分やフレンドのプロフィール情報を管理する画面。
「ファイター一覧」と、選択したファイターの「コレクション（詳細）」をタブで切り替えて表示する、マスター/ディテール形式のUIを採用する。

## 2. 画面レイアウト

本画面は、`ProfileFragment` をコンテナとし、内部に2つのページを持つ `ViewPager2` で構成される。

- **タブ1: ファイター (`FightersPageFragment`)**
  - 自分とフレンドを含む、すべてのファイターのリストを `RecyclerView` で表示する。
- **タブ2: コレクション (`CollectionsPageFragment`)**
  - 「ファイター」タブで選択されたファイターの、デッキなどの詳細情報を表示する。

## 3. クラス構成と責務

| クラス名 | 責務 |
|---|---|
| `ProfileFragment` | タブ (`TabLayout`) と `ViewPager2` のセットアップおよび管理を行う、機能全体のコンテナ。 | 
| `FightersPageFragment` | 全てのファイター（自分とフレンド）をリスト表示する。 | 
| `CollectionsPageFragment`| 選択されたファイターのデッキなどのコレクション情報を表示する。 |
| `ProfileViewModel` | `FightersPageFragment`と`CollectionsPageFragment`間のデータ（選択されたファイター情報）の受け渡しを担う共有ViewModel。 |
| `FightersViewModel` | ファイター一覧のデータ取得と状態管理に責務を持つ。 | 
| `CollectionsViewModel` | 選択されたファイターのコレクション詳細データの状態管理に責務を持つ。 |

## 4. 動作仕様

- **初期表示**: 
  - `ProfileFragment` が表示されると、`FightersViewModel` がリポジトリからファイター一覧を非同期で読み込む。
  - 読み込み完了後、`FightersPageFragment` の `RecyclerView` が更新される。
  - デフォルトで「自分」のプロフィールが選択され、その情報が`ProfileViewModel`を通じて`CollectionsPageFragment`に通知される。
- **ファイター選択**: 
  - `FightersPageFragment`のリスト項目がタップされると、選択された`Profile`オブジェクトの情報が`ProfileViewModel`にセットされる。
- **タブ連動**: 
  - `ProfileViewModel`の`selectedProfile`が更新されると、`ViewPager2`が自動的に「コレクション」タブ（2ページ目）に切り替わる。
- **詳細表示**: 
  - `CollectionsPageFragment`は`ProfileViewModel`の`selectedProfile`を監視している。
  - `selectedProfile`が変更されると、その情報を自身の`CollectionsViewModel`に渡し、UI（プレイヤー名、メインデッキなど）を更新する。
- **編集機能**: 
  - `CollectionsPageFragment`にて、表示されているのが「自分」のプロフィールである場合のみ、編集用のFAB（フローティングアクションボタン）が表示される。

---
© 2026 Ryuusuke Azuma
