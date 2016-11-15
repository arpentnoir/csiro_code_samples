(ns robot-swarm.core
  (:gen-class))

(with-open [reader (clojure.java.io/reader "input.txt")]
	(def lines (doall(line-seq reader))))

(defn print-robot-positions
	"Print robot positions"
	[input-lines]
	)

(defn calculate-new-position
	"Gets to next position"
	([xPos yPos heading xBoundary yBoundary]
	(case heading 
		"N" [xPos (min (+ yPos 1) yBoundary) ]
		"E" [(min (+ xPos 1) xBoundary) yPos ]
		"S" [xPos (max (- yPos 1) 0)]
		"W" [(max (- xPos 1) 0) yPos]
		)
	)

	)

(defn calculate-new-heading
	"Get new heading"
	[xPos yPos heading instructions xBoundary yBoundary]
	(if (= (get instructions 0) "L")
		(case heading
			"N" [xPos yPos "W" (subs instructions 1) xBoundary yBoundary]
			"E" [xPos yPos "N" (subs instructions 1) xBoundary yBoundary]
			"S" [xPos yPos "E" (subs instructions 1) xBoundary yBoundary]
			"W" [xPos yPos "S" (subs instructions 1) xBoundary yBoundary])
		(case heading
			"N" [xPos yPos "E" (subs instructions 1) xBoundary yBoundary]
			"E" [xPos yPos "S" (subs instructions 1) xBoundary yBoundary]
			"S" [xPos yPos "W" (subs instructions 1) xBoundary yBoundary]
			"W" [xPos yPos "N" (subs instructions 1) xBoundary yBoundary])
		)
	)

(defn get-final-position
	""
	([xPos yPos heading instructions xBoundary yBoundary]
	(if (> (count instructions) 0)
		(case (get instructions 0)
			"M" (get-final-position (calculate-new-position xPos yPos heading instructions xBoundary yBoundary))
			(get-final-position (calculate-new-heading xPos yPos heading instructions xBoundary yBoundary))
			)
		[xPos yPos heading instructions xBoundary yBoundary]
		)
	)
	([xPos yPos heading xBoundary yBoundary]
		[xPos yPos heading]
		)

	)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (calculate-new-position(0 0 "N" 5 5)))
  )
