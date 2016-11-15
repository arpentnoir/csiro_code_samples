(ns robot-swarm.core-test
  (:require [clojure.test :refer :all]
            [robot-swarm.core :refer :all]))


(deftest test-calculate-new-position
	(testing "Moving north should increment the y coordinate by 1"
		(is (= (calculate-new-position 1 1 "N" 5 5) (vector 1 2)))
		)
	(testing "Moving east should increment the x coordinate by 1"
		(is (= (calculate-new-position 1 1 "E" 5 5) (vector 2 1)))
		)
	(testing "Moving south should decrement the y coordinate by 1"
		(is (= (calculate-new-position 1 1 "S" 5 5) (vector 1 0)))
		)
	(testing "Moving west should decrement the x coordinate by 1"
		(is (= (calculate-new-position 1 1 "W" 5 5) (vector 0 1)))
		)
	(testing "Moving north while at the northern boundary should not increment the y coordinate"
		(is (= (calculate-new-position 0 5 "N" 5 5) (vector 0 5)))
		)
	(testing "Moving east while at the eastern boundary should not increment the x coordinate"
		(is (= (calculate-new-position 5 0 "E" 5 5) (vector 5 0)))
		)
	(testing "Moving south while at the southern boundary should not decrement the y coordinate"
		(is (= (calculate-new-position 0 0 "S" 5 5) (vector 0 0)))
		)
	(testing "Moving west while at the western boundary should not decrement the x coordinate"
		(is (= (calculate-new-position 0 0 "W" 5 5) (vector 0 0)))
		)
	)

(deftest test-split-input-line
	(testing "Input string should be split on space into a vector"
		(is (= (split-input-line "zero one two three") (vector "zero" "one" "two" "three")))
		)
	)

(deftest test-get-x-pos
	(testing "Should return an integer representing a robot's initial x-position from an input line"
		(is (= (get-x-pos "1 2 N") 1)))
	)

(deftest test-get-y-pos
	(testing "Should return an integer representing a robot's initial y-position from an input line"
		(is (= (get-y-pos "1 2 N") 2)))
	)

(deftest test-get-heading
	(testing "Should return a character representing a robot's initial heading from an input line"
		(is (= (get-heading "1 2 N") "N")))
	)


(deftest test-get-grid-width
	(testing "Should return an integer representing the width of the landing grid"
		(is (= (get-grid-width "5 6") 5)))
	)


(deftest test-get-grid-height
	(testing "Should return an integer representing the height of the landing grid"
		(is (= (get-grid-height "5 6") 6)))
	)

(deftest test-calculate-new-heading
	(testing "Turning right should return the next cardinal point in a clockwise direction"
		(is (= (calculate-new-heading "N" "R") "E"))
		(is (= (calculate-new-heading "E" "R") "S"))
		(is (= (calculate-new-heading "S" "R") "W"))
		(is (= (calculate-new-heading "W" "R") "N"))
		)
	(testing "Turning left should return the next cardinal point in an anti-clockwise direction"
		(is (= (calculate-new-heading "N" "L") "W"))
		(is (= (calculate-new-heading "E" "L") "N"))
		(is (= (calculate-new-heading "S" "L") "E"))
		(is (= (calculate-new-heading "W" "L") "S"))
		)
	)


(deftest test-get-final-position
	(testing "Final position should be a string in the same format as the input string representing x and y coordinates and heading, after execution of instruction list"
		(is (= (get-final-position 1 2 "N" "LMLMLMLMM" 5 5) "1 3 N"))
		(is (= (get-final-position 3 3 "E" "MMRMMRMRRM" 5 5) "5 1 E"))
		)
	)



