# 課題: プロフィール情報の画面表示

## 概要 (Title)

作成したデータベースとリポジトリを利用し、プロフィール画面にファイターの一覧と、選択されたファイターのコレクション（デッキ一覧）を表示する。

## 機能要求 (Feature Request)

### 1. ファイター一覧の表示
- `FightersViewModel`に、`FightersRepository`の`getAllFighters()`を呼び出し、取得したファイターのリストを`LiveData`で保持するロジックを実装する。
- `FightersPageFragment`で、`FightersViewModel`の`LiveData`を監視し、`RecyclerView`にファイターの一覧を表示する。
- リストの項目がタップされたら、選択されたファイターの情報を共有`ProfileViewModel`に通知する。

### 2. コレクション（デッキ一覧）の表示
- `CollectionsViewModel`に、選択されたファイターのIDを元に、`FightersRepository`の`getDecksByFighterId()`を呼び出し、そのファイターが持つデッキのリストを取得・保持するロジックを実装する。
- `CollectionsPageFragment`で、共有`ProfileViewModel`を監視し、選択されたファイターが変更されたことを検知する。
- 選択されたファイターの情報を元に`CollectionsViewModel`のデータを更新し、UIに詳細情報（プレイヤー名、デッキ一覧など）を表示する。

## 優先度 (Priority)

- [x] High

## ステータス (Status)

- [x] Open
- [ ] In Progress
- [ ] Done
