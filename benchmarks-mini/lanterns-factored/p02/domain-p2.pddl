(define (domain lanterns)
	(:requirements :strips :factored-privacy :typing)
(:types
	 thing door window room switch - object 
 	 player lantern - thing 
 )
(:predicates
	(adjacent-rooms ?r1 - room ?r2 - room ?d - door)
	(adjacent-switch ?r - room ?s - switch)
	(switch-to-door ?sw - door ?s - switch)
	(window ?r1 - room ?r2 - room)
	(locked ?d - door)
	(unlocked ?d - door)
	(lighted ?l - lantern)
	(not-lighted ?l - lantern)
	(room-with-light ?r - room)
	(in ?o - thing ?r - room)
	(on ?p - player ?s - switch)
	(holding-lantern ?p - player ?l - lantern)
	(without-lantern ?p - player)
)

(:action move-room
	:parameters (?p - player ?l1 - room ?l2 - room ?d - door)
	:precondition (and
		(in ?p ?l1)
		(adjacent-rooms ?l1 ?l2 ?d)
		(unlocked ?d)
	)
	:effect (and
		(in ?p ?l2)
		(not (in ?p ?l1))
	)
)


(:action collect-lantern
	:parameters (?p - player ?l - lantern ?r - room)
	:precondition (and
		(in ?p ?r)
		(in ?l ?r)
		(without-lantern ?p)
	)
	:effect (and
		(not (without-lantern ?p))
		(holding-lantern ?p ?l)
		(not (in ?l ?r))
	)
)


(:action put-down-lantern
	:parameters (?p - player ?l - lantern ?r - room)
	:precondition (and
		(in ?p ?r)
		(holding-lantern ?p ?l)
	)
	:effect (and
		(in ?l ?r)
		(without-lantern ?p)
		(not (holding-lantern ?p ?l))
	)
)


(:action light-lantern
	:parameters (?p - player ?l - lantern ?r - room)
	:precondition (and
		(in ?p ?r)
		(room-with-light ?r)
		(holding-lantern ?p ?l)
		(not-lighted ?l)
	)
	:effect (and
		(lighted ?l)
		(not (not-lighted ?l))
	)
)


(:action move-room-to-switch
	:parameters (?p - player ?r - room ?s - switch ?d - door)
	:precondition (and
		(in ?p ?r)
		(adjacent-switch ?r ?s)
		(switch-to-door ?d ?s)
		(locked ?d)
	)
	:effect (and
		(not (in ?p ?r))
		(on ?p ?s)
		(unlocked ?d)
		(not (locked ?d))
	)
)


(:action move-switch-to-room
	:parameters (?p - player ?r - room ?s - switch ?d - door)
	:precondition (and
		(on ?p ?s)
		(adjacent-switch ?r ?s)
		(switch-to-door ?d ?s)
		(unlocked ?d)
	)
	:effect (and
		(not (on ?p ?s))
		(in ?p ?r)
		(locked ?d)
		(not (unlocked ?d))
	)
)


(:action send-lantern
	:parameters (?p - player ?l - lantern ?r1 - room ?r2 - room)
	:precondition (and
		(in ?p ?r1)
		(holding-lantern ?p ?l)
		(window ?r1 ?r2)
	)
	:effect (and
		(in ?l ?r2)
		(without-lantern ?p)
		(not (holding-lantern ?p ?l))
	)
)

)