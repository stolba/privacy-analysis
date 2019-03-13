(define (problem p03) (:domain lanterns)
(:objects
	r1 - room
	r2 - room
	r3 - room
	r4 - room
	r5 - room
	r6 - room
	r7 - room
	d1 - door
	d2 - door
	d3 - door
	d4 - door
	d5 - door
	d6 - door
	l1 - lantern
	l2 - lantern
	l3 - lantern
	l4 - lantern
	s1 - switch
	s2 - switch
	s3 - switch
	s4 - switch
	s5 - switch
	s6 - switch

	(:private
		p1 - player
	)
)
(:init
	(adjacent-rooms r1 r3 d1)
	(adjacent-rooms r3 r5 d2)
	(adjacent-rooms r5 r6 d3)
	(adjacent-rooms r6 r7 d4)
	(adjacent-rooms r2 r4 d5)
	(adjacent-rooms r4 r6 d6)
	(adjacent-rooms r3 r1 d1)
	(adjacent-rooms r5 r3 d2)
	(adjacent-rooms r6 r5 d3)
	(adjacent-rooms r7 r6 d4)
	(adjacent-rooms r4 r2 d5)
	(adjacent-rooms r6 r4 d6)
	(adjacent-switch r2 s1)
	(adjacent-switch r2 s2)
	(adjacent-switch r2 s3)
	(adjacent-switch r4 s4)
	(adjacent-switch r6 s5)
	(adjacent-switch r7 s6)
	(switch-to-door d1 s1)
	(switch-to-door d2 s2)
	(switch-to-door d3 s3)
	(switch-to-door d4 s4)
	(switch-to-door d5 s5)
	(switch-to-door d6 s6)
	(locked d1)
	(locked d2)
	(locked d3)
	(locked d4)
	(locked d5)
	(locked d6)
	(not-lighted l1)
	(not-lighted l2)
	(not-lighted l3)
	(not-lighted l4)
	(in p1 r1)
	(in l1 r2)
	(in l2 r7)
	(in l3 r1)
	(in l4 r3)
	(without-lantern p1)
	(room-with-light r4)
)
(:goal
	(and
		(lighted l1)
		(lighted l2)
		(lighted l3)
		(lighted l4)
	)
)
)