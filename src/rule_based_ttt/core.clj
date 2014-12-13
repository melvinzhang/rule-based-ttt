(ns rule-based-ttt.core
  (:gen-class))

; board
;    0 1 2
;    -----
; 0 |2 7 3
; 1 |6 0 8
; 2 |1 5 4

(defn other [s]
  (if (= s 'X) 'O 'X))

(defn play [s board]
  (let [pos (for [x (range 0 3) y (range 0 3)] [x y])
        avail (filter #(nil? (get board %)) pos)
        t (other s)
        win (filter #(won s (assoc board % s)) avail)
        block (filter #(won t (assoc board % t)) avail)]
    (cond
      (> (count win) 0) (assoc board (first win) s)
      (> (count block) 0) (assoc board (first block) s)
      :else
        (condp = nil
          (get board [1 1]) (assoc board [1 1] s)
          (get board [2 0]) (assoc board [2 0] s)
          (get board [0 0]) (assoc board [0 0] s)
          (get board [0 2]) (assoc board [0 2] s)
          (get board [2 2]) (assoc board [2 2] s)
          (get board [2 1]) (assoc board [2 1] s)
          (get board [1 0]) (assoc board [1 0] s)
          (get board [0 1]) (assoc board [0 1] s)
          (get board [1 2]) (assoc board [1 2] s)) 
      )))

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
  (print (get board [0 0] " "))
  (print (get board [0 1] " "))
  (print (get board [0 2] " "))
  (print "\n")
  (print (get board [1 0] " "))
  (print (get board [1 1] " "))
  (print (get board [1 2] " "))
  (print "\n")
  (print (get board [2 0] " "))
  (print (get board [2 1] " "))
  (print (get board [2 2] " "))
  (print "\n")
  (print "\n"))

(defn run [p1 p2 board]
  (if (done board)
    (show board)
    (run p2 p1 (p1 board))))

(defn test-first [prog board depth]
  (if (done board)
    (when (won 'O board)
      (show board))
    (let [pboard (prog board)
          pos (for [x (range 0 3) y (range 0 3)] [x y])
          avail (filter #(nil? (get pboard %)) pos)]
        (when (not (done board)) 
          (for [p avail]
            (test-first prog (assoc pboard p 'O) (+ depth 1)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
