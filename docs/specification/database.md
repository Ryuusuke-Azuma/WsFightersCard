# データベース仕様書

## 概要

このドキュメントは、アプリケーションが使用するデータベースのスキーマについて詳述します。
ER図は `DATABASE_ER.puml` を参照してください。

## テーブル定義

### 1. `game` テーブル

**説明:** 大会やフリー対戦など、一連の対戦イベントの概要を格納します。

| カラム名 | データ型 | 制約 | 説明 |
| :--- | :--- | :--- | :--- |
| `id` | INTEGER | **PK** | 主キー |
| `game_name` | TEXT | `NOT NULL` | 大会・イベントの名称 |
| `game_date` | INTEGER | `NOT NULL` | 開催日。Unixタイムスタンプ (ミリ秒) で格納 |
| `game_style` | INTEGER | `NOT NULL` | 対戦形式。`GameStyle` enumに対応 (例: `0`=シングルス, `1`=チーム戦) |
| `memo` | TEXT | `NOT NULL` | イベント全体に関するメモ |

### 2. `score` テーブル

**説明:** `game` テーブルに紐づく、各個別の対戦結果を格納します。

| カラム名 | データ型 | 制約 | 説明 |
| :--- | :--- | :--- | :--- |
| `id` | INTEGER | **PK** | 主キー |
| `game_id` | INTEGER | **FK** | `game`テーブルへの外部キー |
| `battle_deck` | TEXT | `NOT NULL` | 自分が使用したデッキ名 |
| `matching_deck`| TEXT | `NOT NULL` | 対戦相手が使用したデッキ名 |
| `win_lose` | INTEGER | `NOT NULL` | **個人の勝敗**を記録します。`WinLose` enumに対応 (例: `0`=負け, `1`=勝ち)。チーム戦の場合でも、その対戦における個人の結果をここに格納します。 |
| `team_win_lose`| INTEGER | (NULLable) | **チームの戦績**を記録します。`TeamWinLose` enumに対応 (例: `0`=3-0, `1`=2-1)。シングルスの場合は `NULL` となります。 |
| `memo` | TEXT | `NOT NULL` | その対戦に関するメモ |

### 3. `fighter` テーブル

**説明:** ユーザー本人と、対戦相手として記録するフレンドのプロフィール情報を格納します。

| カラム名 | データ型 | 制約 | 説明 |
| :--- | :--- | :--- | :--- |
| `id` | INTEGER | **PK** | 主キー |
| `name` | TEXT | `NOT NULL` | ファイターの名前 |
| `is_self` | INTEGER | `NOT NULL` | このレコードがユーザー本人であるかを示すフラグ ( `1`=自分, `0`=他人) |
| `memo` | TEXT | `NOT NULL` | ファイターに関するメモ |

### 4. `deck` テーブル

**説明:** `fighter` テーブルに紐づく、各ファイターが所有するデッキの情報を格納します。

| カラム名 | データ型 | 制約 | 説明 |
| :--- | :--- | :--- | :--- |
| `id` | INTEGER | **PK** | 主キー |
| `fighter_id` | INTEGER | **FK** | `fighter`テーブルへの外部キー |
| `deck_name` | TEXT | `NOT NULL` | デッキの名称 |
| `memo` | TEXT | `NOT NULL` | デッキに関するメモ |
