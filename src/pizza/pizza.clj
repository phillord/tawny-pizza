;; The contents of this file are subject to the LGPL License, Version 3.0.

;; Copyright (C) 2013, Phillip Lord, Newcastle University

;; This program is free software: you can redistribute it and/or modify it
;; under the terms of the GNU Lesser General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or (at your
;; option) any later version.

;; This program is distributed in the hope that it will be useful, but WITHOUT
;; ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
;; FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
;; for more details.

;; You should have received a copy of the GNU Lesser General Public License
;; along with this program. If not, see http://www.gnu.org/licenses/.



;; define a name space. Use owl.owl without requiring namespace qualification
(ns pizza.pizza
  (:use [tawny.owl])
  (:require [tawny
             [read]
             [polyglot]
             [reasoner :as r]
             [pattern :as p]]))



;; create a new ontology with the values specified. The ontology will be
;; available from the var pizzaontology for use within the `with-ontology'
;; macro. Alternatively, it will be used for all operations inside the current
;; namespace. If any of the forms after this as eval'd before this one,
;; clojure-owl will crash with a suitable warning message.
(defontology pizzaontology
  :iri "http://www.ncl.ac.uk/pizza"
  :prefix "piz:"
  :comment "An example ontology modelled on the Pizza tutorial ontology from
Manchester University, written using the tawny-owl library"
  :versioninfo "Unreleased Version"
  :seealso "Manchester Version"
  )



(defaproperty myOpinion
  :subproperty owl-comment-property
  :label "My Opinion"
  :comment "Do I think this is a good pizza to eat?"
  )

(defclass Pizza
  :label "Pizza")

(defclass PizzaComponent)

;; these classes are all siblings and should all be disjoint
;; using the as-disjoint macro makes things a little easier.
(as-disjoint
 ;; we use :label here as it is easier and more straight forward


 (defclass PizzaTopping)
 ;; currently we have to use the annotation function with label to pass a
 ;; language in.
 (defclass PizzaBase
   ;; the pizza ontology contains some Portuguese labels. The :label keyword
   ;; used above is a shortcut for English
   :annotation (label "BaseDaPizza" "pt")))

;; now that we have our first classes we can specify our properties
(as-inverse
 (defoproperty hasIngredient
   :characteristic :transitive)
 (defoproperty isIngredientOf
   :characteristic :transitive
   ))

(defoproperty hasTopping
  :subproperty hasIngredient
  :range PizzaTopping
  :domain Pizza
  )

(defoproperty hasBase
  :subproperty hasIngredient
  :characteristic :functional
  :range PizzaBase
  :domain Pizza
  )


(defdproperty hasCalorificContentValue
  :range :XSD_INTEGER)

(defn cal [number]
  (has-value hasCalorificContentValue number))

(owl-class Pizza
          :super
          (owl-some hasCalorificContentValue :XSD_INTEGER)
          (owl-some hasTopping PizzaTopping)
          (owl-some hasBase PizzaBase))


;; define a set of subclasses which are all mutually disjoint
(as-disjoint-subclasses
 PizzaBase
 
 (defclass ThinAndCrispyBase
   :super (cal 150)
   :annotation (label "BaseFinaEQuebradica" "pt"))

 (defclass DeepPanBase
   :super (cal 250)
   :annotation (label  "BaseEspessa" "pt")))

(p/defpartition
  Spiciness
 [Mild
  Medium
  Hot]
 )


(as-disjoint-subclasses
 PizzaTopping

 ;; This section used to reflect the natural hierarchy within the lisp, by
 ;; embedding multiple 'as-disjoint-subclasses' forms. In the end, I have
 ;; unwound this for two reasons. First, the implementation used dynamic
 ;; binding and scoping in a way that I was not entirely happy with; second,
 ;; the deep embedding means that small.
 (defclass CheeseTopping)
 (defclass FishTopping)
 (defclass FruitTopping)
 (defclass HerbSpiceTopping)
 (defclass MeatTopping)
 (defclass NutTopping)
 (defclass SauceTopping)
 (defclass VegetableTopping))

(as-disjoint-subclasses
 CheeseTopping

 (declare-classes
  GoatsCheeseTopping
  GorgonzolaTopping
  MozzarellaTopping
  ParmesanTopping))

(as-disjoint-subclasses
 FishTopping

 (declare-classes AnchoviesTopping
                  MixedSeafoodTopping
                  PrawnsTopping))

(as-disjoint-subclasses
 FruitTopping
 (declare-classes PineappleTopping
                  SultanaTopping))

(as-disjoint-subclasses
 HerbSpiceTopping

 (declare-classes CajunSpiceTopping
                  RosemaryTopping))

(as-disjoint-subclasses
 MeatTopping

 (declare-classes ChickenTopping
                  HamTopping
                  HotSpicedBeefTopping
                  PeperoniSausageTopping))


(as-subclasses
 NutTopping
 (defclass PineKernels))

(as-subclasses
 SauceTopping
 (defclass TobascoPepperSauce))

(as-disjoint-subclasses
 VegetableTopping

 (declare-classes PepperTopping
                  GarlicTopping
                  PetitPoisTopping
                  AsparagusTopping
                  CaperTopping
                  SpinachTopping
                  ArtichokeTopping
                  OnionTopping
                  OliveTopping
                  MushroomTopping
                  RocketTopping
                  TomatoTopping
                  LeekTopping))

(as-disjoint-subclasses
 PepperTopping
 (declare-classes PeperonataTopping
                  JalapenoPepperTopping
                  SweetPepperTopping
                  GreenPepperTopping))

;; equivalent classes -- these are the main categories which will be reasoned under. 
(defclass CheesyPizza
  :equivalent
  (owl-and Pizza
          (owl-some hasTopping CheeseTopping)))

(defclass InterestingPizza
  :equivalent
  (owl-and Pizza
          (at-least 3 hasTopping PizzaTopping)))

(defclass FourCheesePizza
  :equivalent
  (owl-and Pizza
          (exactly 4 hasTopping CheeseTopping)))

(defclass VegetarianPizza
  :annotation 
  (annotation myOpinion "Always a good start.")
  :equivalent
  (owl-and Pizza
          (owl-not 
           (owl-some hasTopping MeatTopping))
          (owl-not 
           (owl-some hasTopping FishTopping))))

(defclass NonVegetarianPizza
  :annotation 
  (annotation myOpinion "Not a good start.")
  :equivalent
  (owl-and Pizza (owl-not VegetarianPizza)))

;; different, but equivalent, definition
(defclass VegetarianPizza2
  :equivalent 
  (owl-and Pizza
          (only hasTopping 
                (owl-not (owl-or MeatTopping FishTopping)))))


(defclass HighCaloriePizza
  :equivalent
  (owl-some hasCalorificContentValue
           (span >= 700)))

(defclass MediumCaloriePizza
  :equivalent
  (owl-some hasCalorificContentValue
           (span >=< 400 700)))

(defclass LowCaloriePizza
  :equivalent
  (owl-some hasCalorificContentValue
           (span <= 400)))

;; named pizzas
(defclass NamedPizza
  :super Pizza)

;; as well as "normal" usage, tawny is also fully programmatic.

;; we can also add ingredients using strings. This version is very easy, and
;; just uses a map. In practice, if you want to do this, you would probably be
;; reading the strings from outside of this source file -- a database, or
;; spreadsheet. We must also use doall because map returns a sequence which is
;; lazy in Clojure, which doesn't work with the Java underneath.
(doseq
    [n ["Carrot"
        "CherryTomatoes"
        "KalamataOlives"
        "Lettuce"
        "Peas"]]
  (owl-class (str n "Topping") :super VegetableTopping))

;; The problem with the approach above is that while the classes will be
;; created, can affect reasoning and will be saved to file, we cannot refer to
;; these as Clojure variables. So,
;;
;; (defclass MushyPeas :super Peas)
;;
;; would fail, and we would have to use
;;
;; (defclass MushyPeas :super "Peas")
;;
;; This is probably fine if, for instance, you are pulling individuals in from
;; a file. But it might not be good in other circumstances when you also want
;; clojure vars. There are a variety of ways to achieve this, but the easiest
;; is probably with tawny.read/intern-entity. intern-entity doesn't care what
;; kind of thing you are interning, so long as it is an OWLNamedObject.


;; not testing this yet!!!!
(doseq
    [n
     ["ChilliOil"
      "Chives"
      "Chutney"
      "Coriander"
      "Cumin"
      "Basil"
      ]]
  (tawny.read/intern-entity
   (owl-class (str n "Topping") :super VegetableTopping)))



;; This should all work now. So we can do something like define a curry pizza
;; (defclass CurryPizza
;;   :super Pizza
;;   (owlsome hasTopping Coriander Cumin Chutney))

;; as well as taking care of some book-keeping, intern-entity is quite
;; flexible in how it creates the variable name. The default case just uses
;; the fragment from the IRI, but it can also use labels, suitable transformed
;; for stop characters. In the default case, this makes little difference
;; because most illegal characters in IRI fragments are also illegal in
;; clojure: spaces are a good example.
;;(defclass ChilliHot
;;  :equivalent (owlsome hasTopping ChilliOil))


;; Finally, we can generate arbitrarily complex statements. this is a one-off
;; function that is unlikely to be much use for more general purposes.
(defn generate-named-pizza [& pizzalist]
  (doseq
      [[named & toppings] pizzalist]
    (owl-class
     named
     :super (some-only hasTopping toppings))))

;; define all the named pizzas. We could get away without doing this, but then
;; we would need to replace generate-named-pizza with a macro, and life is too
;; short. We could also get around this by using a string for the pizza name.
(as-disjoint-subclasses
 NamedPizza
 (declare-classes MargheritaPizza CajunPizza CapricciosaPizza
                  SohoPizza ParmensePizza))

(generate-named-pizza
 [MargheritaPizza MozzarellaTopping TomatoTopping]

 [CajunPizza MozzarellaTopping OnionTopping PeperonataTopping
  PrawnsTopping TobascoPepperSauce TomatoTopping]

 [CapricciosaPizza AnchoviesTopping MozzarellaTopping
  TomatoTopping PeperonataTopping HamTopping CaperTopping
  OliveTopping]

 [ParmensePizza AsparagusTopping
  HamTopping
  MozzarellaTopping
  ParmesanTopping
  TomatoTopping]

 [SohoPizza OliveTopping RocketTopping TomatoTopping ParmesanTopping
  GarlicTopping]
 )


(defindividual ExampleMargheritaPizza
  :type MargheritaPizza
  :fact (is hasCalorificContentValue 300))


(defindividual ExampleParmense
  :type ParmensePizza
  :fact (is hasCalorificContentValue 700))


;; adding spiciness
;; (subclasses PizzaTopping)
(defn spiciness
  [& arg]
  (let [top (first arg)
        spiciness (second arg)
        rest (-> arg rest rest)]
    (add-subclass top
                  (owl-some hasSpiciness spiciness))
    (when-not (= 0 (count rest))
      (recur rest))))


(spiciness
 TobascoPepperSauce Hot
 RocketTopping Mild
 )

;; create an empty resource for translation
;; save in an absolute location
;;(tawny.polyglot/polyglot-create-resource "src/pizza/pizzalabel_it.properties")

;; load labels from resource
;; this is found anywhere in the classpath incllude ./src and ./resources
(tawny.polyglot/polyglot-load-label
 "pizza/pizzalabel_it.properties" "it")

;; this should be dublin core really
(defaproperty creator
  :subproperty owl-comment-property
  )

;; add myself as a creator to everything. Not everything works at the moment,
;; because I haven't coded it all!
(doseq
    [e (.getSignature pizzaontology)
     :while
     (and (named-object? e)
          (.startsWith
           (.toString (.getIRI e))
           "http://www.ncl.ac.uk/pizza"))]
  (try
    (println "refining:" e)
    (refine e :annotation (annotation creator "Phillip Lord"))
    (catch Exception exp
      (printf "Problem with entity: %s :%s\n" e exp)
      )))


;; ontologies save into the default directory, which is the top leve of the project

;; save the ontology in Manchester syntax because this is the nicest to read and
;; the best way to check what you have done
(save-ontology "pizza.omn" :omn)

;; save the ontology in OWL XML syntax because Manchester syntax doesn't
;; roundtrip at the moment, this is will be read into protege
(save-ontology "pizza.owl" :owl)

(save-ontology "pizza.turtle" (org.coode.owlapi.turtle.TurtleOntologyFormat.))

;; (r/reasoner-factory :hermit)
;; (r/unsatisfiable)
;; (r/coherent?)
;; (r/consistent?)

;;(require 'tawny.repl)
;;(tawny.repl/update-ns-doc *ns*)
;;(println "Doc complete")

