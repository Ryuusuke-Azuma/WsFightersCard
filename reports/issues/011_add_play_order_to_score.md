# 課題: スコア登録に先攻/後攻の入力欄を追加

## 概要 (Title)

各対戦のスコアを登録する際に、自分が「先攻」だったか「後攻」だったかを記録できるようにする。これにより、先攻/後攻別の勝率など、より詳細なデータ分析を可能にする。

## 機能要求 (Feature Request)

### 1. データベースのスキーマ変更
- `score` テーブルに、先攻/後攻を記録するための新しいカラム `battle_order` を追加する。
  - **カラム名**: `battle_order`
  - **データ型**: `INTEGER`
  - **制約**: `NOT NULL`
  - **デフォルト値**: `0` (先攻)
- このカラムに対応する `enum class BattleOrder(val id: Long)` を作成する。
  - `FIRST(0)`: 先攻
  - `SECOND(1)`: 後攻

### 2. UIの変更
- `dialog_record_score.xml` に、先攻/後攻を選択するための `RadioGroup` を追加する。
  - 「先攻」「後攻」のラジオボタンを配置する。

### 3. ロジックの実装
- `RecordScoreDialogFragment` を修正し、以下の処理を追加する。
  - ダイアログ表示時に、記録済みの `battle_order` の値に応じてラジオボタンの選択状態を復元する。
  - スコアを保存する際に、選択されているラジオボタンの値を `battle_order` として `FightersRepository` に渡す。
- `FightersRepository` と `FightersDatabase.sq` を修正し、`addScore` と `updateScore` で `battle_order` の値を保存・更新できるようにする。

## 優先度 (Priority)

- [x] High

## ステータス (Status)

- [x] Open
- [ ] In Progress
- [ ] Done
