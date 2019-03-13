(define (problem example) (:domain uav)
(:objects
	l1 l2 l3 l4 l5
)
(:init
	(not (uav-has-fuel))
	(not (location-complete l1))
	(not (location-complete l2))
	(not (location-complete l3))
	(not (location-complete l4))
	(not (location-complete l5))
)
(:goal
	(and
		(mission-complete l1 l2)
		(mission-complete l2 l3)
		(mission-complete l4 l5)
		(uav-has-fuel)
	)
)
)
