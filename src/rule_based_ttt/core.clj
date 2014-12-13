(ns rule-based-ttt.core
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
      (= (count board) 9)))

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
      (> (count win) 0) (move board (first win) s)
      (> (count block) 0) (move board (first block) s)
      (> (count pref) 0) (move board (first pref) s))))

; 2 7 3
; 6 0 8
; 1 5 4
(defn basic [s board]
  (basic-prefs s board [[1 1] [2 0] [0 0] [0 2] [2 2] [2 1] [1 0] [0 1] [1 2]]))

; 3 7 2
; 6 0 8
; 1 5 4
(defn basic2 [s board]
  (basic-prefs s board [[1 1] [2 0] [0 2] [0 0] [2 2] [2 1] [1 0] [0 1] [1 2]]))

; lift a function p from board -> board to board -> [board]
(defn lift [p s]
  (fn [board]
    (if (done board) [board] [(p s board)])))

(defn exhaust [s]
  (fn [board]
    (if (done board)
      [board] 
      (let [pos (for [x (range 0 3) y (range 0 3)] [x y])
            avail (filter #(nil? (get board %)) pos)]
        (for [p avail]
          (move board p s))))))

(defn gen-end-state [p1 p2 boards]
  (let [res (mapcat p1 boards)]
    (if (= res boards) boards (gen-end-state p2 p1 (mapcat p1 boards)))))

(defn gen-first [p]
  (gen-end-state (lift p 'X) (exhaust 'O) [{:move {}}]))

(defn gen-second [p]
  (gen-end-state (exhaust 'X) (lift p 'O) [{:move {}}]))

(defn fail-count [p]
  (+ (count (filter #(won 'O %) (gen-first p)))
     (count (filter #(won 'X %) (gen-second p)))))

(defn -main
  [& args]
  (println "Testing AI as first player")
  (map show (filter #(won 'O %) (gen-first basic)))
  (println "Testing AI as second player")
  (map show (filter #(won 'X %) (gen-second basic))))
