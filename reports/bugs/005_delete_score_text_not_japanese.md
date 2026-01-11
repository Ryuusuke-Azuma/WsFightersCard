# バグ報告: 戦績削除時の確認メッセージが日本語でない

## 概要 (Title)

戦績削除時の確認メッセージが日本語でない (修正済み)

## 詳細 (Description)

**問題/課題の具体的な内容:**

戦績（スコア）を削除する際の確認ダイアログの文言が、日本語環境でも日本語で表示されていなかった。

**期待される挙動/ゴール:**

アプリの他の部分と同様に、確認ダイアログのすべてのテキストが日本語で表示されるべき。

## 原因と修正内容 (Cause and Resolution)

**原因:**
日本語用の文字列リソースファイル (`values-ja/strings.xml`) に、戦績削除の確認メッセージに対応する文字列 `dialog_delete_score_confirm_message` が定義されていなかったため、デフォルト（英語）の文字列が表示されていた。

**修正内容:**
課題 `003` の対応過程で `strings.xml` の全体的な見直しと整理を実施。その際に、`dialog_delete_score_confirm_message` を `values-ja/strings.xml` にも正しく追加したため、本件は解決済み。

## 再現手順 (Steps to Reproduce) ※バグの場合

1.  アプリを起動し、対戦履歴画面を開く。
2.  いずれかの大会を選択し、戦績一覧を表示する。
3.  特定の戦績を削除しようと操作する。
4.  表示された確認ダイアログの文言を確認する。

## 優先度 (Priority) / 深刻度 (Severity)

- [ ] High / Critical
- [x] Middle / Major
- [ ] Low / Minor

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [ ] In Review
- [x] Closed
