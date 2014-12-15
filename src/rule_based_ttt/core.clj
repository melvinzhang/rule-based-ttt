(ns rule-based-ttt.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.edn :as edn])
  (:gen-class))

; board
;
;    0 1 2
;    -----
; 0 |
; 1 |
; 2 |

(defn other [s]
  (if (= s 'X) 'O 'X))

(defn move [board p s]
  (let [moves (:move board)]
    (-> board
      (assoc p s)
      (assoc :move (assoc moves p (count moves))))))

(defn row-won [r s board]
   (= s
      (get board [r 0]) 
      (get board [r 1])
      (get board [r 2])))

(defn col-won [c s board]
   (= s
      (get board [0 c]) 
      (get board [1 c])
      (get board [2 c])))

(defn diag-won [s board]
  (or 
    (= s
      (get board [0 0]) 
      (get board [1 1])
      (get board [2 2]))
   (= s
      (get board [2 0]) 
      (get board [1 1])
      (get board [0 2]))))

(defn won [s board]
  (or (diag-won s board)
      (row-won 0 s board)
      (row-won 1 s board)
      (row-won 2 s board)
      (col-won 0 s board)
      (col-won 1 s board)
      (col-won 2 s board)))

(defn done [board]
  (or (won 'X board)
      (won 'O board)
      (= (count (board :move)) 9)))

(defn show [board]
  (let [moves (:move board)]
    (print "\n")
    (print (str (get board [0 0] " ") (get moves [0 0] " ") " "))
    (print (str (get board [0 1] " ") (get moves [0 1] " ") " "))
    (print (str (get board [0 2] " ") (get moves [0 2] " ") " "))
    (print "\n")
    (print (str (get board [1 0] " ") (get moves [1 0] " ") " "))
    (print (str (get board [1 1] " ") (get moves [1 1] " ") " "))
    (print (str (get board [1 2] " ") (get moves [1 2] " ") " "))
    (print "\n")
    (print (str (get board [2 0] " ") (get moves [2 0] " ") " "))
    (print (str (get board [2 1] " ") (get moves [2 1] " ") " "))
    (print (str (get board [2 2] " ") (get moves [2 2] " ") " "))
    (print "\n")
    (print "\n")))

; basic AI
; 1. win if self have two symbols in a row
; 2. block if opponent have two symbols in a row
; 3. else put in preference
(defn basic-prefs [s board prefs]
  (let [pos (for [x (range 0 3) y (range 0 3)] [x y])
        avail (filter #(nil? (get board %)) pos)
        t (other s)
        win (filter #(won s (move board % s)) avail)
        block (filter #(won t (move board % t)) avail)
        pref (filter #(nil? (get board %)) prefs)]
    (cond
      (not-empty win) (move board (first win) s)
      (not-empty block) (move board (first block) s)
      :else (move board (first pref) s))))

(defn arr-to-labeled-prefs [arr]
  (for [r (range 0 3) c (range 0 3)]
    [(get-in arr [r c]) [r c]]))

(defn arr-to-prefs [arr]
  (->> arr
       arr-to-labeled-prefs
       sort
       (map #(get % 1))))

(defn basic-arr-prefs [arr]
  (fn [s board] (basic-prefs s board (arr-to-prefs arr))))

(def basic (basic-arr-prefs
  [[2 7 3]
   [6 0 8]
   [1 5 4]]))

(def basic2 
  (basic-arr-prefs
  [[3 7 2]
   [6 0 8]
   [1 5 4]]))

; lift a function p from board -> board to board -> [board]
(defn lift [p s]
  (fn [board]
    (if (done board) [board] [(p s board)])))

(defn exhaust [s]
  (fn [board]
    (if (done board)
      [board] 
      (let [pos (for [x (range 0 3) y (range 0 3)] [x y])
            avail (filter #(nil? (get board %)) pos)
            t (other s)
            win (filter #(won s (move board % s)) avail)
            block (filter #(won t (move board % t)) avail)]
        (cond
          (not-empty win) [(move board (first win) s)]
          (not-empty block) [(move board (first block) s)]
          :else (for [p avail] (move board p s)))))))

(defn gen-end-tree [p1 p2 board]
  (if (done board) 
    board
    (for [b (p1 board)]
      (gen-end-tree p2 p1 b))))

(defn gen-end-state [p1 p2 board]
  (flatten (gen-end-tree p1 p2 board)))

(defn gen-first [p]
  (gen-end-state (lift p 'X) (exhaust 'O) {:move {}}))

(defn gen-second [p]
  (gen-end-state (exhaust 'X) (lift p 'O) {:move {}}))

(defn gen-first-fail [p]
  (filter #(won 'O %) (gen-first p)))

(defn gen-second-fail [p]
  (filter #(won 'X %) (gen-second p)))

(defn show-first-fail [arr]
  (map show (gen-first-fail (basic-arr-prefs arr))))

(defn show-second-fail [arr]
  (map show (gen-second-fail (basic-arr-prefs arr))))

(defn stats [p]
  (let [as-first (gen-first p)
        as-second (gen-second p)
        win-first   (count (filter #(won 'X %) as-first))
        lose-first  (count (filter #(won 'O %) as-first))  
        draw-first  (- (count as-first) win-first lose-first)
        win-second  (count (filter #(won 'O %) as-second)) 
        lose-second (count (filter #(won 'X %) as-second)) 
        draw-second (- (count as-second) win-second lose-second)]
    {:total
      {:win (+ win-first win-second)
       :lose (+ lose-first lose-second)
       :draw (+ draw-first draw-second)}
     :as-first 
      {:win win-first
       :lose lose-first 
       :draw draw-first}
     :as-second
      {:win win-second
       :lose lose-second
       :draw draw-second}}))

(defn check [arr]
  (let [s (stats (basic-arr-prefs arr))]
    (println "Testing AI as first player")
    (println "win:" (get-in s [:as-first :win]))
    (println "lose:" (get-in s [:as-first :lose]))
    (println "draw:" (get-in s [:as-first :draw]))
    (println "Testing AI as second player")
    (println "win:" (get-in s [:as-second :win]))
    (println "lose:" (get-in s [:as-second :lose]))
    (println "draw:" (get-in s [:as-second :draw]))))

(def perms (combo/permutations [[1 1] [2 0] [0 0] [0 2] [2 2] [2 1] [1 0] [0 1] [1 2]]))

(defn -main
  [& args]
  (let [proc (fn [perm] {:perm perm :stats (stats (fn [s board] (basic-prefs s board perm)))})
        res (pmap proc perms)]
    (doseq [r res] (println r))))

(defn read-stats []
  (for [line (clojure.string/split (slurp "stats.edn") #"\n")]
    (edn/read-string line)))
