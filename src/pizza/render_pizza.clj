(ns pizza.render-pizza
  (:require [tawny.owl]
            [pizza.pizza]
            [tawny.render]))

(defn render-pizza []
  (with-open
      [r 
       (java.io.PrintWriter.
        (java.io.FileWriter. "target/rendered-pizza.clj"))]
    (println r ";; Rendered by tawny")
    (doseq
        [e (.getSignature pizza.pizza/pizzaontology)]
      (println "Rendering:" e)
      ;;binding [*ns* (find-ns 'pizza.pizza)]
      (.println r
                (tawny.render/as-form e)))))
