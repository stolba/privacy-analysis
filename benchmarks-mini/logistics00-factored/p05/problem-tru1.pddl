(define (problem logistics-4-0) (:domain logistics)
(:objects
	obj21 - package
	obj22 - package
	obj23 - package
	apt2 - airport
	apt1 - airport
	

	(:private
		pos1 - location
		tru1 - truck
		cit1 - city
	)
)
(:init
	(at tru1 pos1)
	(at obj22 pos1)
	(in-city tru1 pos1 cit1)
	(in-city tru1 apt1 cit1)
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
