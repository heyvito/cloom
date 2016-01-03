(ns cloom.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [cloom.cli :as cli]
            [cloom.util :as util])
  (:gen-class))

(defn -main
  [& args]
  (if (= (count args) 0)
     (do
       (cli/list-groups)
       (System/exit 0)))
  (if (= "default" (case (first args)
    "help" (cli/show-help)
    "all" (cli/list-everything)
    "rm-item" (util/ensure-arguments {
                                     :args args
                                     :qty 3
                                     :take 2
                                     :skip 1
                                     :error "Incorrect usage. Example: rm-item <group> <name>"
                                     :runner cli/remove-item!
                                     })
    "rm-group" (util/ensure-arguments {
                                      :args args
                                      :qty 2
                                      :take 1
                                      :skip 1
                                      :error "Incorrect usage. Example: rm-group <group>"
                                      :runner cli/remove-group!
                                      })
    "echo" (util/ensure-arguments {
                                   :args args
                                   :qty 2
                                   :take 2
                                   :skip 1
                                   :error "Incorrect usage. Example: echo [<group>] <name>"
                                   :runner cli/echo-item
                                   })
    "default"))
    (cli/process-complex args)))
