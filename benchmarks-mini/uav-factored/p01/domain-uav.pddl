(define (domain uav)
	(:requirements :factored-privacy)

(:constants
	(:private
		l1 l2
	)
)

(:predicates
	(uav-has-fuel)
	(mission-complete)

	(:private
		(location1-complete ?l)
	)
)

(:action survey-location
	:parameters (?l)
	:precondition (and
		(uav-has-fuel)
		(not (location-complete ?l))
	)
	:effect (and
		(not (uav-has-fuel))
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
