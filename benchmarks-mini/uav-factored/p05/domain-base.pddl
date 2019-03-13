(define (domain uav)
	(:requirements :factored-privacy)

(:predicates
	(uav-has-fuel)
	(mission-complete ?l1 ?l2)

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

(:action resuply
	:parameters ()
	:precondition (and
		(not (base-has-supplies))
	)
	:effect (and
		(base-has-supplies)
	)
)




)
