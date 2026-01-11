# バグ報告

## 概要 (Title)

大会登録・編集画面の日付入力がテキストフィールドになっている (修正済み)

## 再現手順 (Steps to Reproduce)

1. スケジュール画面で、大会を追加（または編集）しようとする。
2. 大会登録ダイアログが開く。
3. 「開催日」の欄が、単なるテキスト入力になっていることを確認する。

## 本来の挙動 (Expected Behavior)

「開催日」のテキストボックスをタップすると、カレンダー形式のDate Pickerが表示され、ユーザーは直感的に日付を選択できるべき。

## 実際の挙動 (Actual Behavior)

ユーザーは、"yyyy/MM/dd" という形式に合わせて、日付をキーボードで手入力しなければならず、非常に不便で入力ミスも起きやすい。

## 環境 (Environment)

- **アプリバージョン:** v1.0
- **OSバージョン:** Android 13

## 原因と修正内容 (Cause and Resolution)

**原因:**
大会登録・編集ダイアログの日付入力が、キーボードによる手入力が必須の `TextInputEditText` になっていた。

**修正内容:**
1.  `RecordGameDialogFragment` を修正し、`editGameDate` (`EditText`) をクリックした際に、`DatePickerDialog` が表示されるようにした。
2.  ユーザーがカレンダーから選択した日付が、"yyyy/MM/dd" 形式で `EditText` に反映されるようにした。
3.  併せて、`DatePicker` を使用する前提で不要になった、過剰な例外処理 (`try-catch`, `runCatching`) を削除し、コードをクリーンにした。

## 深刻度 (Severity)

- [ ] Critical
- [x] Major (主要なデータ登録フローのUXを著しく損なう)
- [ ] Minor
- [ ] Trivial

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [ ] In Review
- [x] Closed
