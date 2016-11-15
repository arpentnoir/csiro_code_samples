(ns robot-swarm.core
  (:gen-class))
(require '[clojure.string :as str])

(defn calculate-new-position
	"Calculates new position after a move instruction. Does not allow moves that would result in exiting the landing area."
	([xPos yPos heading xBoundary yBoundary]
	(case heading 
		"N" (vector xPos (min (+ yPos 1) yBoundary)) 
		"E" (vector (min (+ xPos 1) xBoundary) yPos)
		"S" (vector xPos (max (- yPos 1) 0)) 
		"W" (vector (max (- xPos 1) 0) yPos) 
		)
	)

	)

(defn split-input-line
	"Splits an input line on space character, returning a vector"
	[input-line]
	(str/split input-line #" ")
	)

(defn get-x-pos
	"Returns the x-position of a robot given in an input line"
	[input-line]
	(read-string (get (split-input-line input-line) 0))
	)
(defn get-y-pos
	"Returns the y-position of a robot given in an input line"
	[input-line]
	(read-string (get (split-input-line input-line) 1))
	)
(defn get-heading
	"Returns the heading of a robot given in an input line"
	[input-line]
	(get (split-input-line input-line) 2)
	)
(defn get-grid-width
	"Returns the landing grid width given by the first line of input"
	[input-line]
	(read-string(get (split-input-line input-line) 0))
	)
(defn get-grid-height
	"Returns the landing grid height given by the first line of input"
	[input-line]
	(read-string(get (split-input-line input-line) 1))
	)

(defn calculate-new-heading
	"Gives a new heading based on current heading and direction of turn"
	[heading turn-direction]
	(if (= (str turn-direction) "L")
		(case heading
			"N" "W"
			"E" "N"
			"S" "E"
			"W" "S")
		(case heading
			"N" "E"
			"E" "S"
			"S" "W"
			"W" "N")
		)
	)

(defn get-final-position
	"Calculates the final position and orientation of a robot after execution of it's instructions"
	[xPos yPos heading instructions xBoundary yBoundary]
	(def position (vector xPos yPos))
	(def current-heading heading)
	(loop [iteration 0]
		(def current-instruction (get instructions iteration))
  		(if (> iteration (- (count instructions) 1))
    		(str (get position 0) " " (get position 1) " " current-heading)
    		(do (if (= (str current-instruction) "M")
					(def position (calculate-new-position (get position 0) (get position 1) current-heading xBoundary yBoundary))
					(def current-heading (calculate-new-heading current-heading current-instruction))
					)
    			(recur (inc iteration))
    			)
    		)
  		)
	)

(defn print-robot-positions
	"Takes a list of input lines and prints the final position and orientation of the robots represented in the input."
	[input-lines]
	(def first-line (nth input-lines 0))
	(loop [iteration 1]
		(if(> iteration (- (count input-lines) 1))
			(println "")
			(do (def robot-position-line (nth input-lines iteration))
				(def robot-instruction-line (nth input-lines (+ iteration 1)))
				
				(println (get-final-position (get-x-pos robot-position-line)
									(get-y-pos robot-position-line)
									(get-heading robot-position-line)
									robot-instruction-line
									(get-grid-width first-line)
									(get-grid-height first-line) 
									 ))
				(recur (inc(inc iteration))))

			)
		)
	)

(defn -main
  "Reads input from file if a file name is passed as an argument from the command line, otherwise reads input from std in, then prints the final positions"
  [& args]

  (if (> (count args ) 0)
  	(with-open [reader (clojure.java.io/reader (nth args 0))]
	(def lines (doall(line-seq reader))))
  	(def lines (remove (fn [x] (= (count x) 0))(line-seq (java.io.BufferedReader. *in*))))
  	)
  (print-robot-positions lines)
  )
