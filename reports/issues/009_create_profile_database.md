# 課題: プロフィール用データベースの作成

## 概要 (Title)

自分とフレンドのプロフィール情報（プレイヤー名、デッキコレクション）を永続化するための、データベースのスキーマ設計と実装を行った。

## 機能要求 (Feature Request)

### 1. データベーステーブルの設計
- **`fighter` テーブル**:
  - `id`: 主キー（自動インクリメント）
  - `name`: プレイヤー名 (TEXT, NOT NULL)
  - `is_self`: 自分自身のプロフィールかどうかを示すフラグ (INTEGER, NOT NULL, DEFAULT 0)
  - `memo`: ファイターに関するメモ (TEXT, NOT NULL)

- **`deck` テーブル**:
  - `id`: 主キー（自動インクリメント）
  - `fighter_id`: `fighter`テーブルへの外部キー (INTEGER, NOT NULL)
  - `deck_name`: デッキ名 (TEXT, NOT NULL)
  - `memo`: デッキに関するメモ (TEXT, NOT NULL)

### 2. データベースの実装
- `SQLDelight`を用いて、上記のスキーマを持つデータベースをプロジェクトに組み込んだ。
- `FightersRepository` に、`fighter`および`deck`テーブルに対するCRUD操作を実装した。
- 外部キー制約に`ON DELETE CASCADE`を設定し、ファイター削除時に、そのファイターが持つデッキも自動的に削除されるようにした。

## 次の課題 (Next Steps)
- `FightersViewModel`から`FightersRepository`のメソッドを呼び出し、データベースから取得したファイターの一覧を画面に表示する。
- **詳細は新しい課題 `010_implement_profile_display` で管理する。**

## 優先度 (Priority)

- [x] High

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Done
