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

(:action send-drone
	:parameters (?v - drone ?from - warehouse ?to - destination ?p1 - package)
	:precondition (and
		(depo ?v ?from)
		(at-depo ?v)
		(map ?from ?to)
		(address ?p1 ?to)
		(at ?p1 ?from)
	)
	:effect (and
		(not (at ?p1 ?from))
		(not (at-depo ?v))
		(delivering ?v)
		(delivered ?p1)
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