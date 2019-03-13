(define (problem example) (:domain uav)
(:objects
	
)
(:init
	(not (uav-has-fuel))
	(not (mission-1-complete))
	(not (mission-2-complete))
	(not (location-complete l1))
	(not (location-complete l2))
	(not (location-complete l3))
)
(:goal
	(and
		(mission-1-complete)
		(mission-2-complete)
		(uav-has-fuel)
	)
)
)
