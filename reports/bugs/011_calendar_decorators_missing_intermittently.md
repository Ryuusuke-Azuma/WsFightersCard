# バグ報告: カレンダー表示が不完全になる（今日、選択日、イベントドットが表示されない）

## 概要 (Title)

スケジュール画面の日付選択ボタンを押してカレンダーを開いた際、今日の日付の強調、選択中の日付の枠、およびイベントがある日のドットが時々表示されない。

## 詳細 (Description)

**問題の具体的な内容:**

`CalendarPickerFragment` が `onCreateDialog` で `ScheduleViewModel` の `LiveData` の値を一度だけ参照しているため、データの非同期読み込みが完了する前にダイアログが表示されると、デコレーターが正しく適用されない。

## 再現手順 (Steps to Reproduce)

1. アプリを起動し、スケジュール画面を表示する。
2. 画面上部の日付ボタンをタップしてカレンダーを開く。
3. （タイミングにより）今日の日付の背景や、イベント日のドットが表示されない。一度閉じて開き直すと表示されることがある。

## 本来の挙動 (Expected Behavior)

カレンダーダイアログが表示された後でも、データの読み込みが完了次第、カレンダー上の「今日」「選択日」「イベントドット」が正しく表示されること。

## 実際の挙動 (Actual Behavior)

データの読み込みが完了する前にダイアログが描画されると、初期状態の「何もデコレーションされていないカレンダー」が表示され、その後も更新されない。

## 環境 (Environment)

- **アプリバージョン**: v1.0

## 原因と修正内容 (Cause and Resolution)

### 原因
`CalendarPickerFragment` において、`ScheduleViewModel` の `LiveData` (`markedDates`, `selectedDate`) を `onCreateDialog` 時の一度しか参照しておらず、データの変化を監視していなかった。そのため、非同期でのデータロード完了がダイアログ表示より後になった場合、描画に反映されなかった。

### 修正内容
`CalendarPickerFragment` 内で `LiveData` を監視 (`observe`) するように変更。データがロードまたは更新されたタイミングで `invalidateDecorators()` を呼び出し、カレンダーの表示を動的に更新するようにした。

## 深刻度 (Severity)

- [ ] Critical
- [x] Major (主要なナビゲーション機能の表示不備)
- [ ] Minor
- [ ] Trivial

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Closed
