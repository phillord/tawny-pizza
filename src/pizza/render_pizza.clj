(ns pizza.render-pizza
  (:require [tawny.owl]
            [pizza.pizza :as p]
            [tawny.render]))

(defn render-pizza []

                 (tawny.owl/save-ontology p/pizzaontology "pizza.rdf" :rdf)
                 (tawny.owl/save-ontology p/pizzaontology "pizza.owl" :owl))
