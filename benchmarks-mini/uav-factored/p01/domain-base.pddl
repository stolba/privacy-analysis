(define (domain uav)
	(:requirements :factored-privacy)

(:predicates
	(uav-has-fuel)
	(mission-complete)

	(:private
		(base-has-supplies)
	)
)

(:action refuel
	:parameters ()
	:precondition (and
		(not (uav-has-fuel))
		(base-has-supplies)
	)
	:effect (and
		(uav-has-fuel)
		(not (base-has-supplies))
	)
)

(:action refuel-and-resuply
	:parameters ()
	:precondition (and
		(not (uav-has-fuel))
		(not (base-has-supplies))
	)
	:effect (and
		(uav-has-fuel)
		(base-has-supplies)
	)
)




)
