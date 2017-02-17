(defproject pizza "1.0.0-SNAPSHOT"
  :description "The Pizza Ontology in tawny-owl"
  :dependencies [
                 [uk.org.russet/tawny-owl "1.6.0"]]
  :main pizza.core

  :profiles
  {
   :latest
   [:base {:dependencies [[org.clojure/clojure "1.8.0"]]}]})
