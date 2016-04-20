(ns cloom.clipboard
  (:import (java.awt Toolkit)
           (java.awt.datatransfer StringSelection))
  (:require [clojure.java.io]
            [clojure.pprint :refer [pp pprint print-table]]))

(defn- get-os-information
  "Returns a map containing current OS information"
  []
  {
    :name (System/getProperty "os.name"),
    :arch (System/getProperty "os.arch")
    :version (System/getProperty "os.version"),
   })

(defn- pbcopy
  "Copies the given value to the system clipboard using pbcopy
  Useful on OS X, but not so reliable on Linux. In the second case,
  we will stick with awt anyway."
  [& [obj]]
  (let [p (.. (Runtime/getRuntime) (exec "pbcopy"))
        o (clojure.java.io/writer (.getOutputStream p))]
    (binding [*out* o] (print (or obj *1)))
    (.close o)
    (.waitFor p)
    obj))

(defn- fallback-copy
  "Copies the given value to the system clipboard using awt"
  [value]
  (let [clipboard (. (Toolkit/getDefaultToolkit) getSystemClipboard)]
    (if clipboard
      (do
        (let [selection (StringSelection. value)]
          (. clipboard setContents selection nil))
        value))))

(defn set-content
  "Sets the content of the clipboard"
  [value]
  (if value
    (cond
      (= "Mac OS X" (:name (get-os-information))) (pbcopy value)
      :else (fallback-copy value))))
