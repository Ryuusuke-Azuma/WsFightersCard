# 課題: プロフィール機能の刷新（マスター/ディテールUI）

## 概要 (Title)

プロフィール画面のUIを、自分とフレンドを一覧で表示する「マスター/ディテール」形式のタブ構成に刷新する。

## 機能要求 (Feature Request)

### 1. タブ構成の導入
プロフィール画面に `ViewPager2` と `TabLayout` を導入し、以下の2つのタブを設ける。
- **タブ1: ファイター**: 自分とフレンドを含めた、全てのプロフィールをリスト表示する。
- **タブ2: コレクション**: 「ファイター」タブで選択されたユーザーの詳細情報を表示する。

### 2. ファイタータブ (`FightersPageFragment`)
- **表示項目**:
  - プレイヤー名
- **機能**:
  - リストの項目をタップすると、そのユーザーが「選択状態」になり、「コレクション」タブにそのユーザーの情報が表示される。

### 3. コレクションタブ (`CollectionsPageFragment`)
- **表示項目**:
  - プレイヤー名
  - 最もよく使うデッキ
- **編集機能**:
  - 「自分」のプロフィールを表示している時のみ、編集用のFAB（フローティングアクションボタン）を表示する。

## 完了した作業 (Completed Work)
- `ProfileFragment`を親とし、`FightersPageFragment`と`CollectionsPageFragment`をタブで表示する画面の骨格を実装した。
- 各フラグメントに対応するViewModel (`FightersViewModel`, `CollectionsViewModel`) を作成し、責務を分離した。
- プロフィール情報（`Profile`データクラス）や選択状態を管理するための共有ViewModelとして`ProfileViewModel`を配置した。
- 今後のデータベース実装に備え、各ViewModelおよびFragmentから、表示に関わる具体的なロジックは一旦削除し、ビルドが通るクリーンな状態にした。

## 次の課題 (Next Steps)
- プロフィール情報を永続化するためのデータベーススキーマの設計と実装。
- `Room`または`SQLDelight`を使用したデータベースの作成。
- `FightersRepository`に、プロフィールのCRUD操作を追加する。
  - **詳細は新しい課題 `009_create_profile_database.md` で管理する。**

## 優先度 (Priority)

- [x] High

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Closed
