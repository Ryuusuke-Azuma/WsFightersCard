# 課題: プロフィール用データベースの作成

## 概要 (Title)

自分とフレンドのプロフィール情報（プレイヤー名、メインデッキなど）を永続化するための、データベースのスキーマ設計と実装を行う。

## 機能要求 (Feature Request)

### 1. データベーステーブルの設計
- **`fighter` テーブル**:
  - `id`: 主キー（自動インクリメント、またはUUID）
  - `name`: プレイヤー名 (TEXT, NOT NULL)
  - `main_deck`: メインデッキ名 (TEXT)
  - `is_self`: 自分自身のプロフィールかどうかを示すフラグ (BOOLEAN, DEFAULT 0)

### 2. データベースの実装
- `SQLDelight` または `Room` を用いて、上記のスキーマを持つデータベースをプロジェクトに組み込む。
- `FightersRepository` に、`fighters` テーブルに対する以下の操作（CRUD）を実装する。
  - プロフィールの新規作成 (Create)
  - 全プロフィールの読み出し (Read)
  - プロフィールの更新 (Update)
  - プロフィールの削除 (Delete)

## 優先度 (Priority)

- [x] High

## ステータス (Status)

- [x] Open
- [ ] In Progress
- [ ] Done
