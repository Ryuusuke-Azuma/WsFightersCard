# 課題: 設定画面へのデータ全削除機能の実装

## 概要 (Title)

設定画面から、アプリ内のすべてのデータを一括で削除できる機能を実装する。

## 現状の課題 (Problem)

現在、個別のデータ（大会、ファイター、デッキ）を削除する機能はあるが、アプリを初期状態に戻したい場合や、インポート前にデータをクリーンアップしたい場合に、一括で削除する手段がない。

## 期待されるゴール (Goal)

- 設定画面から簡単な操作で、データベース内のすべての情報を削除できる。
- 誤操作を防ぐための確認ダイアログが表示される。
- 削除実行後、アプリの状態が初期化され、ユーザーに完了が通知される。

## 実装内容 (Implementation)

- **DB**: `FightersDatabase.sq` に `deleteAllFighters` を追加。
- **Repository**: `FightersRepository` に全データを削除する `clearAllData` を実装。外部キーの CASCADE 削除を利用し、整合性を維持。
- **ViewModel**: `SettingsViewModel` に全データ削除処理を実装。
- **UI**: 
  - `preferences.xml` に「全データ削除」の項目を追加。
  - 確認ダイアログを表示し、ユーザーの同意を得た後に実行。

## 優先度 (Priority)

- [ ] High
- [x] Middle
- [ ] Low

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Closed
