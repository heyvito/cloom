(ns cloom.storage
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json])
  (:import [java.io File]))

(let [homedir (io/file (System/getProperty "user.home"))
      usersdir (.getParent homedir)]
  (defn home
    "With no arguments, returns the current value of the `user.home` system
     property. If a `user` is passed, returns that user's home directory. It
     is naively assumed to be a directory with the same name as the `user`
     located relative to the parent of the current value of `user.home`."
    ([] homedir)
    ([user] (if (empty? user)
              homedir
              (io/file usersdir user)))))

(defn expand-home
  "If `path` begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the `path` begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`."
  [path]
  (let [path (str path)]
    (if (.startsWith path "~")
      (let [sep (.indexOf path File/separator)]
        (if (neg? sep)
          (home (subs path 1))
          (io/file (home (subs path 1 sep)) (subs path (inc sep)))))
      path)))

(let [cloomfile (expand-home "~/.cloom")]
  (defn load-data
    "Loads cloom datafile into a map"
    []
    (if (.exists (io/file cloomfile))
      (json/read-str (slurp cloomfile))
      {})))

(defn find-item
  "finds an item on the cloom datafile"
  ([data key]
   (get (->> data
            (vals)
            (vec)
            (apply conj)) key))
  ([data group key]
   (let [g (data group)]
     (if-not (nil? g)
       (g key)))))


(defn find-group
  "finds a group on the cloom datafile"
  [data key]
  (get data key))

(defn find-item-or-group
  "finds a given key on the cloom datafile
  first, searches for keys, if no key is found, returns the whole group"
  [key]
  (let [data (load-data)]
    (or (find-item data key) (find-group data key))))

(defn update-or-add-item
  "Adds or updates a new item. Also creates a new group, if none is found"
  [group key value]
  (assoc-in (load-data) [group key] value))

(defn remove-item
  "Removes an item from a group"
  [group key]
  (dissoc (load-data) [group key]))

(defn remove-group
  "Removes an entire group from the datafile"
  [group]
  (dissoc (load-data) group))

(let [cloomfile (expand-home "~/.cloom")]
  (defn save-data
    "Stores a given value into the cloom datafile"
    [value]
    (spit cloomfile (json/write-str value))))
