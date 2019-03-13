(define (domain uav)
	(:requirements :factored-privacy :typing)

(:types
	uav
	location
)

(:constants
	(:private
		l1 l2 - location
	)
)

(:predicates
	(uav-has-fuel ?uav - uav)
	(mission-complete)

	(:private
		(location1-complete ?l - location)
	)
)

(:action survey-location
	:parameters (?uav - uav ?l - location)
	:precondition (and
		(uav-has-fuel ?uav)
		(not (location-complete ?l))
	)
	:effect (and
		(not (uav-has-fuel ?uav))
		(location-complete ?l)
	)
)

(:action complete-mission
	:parameters ()
	:precondition (and
		(location-complete l1)
		(location-complete l2)
		(not (mission-complete))
	)
	:effect (and
		(mission-complete)
	)
)




)
