(define (problem example) (:domain uav)
(:objects
	
)
(:init
	(not (uav-has-fuel))
	(not (mission-complete))
	(not (base-has-supplies))
)
(:goal
	(and
		(mission-complete)
	)
)
)
