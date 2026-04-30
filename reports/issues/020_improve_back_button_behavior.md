# 課題管理

## 概要 (Title)

Backボタンの挙動改善

## 現状の課題 (Problem)

現在の実装では、ナビゲーションメニューで項目（スケジュール、分析、プロファイル）を選択するたびにバックスタックが蓄積されている。
そのため、アプリを終了するために何度もBackボタンを連打する必要があり、ユーザーエクスペリエンスを損なっている。

## 期待されるゴール (Goal)

- メインメニュー（スケジュール、分析、プロファイル）間の遷移では、バックスタックを蓄積せず、履歴を残さないようにする。
- 設定画面などの詳細画面への遷移時は、元の画面に戻れるように履歴を残す。
- メインメニューのいずれかの画面でBackボタンを押した際、ドロワーが閉じていればスムーズにアプリを終了できる。

## 実装内容 (Implementation)

- `MainNavigation.kt` の `navigateTo` メソッドを修正し、`addToBackStack` が `false` の場合に `supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)` を呼び出してバックスタックをクリアするようにした。
- `MainNavigation.kt` の `onNavigationItemSelected` において、メインメニュー項目（スケジュール、統計、プロフィール）の遷移時に `addToBackStack = false` を指定するように変更した。
- `docs/specification/PROJECT_OVERVIEW.md` に「ナビゲーション・ポリシー」のセクションを追加し、バックスタックの挙動について明文化した。

## 優先度 (Priority)

- [ ] High
- [x] Middle
- [ ] Low

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Closed
