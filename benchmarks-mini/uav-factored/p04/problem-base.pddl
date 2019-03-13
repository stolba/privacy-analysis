(define (problem example) (:domain uav)
(:objects
	uav1 uav2 - uav
)
(:init
	(not (uav-has-fuel uav1))
	(not (uav-has-fuel uav2))
	(not (mission-complete))
	(not (base-has-supplies))
)
(:goal
	(and
		(mission-complete)
	)
)
)
