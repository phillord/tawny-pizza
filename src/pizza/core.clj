;; The contents of this file are subject to the LGPL License, Version 3.0.

;; Copyright (C) 2011, Newcastle University

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see http://www.gnu.org/licenses/.


(ns pizza.core
  (:use [tawny.owl] [pizza.pizza])
  (:gen-class)
  )

;; save the ontology in the file named above currently when saved, this
;; ontology is incoherent in protege, as SultanaTopping comes as disjoint from
;; everything. This appears to be a bug in protege -- SultanaTopping comes
;; last before all the disjoint statements. Re-ordering the statements
;; produces a different unsatisfiable class

(defn -main [&args]
  (with-ontology pizzaontology
    (save-ontology)))


;; (main)

