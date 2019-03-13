(define (problem example) (:domain uav)
(:objects
	uav1 uav2 - uav
)
(:init
	(not (uav-has-fuel uav1))
	(not (uav-has-fuel uav2))
	(not (mission-complete))
	(not (location1-complete l1))
	(not (location1-complete l2))
	(not (location1-complete l3))
	(not (location1-complete l4))
)
(:goal
	(and
		(mission-complete)
	)
)
)
