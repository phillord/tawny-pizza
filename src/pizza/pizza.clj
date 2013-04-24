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
  :comment "An example ontology modelled on the Pizza tutorial ontology from Manchester University, 
written using the ptawny-owl library"
  :versioninfo "Unreleased Version"
  :annotation (seealso "Manchester Version"))



;; these classes are all siblings and should all be disjoint
;; using the as-disjoint macro makes things a little easier. 
(as-disjoint
 ;; we use :label here as it is easier and more straight forward
 
 (defclass Pizza
   :label "Pizza")


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
   :characteristics transitive)
 (defoproperty isIngredientOf
   :characteristics transitive
   ))

(defoproperty hasTopping
  :subpropertyof hasIngredient
  :range PizzaTopping
  :domain Pizza
  )

(defoproperty hasBase
  :subpropertyof hasIngredient
  :characteristics functional
  :range PizzaBase
  :domain Pizza
  )



;; define a set of subclasses which are all mutually disjoint
(as-disjoint-subclasses
 PizzaBase
 
 (defclass ThinAndCrispyBase
   :annotation (label "BaseFinaEQuebradica" "pt"))

 (defclass DeepPanBase
   :annotation (label  "BaseEspessa" "pt")))

(p/value-partition
 Spiciness
 [Mild
  Medium
  Hot]
 )


(as-disjoint-subclasses
 PizzaTopping

 ;; I have used defclass here so that I can put the subclases next.
 ;; I could also have used declare-classes and declared all the children of
 ;; PizzaTopping directly, but I like that the lisp brackets reflect the
 ;; natural hierarchy here.
 (defclass CheeseTopping)
 
 (as-disjoint-subclasses
  CheeseTopping

  (declare-classes
   GoatsCheeseTopping
   GorgonzolaTopping
   MozzarellaTopping
   ParmesanTopping))

 (defclass FishTopping)

 (as-disjoint-subclasses
  FishTopping

  (declare-classes AnchoviesTopping
                   MixedSeafoodTopping
                   PrawnsTopping))
 

 (defclass FruitTopping)
 (as-disjoint-subclasses
  FruitTopping

  (declare-classes PineappleTopping
                   SultanaTopping))

 
 (defclass HerbSpiceTopping)
 (as-disjoint-subclasses
  HerbSpiceTopping
  
  (declare-classes CajunSpiceTopping
                   RosemaryTopping))
 
 (defclass MeatTopping)
 (as-disjoint-subclasses
  MeatTopping

  (declare-classes ChickenTopping
                   HamTopping
                   HotSpicedBeefTopping
                   PeperoniSausageTopping)
  )


 (defclass NutTopping)
 
 ;; In a way this does not make sense -- there is only a single disjoint class
 ;; here. However, this as-disjoint-subclasses macro shields PineKernels from
 ;; the as-disjoint macro in which it is contained. Without it, PineKernel
 ;; would become disjoint from it superclass.
 ;;
 ;; OWL2 treats a single disjoint axiom as illegal, so it is dealt with
 ;; specially by clojure-owl. I feel this makes sense as it expresses the
 ;; declarative intent of the developer better, which is likely to mean "these
 ;; classes AND any that I add in future, are disjoint.
 (as-disjoint-subclasses
  NutTopping
  (defclass PineKernels))

 
 
 (defclass SauceTopping)
 ;;
 (as-disjoint-subclasses 
  SauceTopping
  (defclass TobascoPepperSauce))
 
 
 (defclass VegetableTopping)

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
                   LeekTopping)

  (as-disjoint-subclasses
   PepperTopping
   (declare-classes PeperonataTopping
                    JalapenoPepperTopping
                    SweetPepperTopping
                    GreenPepperTopping))))

;; equivalent classes -- these are the main categories which will be reasoned under. 
(defclass CheesyPizza
  :equivalent
  (owland Pizza
           (owlsome hasTopping CheeseTopping)))

(defclass InterestingPizza
  :equivalent
  (owland Pizza
          (atleast 3 hasTopping PizzaTopping)))

(defclass FourCheesePizza
  :equivalent
  (owland Pizza
          (exactly 4 hasTopping CheeseTopping)))

(defclass VegetarianPizza
  :equivalent
  (owland Pizza
          (owlnot 
           (owlsome hasTopping MeatTopping))
          (owlnot 
           (owlsome hasTopping FishTopping))))

(defclass NonVegetarianPizza
  :equivalent
  (owland Pizza (owlnot VegetarianPizza)))

;; different, but equivalent, definition
(defclass VegetarianPizza2
  :equivalent 
  (owland Pizza
          (only hasTopping 
                (owlnot (owlor MeatTopping FishTopping)))))



;; named pizzas 
(defclass NamedPizza
  :subclass Pizza)

;; as well as "normal" usage, tawny is also fully programmatic.

