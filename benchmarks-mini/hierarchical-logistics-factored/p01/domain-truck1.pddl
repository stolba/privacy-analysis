(define (domain hierarchical-logistics)
	(:requirements :strips :factored-privacy :typing)
(:types
	 location vehicle package - object 
 	 warehouse destination - location 
 	 truck car drone - vehicle 
 )
(:predicates
	(map ?from - warehouse ?to - location)
	(at ?m - package ?r - location)
	(address ?p - package ?d - destination)
	(delivered ?p - package)

	(:private
		(depo ?v - vehicle ?w - warehouse)
		(delivering ?v - vehicle)
		(at-depo ?v - vehicle)
	)
)

(:action send-truck-three
	:parameters (?v - truck ?from - warehouse ?to - warehouse ?p1 - package ?p2 - package ?p3 - package)
	:precondition (and
		(depo ?v ?from)
		(at-depo ?v)
		(map ?from ?to)
		(at ?p1 ?from)
		(at ?p2 ?from)
		(at ?p3 ?from)
	)
	:effect (and
		(not (at ?p1 ?from))
		(not (at ?p2 ?from))
		(not (at ?p3 ?from))
		(not (at-depo ?v))
		(delivering ?v)
		(at ?p1 ?to)
		(at ?p2 ?to)
		(at ?p3 ?to)
	)
)


(:action send-truck-two
	:parameters (?v - truck ?from - warehouse ?to - warehouse ?p1 - package ?p2 - package)
	:precondition (and
		(depo ?v ?from)
		(at-depo ?v)
		(map ?from ?to)
		(at ?p1 ?from)
		(at ?p2 ?from)
	)
	:effect (and
		(not (at ?p1 ?from))
		(not (at ?p2 ?from))
		(not (at-depo ?v))
		(delivering ?v)
		(at ?p1 ?to)
		(at ?p2 ?to)
	)
)


(:action send-truck-one
	:parameters (?v - truck ?from - warehouse ?to - warehouse ?p1 - package)
	:precondition (and
		(depo ?v ?from)
		(at-depo ?v)
		(map ?from ?to)
		(at ?p1 ?from)
	)
	:effect (and
		(not (at ?p1 ?from))
		(not (at-depo ?v))
		(delivering ?v)
		(at ?p1 ?to)
	)
)


(:action return
	:parameters (?v - vehicle)
	:precondition 
		(delivering ?v)
	:effect (and
		(not (delivering ?v))
		(at-depo ?v)
	)
)

)