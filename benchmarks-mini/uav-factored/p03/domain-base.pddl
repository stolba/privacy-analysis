(define (domain uav)
	(:requirements :factored-privacy :typing)

(:types
	uav
	location
)

(:predicates
	(uav-has-fuel ?uav - uav)
	(mission-complete)

	(:private
		(base-has-supplies)
	)
)

(:action refuel
	:parameters (?uav - uav)
	:precondition (and
		(not (uav-has-fuel ?uav))
		(base-has-supplies)
	)
	:effect (and
		(uav-has-fuel ?uav)
		(not (base-has-supplies))
	)
)

(:action refuel-and-resuply
	:parameters (?uav - uav)
	:precondition (and
		(not (uav-has-fuel ?uav))
		(not (base-has-supplies))
	)
	:effect (and
		(uav-has-fuel ?uav)
		(base-has-supplies)
	)
)




)
