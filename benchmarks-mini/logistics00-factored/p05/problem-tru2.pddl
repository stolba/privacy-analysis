(define (problem logistics-4-0) (:domain logistics)
(:objects
	obj21 - package
	obj22 - package
	obj23 - package
	apt2 - airport
	apt1 - airport

	(:private
		cit2 - city
		tru2 - truck
		pos2 - location
	)
)
(:init
	(at obj21 pos2)
	(at tru2 pos2)
	(in-city tru2 pos2 cit2)
	(in-city tru2 apt2 cit2)
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
