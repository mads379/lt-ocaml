(ns lt.plugins.ocaml
  (:require [lt.objs.editor :as editor]
            [lt.objs.editor.pool :as pool]
            [lt.objs.command :as cmd])
  (:require-macros [lt.macros :refer [behavior]]))

(def classy->plain
  {"behaviour" "behavior"})

(defn declassify-string [string]
  (reduce
   (fn [string [classy plain]]
     (.replace string classy plain))
   string
   classy->plain))

(defn utop-paste-region []
  (let [cm (editor/->cm-ed (pool/last-active))
        selected-text (editor/selection cm)]
    (js/alert selected-text)))

(defn declassify-behind-cursor []
  (let [cm (editor/->cm-ed (pool/last-active))
        cursor (.getCursor cm)
        from #js {:line (.-line cursor) :ch 0}
        classy-range (.getRange cm from cursor)
        plain-range (declassify-string classy-range)]
    (when (not= plain-range classy-range)
      (.replaceRange cm plain-range from cursor))))

(defn declassify-editor []
  (let [cm (editor/->cm-ed (pool/last-active))
        cursor (.getCursor cm)]
    (doseq [[classy plain] classy->plain]
      (js/CodeMirror.commands.find cm classy false)
      (js/CodeMirror.commands.replace cm plain false true))
    (.setCursor cm cursor)))

(cmd/command {:command :declassify-editor
              :desc "OCaml: Replace classy words in the current editor"
              :exec declassify-editor})

(cmd/command {:command :declassify-behind-cursor
              :desc "OCaml: Replace classy words behind the cursor"
              :exec declassify-behind-cursor})

(cmd/command {:command :utop-paste-region
              :desc "OCaml: Eval Selection"
              :exec utop-paste-region})