;; we can also add ingredients using strings. This version is very easy, and
;; just uses a map. In practice, if you want to do this, you would probably be
;; reading the strings from outside of this source file -- a database, or
;; spreadsheet. We must also use doall because map returns a sequence which is
;; lazy in Clojure, which doesn't work with the Java underneath.
(doall
 (map
 #(owlclass % :subclass VegetableTopping)
 ["Carrot"
  "CherryTomatoes"
  "KalamataOlives"
  "Lettuce"
  "Peas"]))

;; The problem with the approach above is that while the classes will be
;; created, can affect reasoning and will be saved to file, we cannot refer to
;; these as Clojure variables. So,
;;
;; (defclass MushyPeas :subclass Peas)
;;
;; would fail, and we would have to use
;;
;; (defclass MushyPeas :subclass "Peas")
;;
;; This is probably fine if, for instance, you are pulling individuals in from
;; a file. But it might not be good in other circumstances when you also want
;; clojure vars. There are a variety of ways to achieve this, but the easiest
;; is probably with tawny.read/intern-entity. intern-entity doesn't care what
;; kind of thing you are interning, so long as it is an OWLNamedObject.

(doall
 (map
 #(tawny.read/intern-entity
   (owlclass % :subclass VegetableTopping))
   ["ChilliOil"
    "Chives"
    "Chutney"
    "Coriander"
    "Cumin"
    ]))

;; This should all work now. So we can do something like define a curry pizza
(defclass CurryPizza
  :subclass Pizza
  (owlsome hasTopping Coriander Cumin Chutney))

;; as well as taking care of some book-keeping, intern-entity is quite
;; flexible in how it creates the variable name. The default case just uses
;; the fragment from the IRI, but it can also use labels, suitable transformed
;; for stop characters. In the default case, this makes little difference
;; because most illegal characters in IRI fragments are also illegal in
;; clojure: spaces are a good example.
(defclass ChilliHot
  :equivalent (owlsome hasTopping ChilliOil))


;; Finally, we can generate arbitrarily complex statements.
;; this is a one-off function that is unlikely to be much use for more general purposes. 
(defn generate-named-pizza [& pizzalist]
  (doall
   (map
    (fn [[namedpizza & toppings]]
      (add-subclass
       namedpizza
       ;; use apply because we have a single list, someonly expects a list of
       ;; arguments.
       (apply someonly
              ;; toppings is alread a list!
              (cons hasTopping toppings))))
    pizzalist)))


;; define all the named pizzas. We could get away without doing this, but then
;; we would need to replace generate-named-pizza with a macro, and life is too
;; short. We could also get around this by using a string for the pizza name. 
(as-disjoint-subclasses
 NamedPizza
 (declare-classes MargheritaPizza CajunPizza CapricciosaPizza SohoPizza Parmense))

(generate-named-pizza
 [MargheritaPizza MozzarellaTopping TomatoTopping]

 [CajunPizza MozzarellaTopping OnionTopping PeperonataTopping
  PrawnsTopping TobascoPepperSauce TomatoTopping]

 [CapricciosaPizza AnchoviesTopping MozzarellaTopping
  TomatoTopping PeperonataTopping HamTopping CaperTopping
  OliveTopping]

 [Parmense AsparagusTopping
  HamTopping
  MozzarellaTopping
  ParmesanTopping
  TomatoTopping]

 [SohoPizza OliveTopping RocketTopping TomatoTopping ParmesanTopping
  GarlicTopping]

 )


;; adding spiciness
;; (subclasses PizzaTopping)
(defn spiciness
  [& arg]
  (let [top (first arg)
        spiciness (second arg)
        rest (-> arg rest rest)]
    (add-subclass top
                  (owlsome hasSpiciness spiciness))
    (when-not (= 0 (count rest))
      (recur rest))))


(spiciness
 TobascoPepperSauce Hot
 RocketTopping Mild
 )

;;(defdproperty hasCalorificContentValue)


;; create an empty resource for translation
;; save in an absolute location
;;(tawny.polyglot/polyglot-create-resource "src/pizza/pizzalabel_it.properties")

;; load labels from resource
;; this is found anywhere in the classpath incllude ./src and ./resources
(tawny.polyglot/polyglot-load-label 
  "pizza/pizzalabel_it.properties" "it")





;; ontologies save into the default directory, which is the top leve of the project

;; save the ontology in Manchester syntax because this is the nicest to read and
;; the best way to check what you have done
(save-ontology "pizza.omn" :omn)

;; save the ontology in OWL XML syntax because Manchester syntax doesn't
;; roundtrip at the moment, this is will be read into protege
(save-ontology "pizza.owl" :owl)


;; (r/reasoner-factory :hermit)
;; (r/unsatisfiable)
;; (r/coherent?)
;; (r/consistent?)
