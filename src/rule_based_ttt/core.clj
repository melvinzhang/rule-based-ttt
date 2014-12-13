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

(defn move [board p s]
  (let [moves (:move board)]
    (-> board
      (assoc p s)
      (assoc :move (assoc moves p (count moves))))))

(defn basic [s board]
  (let [pos (for [x (range 0 3) y (range 0 3)] [x y])
        avail (filter #(nil? (get board %)) pos)
        t (other s)
        win (filter #(won s (move board % s)) avail)
        block (filter #(won t (move board % t)) avail)]
    (cond
      (> (count win) 0) (move board (first win) s)
      (> (count block) 0) (move board (first block) s)
      :else
        (condp = nil
          (get board [1 1]) (move board [1 1] s)
          (get board [2 0]) (move board [2 0] s)
          (get board [0 0]) (move board [0 0] s)
          (get board [0 2]) (move board [0 2] s)
          (get board [2 2]) (move board [2 2] s)
          (get board [2 1]) (move board [2 1] s)
          (get board [1 0]) (move board [1 0] s)
          (get board [0 1]) (move board [0 1] s)
          (get board [1 2]) (move board [1 2] s)) 
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
    (print "\n") 
    
    )
  )

(defn run [p1 p2 board]
  (if (done board)
    (show board)
    (run p2 p1 (p1 board))))

(defn test-first 
  ([prog] (test-first #(prog 'X %) {:move {}}))
  ([prog board]
    (if (done board)
      (if (won 'O board)
        (do
          (show board)
          false)
        true)
      (let [nextb (prog board)
            pos (for [x (range 0 3) y (range 0 3)] [x y])
            avail (filter #(nil? (get nextb %)) pos)]
        (if (not (done nextb)) 
          (for [p avail]
            (test-first prog (move nextb p 'O)))
          true)))))

(defn test-second
  ([prog] (test-second #(prog 'O %) {:move {}}))
  ([prog board]
    (if (done board)
      (if (won 'X board)
        (do
          (show board)
          false)
        true)
      (let [pos (for [x (range 0 3) y (range 0 3)] [x y])
            avail (filter #(nil? (get board %)) pos)]
        (for [p avail]
          (test-second prog (prog (move board p 'X))))))))

(defn -main
  [& args]
  (println "Testing AI as first player")
  (test-first basic)
  (println "Testing AI as second player")
  (test-second basic))
