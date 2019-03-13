(define (problem logistics-4-0) (:domain logistics)
(:objects
	obj21 - package
	obj22 - package
	obj23 - package
	apt2 - airport
	apt1 - airport

	(:private
		apn1 - airplane
	)
)
(:init
	(at apn1 apt2)
	(at obj23 apt1)
)
(:goal
	(and
		(at obj21 apt1)
		(at obj22 apt2)
		(at obj23 apt2)
	)
)
)
