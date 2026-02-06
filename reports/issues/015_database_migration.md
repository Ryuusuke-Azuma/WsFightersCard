# 課題: データベースのバージョニングとマイグレーション機能の導入

## 概要 (Title)

将来のアプリ更新でデータベースの構造（スキーマ）を変更する際に、ユーザーの既存のデータを失うことなく、安全にデータベースを更新できる仕組みを導入する。

## 背景 (Background)

現在、このアプリのデータベースにはバージョニングの仕組みがない。そのため、もし将来的に`game`テーブルに新しいカラムを追加するなどのスキーマ変更が必要になった場合、古いバージョンのアプリを使っているユーザーのデータベースを更新できず、アプリがクラッシュしたり、データを失ったりする原因となる。

SQLDelightには、この問題を解決するための強力なマイグレーション機能が組み込まれている。

## 実装タスク (Tasks)

1.  **`DatabaseDriverFactory`の修正:**
    - Android (`androidMain`) とiOS (`iosMain`) の両方の`DatabaseDriverFactory`の実装を修正する。
    - `createDriver`メソッド内で、`AndroidSqliteDriver`（または`NativeSqliteDriver`）を初期化する際に、`callback`引数を追加する。
    - `callback`内で`onMigrate`メソッドをオーバーライドし、その中で`FightersDatabase.Schema.migrate(driver, oldVersion, newVersion)`を呼び出すように実装する。

2.  **データベースバージョンの初期設定:**
    - `FightersDatabase.sq`ファイルの先頭に、`PRAGMA user_version = 1;`を追加する。これにより、現在のデータベーススキーマのバージョンが`1`であることを定義する。

## 期待される効果 (Expected Outcome)

- このタスクが完了すると、今後のデータベーススキーマの変更が、マイグレーションファイル（例: `1.sqm` -> `2.sqm`）を作成するだけで、安全かつ簡単に行えるようになる。
- ユーザーは、アプリをアップデートしても、過去に入力したデータを失うことなく、新しいデータベース構造に自動的に移行できるようになる。

## ステータス (Status)

- [x] Open
- [ ] In Progress
- [ ] Done
