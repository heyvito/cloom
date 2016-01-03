(ns cloom.clipboard
  (:import (java.awt Toolkit)
           (java.awt.datatransfer StringSelection)))

(defn- get-system-clipboard
  "Returns the clipboard manager for the current system"
  []
  (. (Toolkit/getDefaultToolkit) getSystemClipboard))

(defn set-content
  "Sets the content of the clipboard"
  [value]
  (if value
    (let [clipboard (get-system-clipboard)]
      (if clipboard
        (do
          (let [selection (StringSelection. value)]
            (. clipboard setContents selection nil))
          value)))))
