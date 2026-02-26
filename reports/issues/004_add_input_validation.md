# 課題: 入力アシスト機能の実装

## 概要 (Title)

大会登録、および戦績登録のダイアログにおいて、主要なテキスト入力欄に入力アシスト機能を実装する。

## 詳細 (Description)

**現状の課題 (Problem):**

大会名やデッキ名の入力が完全な手入力となっており、入力の手間がかかるだけでなく、表記の揺れが発生し、後のデータ分析の精度を低下させる原因となっている。

**期待されるゴール (Goal):**

- ユーザーの入力負荷を軽減し、素早く正確にデータを登録できるようにする。
- 名前の表記揺れを防ぎ、データの一貫性を向上させる。
- Picker形式のUIを導入し、一貫した操作性を提供する。

## 実装内容 (Implementation)

- **UIコンポーネント**: `TextInputLayout`のエンドアイコンをクリックすることで、候補リストを表示するPicker形式を採用。

- **大会名 (`game_name` in `RecordGameDialogFragment`)**:
  - `TournamentPickerFragment`を新設。
  - `string-array`リソース（`game_name_suggestions`）から候補を取得し、シンプルリスト形式で表示する。

- **デッキ選択 (`battle_deck`, `matching_deck` in `RecordScoreDialogFragment`)**:
  - `DeckPickerFragment`を新設し、ファイター選択とデッキ選択を組み合わせたUIを提供。
  - `DeckPickerViewModel`を介して、自分のデッキ（`is_self`ファイター）または相手のデッキを動的に取得。
  - `FighterItem` (Parcelable) を導入し、UI層での効率的なデータ受け渡しを実現。

## 優先度 (Priority)

- [ ] High 
- [x] Middle (ユーザビリティとデータ品質の向上)
- [ ] Low

## ステータス (Status)

- [ ] Open
- [ ] In Progress
- [x] Closed
