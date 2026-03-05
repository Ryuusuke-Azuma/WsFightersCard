# バグ報告: カレンダーピッカーを開いた際、選択日の月が表示されない

## 概要 (Title)

スケジュール画面で日付を選択した後、再度カレンダーピッカーを開くと、選択した日付がある月ではなく、常に「今日」の日付がある月が表示されてしまう。

## 再現手順 (Steps to Reproduce)

1. スケジュール画面を開く。
2. 日付ボタンをタップしてカレンダーを開き、今日とは別の月（例: 2ヶ月前）の日付を選択して閉じる。
3. 再度日付ボタンをタップしてカレンダーを開く。
4. カレンダーが選択した日付（2ヶ月前）の月ではなく、今日の月を表示している。

## 本来の挙動 (Expected Behavior)

カレンダーピッカーを開いた際、現在選択されている日付 (`selectedDate`) が含まれる月が初期表示されるべき。

## 実際の挙動 (Actual Behavior)

`setSelectedDate` で日付の選択状態は更新されているが、カレンダーの表示位置（カレント月）が「今日」にリセット、または維持されている。

## 環境 (Environment)

- **アプリバージョン:** v1.0

## 原因と修正内容 (Cause and Resolution)

### 原因
`MaterialCalendarView` の `setSelectedDate` は「選択状態」にするだけで、表示位置（月）をそこへ移動させる機能はない。表示位置を指定するには `setCurrentDate` を明示的に呼び出す必要がある。

### 修正内容
`CalendarPickerFragment` の `setupCalendarView` 内で、`setSelectedDate` と同時に `setCurrentDate` を呼び出し、選択された日付の月を表示するように修正した。

## 深刻度 (Severity)

- [ ] Critical
- [ ] Major
- [x] Minor (ユーザー体験を損なうが、機能の利用は可能)
- [ ] Trivial

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Closed
