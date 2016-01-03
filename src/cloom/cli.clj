(ns cloom.cli
  (:require [cloom.storage :as storage])
  (:require [cloom.color :as color])
  (:require [cloom.clipboard :as clipboard])
  (:require [clojure.string :as string])
  (:require [cloom.util :as util]
            [clojure.string :as str])
  (:import (clojure.lang PersistentArrayMap)))

(defn remove-item!
  "Removes a specific item from the datafile"
  [group key]
  (storage/save-data (storage/remove-item group key))
  (println (color/style "Cloom!" :cyan) (color/style key :magenta) "is gone forever."))

(defn remove-group!
  "Removes an entire group from the datafile"
  [group]
  (storage/save-data (storage/remove-group group))
  (println (color/style "Cloom!" :cyan) "Removed group" (color/style group :magenta) "and its items. Puf. Gone."))

(defn list-groups
  "Lists all groups and counts their entries"
  []
  (let [data (storage/load-data)]
    (println (->> data
         (keys)
         (map #(str "  " %1 " (" (count (data %1)) ")"))
         (string/join "\n")) "\n")))

(defn list-everything
  "List all groups and their respective keys and values"
  []
  (let [items (storage/load-data)]
    (let [keys (sort (keys items))]
      (doseq [k keys]
         (println " " (color/style k :magenta))
         (util/tabulate 4 (items k))
        ))))

(defn- display-or-copy-item
  "Displays a group or copies a given item to the clipboard"
  [item]
  (when (instance? String item)
    (clipboard/set-content item)
    (println (color/style "Cloom!" :cyan) "Copied" (color/style item :magenta) "to your clipboard.")
    (System/exit 0))
  (when (instance? PersistentArrayMap item)
    (util/tabulate 4 item)
    (System/exit 0)))

(defn process-complex
  "Processes other inputs based on how many arguments are provided"
  [args]
  (case (count args)
    1 (let [item (apply storage/find-item-or-group args)]
        (when (nil? item)
          (println "Cloom could not find a group or item named" (color/style (args 0)))
          (System/exit 1))
        (display-or-copy-item item))
    2 (let [item (apply storage/find-item (+ '((storage/load-data)) args))]
        (when (nil? item)
          (println "Cloom could not find a group named"
                   (color/style (args 0) :magenta)
                   "or an item named"
                   (color/style (args 1) :magenta) "in that group.")
          (System/exit 1)
          )
        (display-or-copy-item item))
    3 (do
        (storage/save-data (apply storage/update-or-add-item args))
        (println (apply #(str (color/style "Cloom!" :cyan) " " %2 " in " %1 " is " %3 ". Got it.")
                        (map #(color/style % :magenta) args))))
    :default (do
               (println "Hold on. There's something wrong with your input. Use cloom help for further information."))))

(defn- do-echo-item
  "Processes a resulting query to the datafile and echoes the result or prints
  an error message"
  [item name]
  (if (instance? String item)
    (do
      (println item)
      (System/exit 0))
    (do
      (println (color/style "Not found:" :red) (color/style name :magenta))
      (System/exit 1))))

(defn echo-item
  "Queries for an item in the datafile and passes echoes the result to the terminal, without copying"
  ([name]
    (let [item (storage/find-item-or-group name)]
      (do-echo-item item name)))
  ([group name]
    (let [item (storage/find-item (storage/load-data) group name)]
      (do-echo-item item name))
    ))

(defn show-help
  "Shows information and lists commands"
  []
  (let [entries [
                 ["cloom"                                "display high-level overview"]
                 ["cloom all"                            "displays all items in all groups"]
                 ["cloom help"                           "this help text"]
                 ["" ""]
                 ["cloom <group>"                        "show items for a group"]
                 ["cloom rm-group <group>"               "deletes a group and its items"]
                 ["" ""]
                 ["cloom <group> <name> <value>"         "create a new group item"]
                 ["cloom <name>"                         "copy item's value to clipboard"]
                 ["cloom <group> <name>"                 "copy item's value to clipboard"]
                 ["cloom rm-item <group> <name>"         "deletes an item"]
                 ["cloom echo <name>"                    "echo the item value without copying"]
                 ["cloom echo <group> <name>"            "echo the item value without copying"]]]
    (let [larger-item (apply max (map count (map first entries)))]
      (println "  - cloom: help ---------------------------------------------------\n")
      (println (str/join "\n" (map #(str "  " (util/fix-width (% 0) (+ larger-item 4)) (% 0)) entries)))
      (println "\n  all other documentation is located at:\n    https://github.com/victorgama/cloom"))))
