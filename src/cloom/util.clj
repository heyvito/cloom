(ns cloom.util
  (:require [clojure.string :as str]))

(defn- truncate
  "Truncates a given string if its size is larger than expected"
  [text max]
  (if (> (count text) max)
    (str (subs text 0 max) "…")
    text))

(defn fix-width
  "Ensures a given piece of text has a given width"
  [text width]
  (if (< (count text) width)
    (str text (apply str (repeat (- width (count text)) " ")))
    text))

(defn tabulate
  "Neatly tabulates given items"
  [margin items]
  (let [keys (keys items) max-name 15]
      (println (str/join "\n" (map #(str (apply str (repeat margin " "))
                                         (fix-width (str (truncate % max-name) ": ") 18)
                                         (items %))
                         keys))
               "\n")))

(defmacro ensure-arguments
  "Validates provided arguments has a given lenght
  before passing them to its runner, taking a given
  number and applying the items to the function provided
  in the runner key.

  (ensure-arguments {
                      :args [1 2 3]
                      :qty 2
                      :take 2
                      :error \"Oh no!\"
                      :runner #(+ %1 %2)
                    })
  ;; => 3

  (ensure-arguments {
                      :args [1 2 3]
                      :qty 2
                      :take 2
                      :skip 1
                      :error \"Oh no!\"
                      :runner #(+ %1 %2)
                    })
  ;; => 5
  "
  [{:keys [args qty take skip error runner]}]
  `(if (< (count ~args) ~qty)
     (do
       (println ~error)
       (System/exit 1))
     (do
       (let [inp# (if (nil? ~skip) ~args (drop ~skip ~args))]
         (apply ~runner (if (nil? ~take) inp# (take ~take inp#)))))))
