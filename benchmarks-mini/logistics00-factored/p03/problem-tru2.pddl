(define (problem logistics-4-0) (:domain logistics)
(:objects
	
	apt2 - airport
	apt1 - airport
	obj11 - package
	pos1 - location

	(:private
		cit2 - city
		tru2 - truck
		pos2 - location
	)
)
(:init
	(at obj11 pos1)
	(at tru2 pos2)
	(in-city tru2 pos2 cit2)
	(in-city tru2 apt2 cit2)
)
(:goal
	(and
		(at obj11 apt1)
	)
)
)
