# 課題: スコア登録に先攻/後攻の入力欄を追加

## 概要 (Title)

各対戦のスコアを登録する際に、自分が「先攻」だったか「後攻」だったかを記録できるようにする。これにより、先攻/後攻別の勝率など、より詳細なデータ分析を可能にする。

## 機能要求 (Feature Request)

### 1. データベースのスキーマ変更
- `score` テーブルに、先攻/後攻を記録するための新しいカラム `first_second` を追加した。
  - **カラム名**: `first_second`
  - **データ型**: `INTEGER`
  - **制約**: `NOT NULL`
  - **デフォルト値**: `0` (先攻)
- このカラムに対応する `enum class FirstSecond(val id: Long)` を作成した。
  - `FIRST(0)`: 先攻
  - `SECOND(1)`: 後攻

### 2. UIの変更
- `dialog_record_score.xml` に、先攻/後攻を選択するための `RadioGroup` を追加した。
  - 「先攻」「後攻」のラジオボタンを配置した。

### 3. ロジックの実装
- `RecordScoreDialogFragment` を修正し、以下の処理を追加した。
  - ダイアログ表示時に、記録済みの `first_second` の値に応じてラジオボタンの選択状態を復元する。
  - スコアを保存する際に、選択されているラジオボタンの値を `first_second` として `FightersRepository` に渡す。
- `FightersRepository` と `FightersDatabase.sq` を修正し、`addScore` と `updateScore` で `first_second` の値を保存・更新できるようにした。
- インポート/エクスポート機能も、この新しいカラムに対応するように修正した。

### 4. ドキュメントの更新
- `DATABASE_ER.puml`, `database.md`, `screen_schedule.md` を最新の仕様に合わせて更新した。

## 優先度 (Priority)

- [x] High

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [ ] In Review
- [x] Closed
