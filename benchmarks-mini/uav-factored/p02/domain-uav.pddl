(define (domain uav)
	(:requirements :factored-privacy)

(:constants
	(:private
		l1 l2 l3
	)
)

(:predicates
	(uav-has-fuel)
	(mission-1-complete)
	(mission-2-complete)

	(:private
		(location-complete ?l)
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

(:action complete-mission-1
	:parameters ()
	:precondition (and
		(location-complete l1)
		(location-complete l2)
		(not (mission-1-complete))
	)
	:effect (and
		(mission-1-complete)
	)
)

(:action complete-mission-2
	:parameters ()
	:precondition (and
		(location-complete l2)
		(location-complete l3)
		(not (mission-2-complete))
	)
	:effect (and
		(mission-2-complete)
	)
)






)
