(define (problem example) (:domain uav)
(:objects
	
)
(:init
	(not (uav-has-fuel))
	(not (mission-1-complete))
	(not (mission-2-complete))
	(not (base-has-supplies))
)
(:goal
	(and
		(mission-1-complete)
		(mission-2-complete)
		(uav-has-fuel)
	)
)
)
