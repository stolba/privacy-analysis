(define (problem example) (:domain uav)
(:objects
	
)
(:init
	(not (uav-has-fuel))
	(not (mission-complete))
	(not (location1-complete l1))
	(not (location1-complete l2))
)
(:goal
	(and
		(mission-complete)
	)
)
)
