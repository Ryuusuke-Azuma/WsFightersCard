# 課題管理: 不戦勝（Bye）対応の実装

## 概要 (Title)

不戦勝（Bye）を戦績として登録・管理できるようにする。

## 現状の課題 (Problem)

現状のアプリでは、大会等で発生する「不戦勝（Bye）」を正しく記録する手段がない。

## 期待されるゴール (Goal)

- 戦績登録時の「先攻/後攻」の選択肢に「不戦（Bye）」を追加する。
- ユーザーが「不戦」を選択した場合でも、他の項目（相手デッキ、勝敗など）は通常通り入力・選択できる状態とする。
- リスト表示で、先攻/後攻の代わりに「不戦」と表示され、一目でわかる。

## 実装内容 (Implementation)

1. **データベース層の更新**: `FirstSecond` 列挙型に `BYE` (id: 2, label: "bye") を追加。
2. **リソースの追加**: UI選択肢および表示用のラベルとして「不戦」「Bye」を `strings.xml` に追加。
3. **登録UIの修正**: `RecordScoreDialogFragment` において、RadioGroup に「不戦」の選択肢を追加し、保存・復元ロジックを更新。
4. **リスト表示の修正**: `ScoresPageFragment` のアダプターにおいて、`FirstSecond.BYE` の場合に適切なラベルが表示されるように修正。

## 優先度 (Priority)

- [x] High
- [ ] Middle
- [ ] Low

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Closed
