# バグ報告: スコア修正画面のタイトルが「設定」になっていた

## 概要 (Title)

スコア修正画面のタイトルが「設定」になっていた。(修正済み)

## 詳細 (Description)

**問題/課題の具体的な内容:**

スコアを修正するために開かれるダイアログのタイトル（Toolbarのタイトル）が、画面の内容と一致しない「設定」と表示されていた。

**期待される挙動/ゴール:**

スコア修正画面のタイトルは「スコアの編集」など、画面の内容に即したものであるべき。

## 原因と修正内容 (Cause and Resolution)

**原因:**
`RecordScoreDialogFragment` を生成する際の `AlertDialog.Builder` で、`setTitle()` に誤った文字列リソースが設定されていた、あるいはタイトル設定のロジックが欠落していたことが原因。

**修正内容:**
課題`003`の対応過程で `RecordScoreDialogFragment` のリファクタリングを実施。
その際に、ダイアログが編集モード (`isEdit`) かどうかを判定し、`setTitle()` に `R.string.dialog_edit_score` (スコアの編集) または `R.string.dialog_record_score` (スコアの追加) を動的に設定するロジックを正しく実装したため、本件は解決済み。

## 再現手順 (Steps to Reproduce) ※バグの場合

1. アプリを起動する
2. 対戦履歴一覧画面を開く
3. いずれかの対戦履歴をタップし、スコア修正画面に遷移する
4. 画面上部のタイトルを確認する

## 優先度 (Priority) / 深刻度 (Severity)

- [ ] High / Critical
- [x] Middle / Major
- [ ] Low / Minor

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [ ] In Review
- [x] Closed
