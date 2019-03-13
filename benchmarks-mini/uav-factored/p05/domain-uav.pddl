(define (domain uav)
	(:requirements :factored-privacy)


(:predicates
	(uav-has-fuel)
	(mission-complete ?l1 ?l2)

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

(:action complete-mission
	:parameters (?l1 ?l2)
	:precondition (and
		(location-complete ?l1)
		(location-complete ?l2)
		(not (mission-complete ?l1 ?l2))
	)
	:effect (and
		(mission-complete ?l1 ?l2)
	)
)






)
