AGENTS instructions
===================

This is the repository for sbt's documentation website.
sbt is a build tool targeting the programming language Scala, Java, and more.

Reference manual
----------------

The main reference manual is titled 'The book of sbt'.
The tone should be technical.

### Documentation files

The Markdown file for the original English version is located in `src/reference`. We use mdBook to convert the markdown file to HTML.

### Translation

We use gettext PO files for localization. These are located in `po/summary`. For example, the Japanese PO file for `src/reference/reference/sbt.md` is `po/summary/reference/sbt.ja.po`, and the Chinese PO file for the same is `po/summary/reference/sbt.zh-cn.po`.

When asked to translate a PO file, provide the translation of the `msgid` field in `msgstr` field. For example:

```
#: src/reference/reference/sbt.md:145
msgid "`shutdown` Shuts down the sbt server to end the current sbt session."
msgstr "`shutdown` sbt server をシャットダウンして現行の sbt セッションを終了する。"
```

When translating code or example outputs, use a blank string to indicate that the original should be kept as-is. For example:

```
#: src/reference/reference/sbt.md:284
msgid "\"-Dfile.encoding=Cp1252\""
msgstr ""
```

Tips for Japanese translation
-----------------------------

- Use だ/である tone.
- Use さん suffix after a name of a person.
- Keep whitespaces before and after English words and proper nouns, such as Coursier
- X setup (in header titles) → Xの設定
- See X for details → 詳細はXを参照
- current interactive session → 現行の sbt セッション
- current project → カレント・プロジェクト
- transitive dependencies → 間接依存ライブラリ
- incremental compilation → 差分コンパイル
- dependencies → ライブラリ依存性 (in library dependency context)
- recipe → レシピ
- configuration → コンフィギュレーション (to disambiguate from settings/セッティング)
- cached task → キャッシュ化タスク
- equivalent → 等価
- full Scala version → Scala のフルバージョン
- binary Scala version → Scala のバイナリバージョン
- caching → キャッシュ化
- input task → インプットタスク

Tips for Chinese translation
-----------------------------

- Use 您, but otherwise keep the technical tone.
- Leave "sbt runner" as is
- Leave "sbt server" as is
- setting → 设置
- task → 任务
- requested dependencies → 指定设置依赖 (specified setting dependencies)
- actual dependencies → 有效设置依赖 (effective setting dependencies)
