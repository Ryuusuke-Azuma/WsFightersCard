# バグ報告: スコア追加時にリストが更新されない

## 概要 (Title)

スケジュール画面の「Scores」タブで戦績を追加した後、リストが自動的に更新されず、追加したデータがすぐに表示されない。

## 詳細 (Description)

**問題の具体的な内容:**

`RecordScoreDialogFragment` でスコアを登録し、ダイアログを閉じた後、`ScheduleFragment` の「Scores」タブに表示されている戦績一覧が古いままになっている。一度「Games」タブへ切り替えて戻ってくるなど、何らかの再描画処理を挟まないと、追加した最新のスコアを確認できない。

**期待される挙動:**

スコア追加ダイアログが閉じて `result_saved` が通知されたら、`ScheduleViewModel` の `loadData()` が呼び出され、大会リスト（勝敗数）と戦績リストの両方が即座に最新の状態に更新されるべき。

## 再現手順 (Steps to Reproduce)

1. スケジュール画面で任意の大会を選択し、「Scores」タブへ移動する。
2. FABをタップし、新しい戦績を登録する。
3. ダイアログを閉じる。
4. 「Scores」タブのリストに追加した戦績が表示されていないことを確認する。

## 環境 (Environment)

- **アプリバージョン**: v1.0
- **OSバージョン**: Android 13

## 深刻度 (Severity)

- [ ] Critical
- [x] Major (主要なデータ登録フローのユーザー体験を著しく損なう)
- [ ] Minor
- [ ] Trivial

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [ ] In Review
- [x] Closed
