(define (problem example) (:domain uav)
(:objects
	l1 l2 l3 l4 l5
)
(:init
	(not (uav-has-fuel))
	(not (base-has-supplies))
)
(:goal
	(and
		(mission-complete l1 l2)
		(mission-complete l2 l3)
		(mission-complete l4 l5)
		(mission-complete)
		(uav-has-fuel)
	)
)
)
