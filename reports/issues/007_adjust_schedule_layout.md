# 課題: スケジュール画面のUIをカレンダーピッカーに変更

## 概要 (Title)

スケジュール画面のUIを刷新し、常時表示されていたカレンダービューを削除して、日付選択ボタンとカレンダーピッカー（`DatePickerDialog`）に置き換える。

## 現状の課題 (Problem)

現在のスケジュール画面では、カレンダービューが画面の大部分を占めており、リスト表示エリアが狭くなっている。また、カレンダー自体の主張が強く、UI全体のバランスを損ねていた。

## 期待されるゴール (Goal)

- スケジュール画面のUIがシンプルになり、リスト表示エリアが広がることで、大会や戦績の視認性が向上した。
- 日付の選択は、日付表示ボタンをタップして表示されるカレンダーダイアログで行うようになり、操作性が統一された。

## 実装内容 (Implementation)

- `fragment_schedule.xml` から `MaterialCalendarView` を削除し、日付選択ボタン (`btn_target_date`) を配置した。
- カレンダーダイアログ専用のレイアウト `dialog_widget_calendar.xml` を作成した。
- `CalendarPickerFragment` を `schedule` パッケージ配下の `widget` パッケージに作成し、カレンダーの表示と日付選択のロジックを分離した。
- `ScheduleFragment` で、日付選択ボタンがクリックされたときに `CalendarPickerFragment` を表示し、選択された日付を `ScheduleViewModel` に通知するように修正した。
- `layout-land` のレイアウトも同様に修正し、View Bindingが生成するプロパティのnull許容問題を解決した。

## 優先度 (Priority)

- [ ] High
- [x] Middle
- [ ] Low

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Done
