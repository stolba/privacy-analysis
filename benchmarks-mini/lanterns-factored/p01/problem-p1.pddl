(define (problem p01) (:domain lanterns)
(:objects
	r1 - room
	r2 - room
	r3 - room
	r4 - room
	r5 - room
	d1 - door
	d2 - door
	d3 - door
	l1 - lantern
	l2 - lantern
	l3 - lantern
	s1 - switch
	s2 - switch
	s3 - switch
	w1 - window

	(:private
		p1 - player
	)
)
(:init
	(adjacent-rooms r1 r5 d1)
	(adjacent-rooms r5 r1 d1)
	(adjacent-rooms r1 r3 d2)
	(adjacent-rooms r3 r1 d2)
	(adjacent-rooms r3 r4 d3)
	(adjacent-rooms r4 r3 d3)
	(adjacent-switch r2 s1)
	(adjacent-switch r2 s2)
	(adjacent-switch r2 s3)
	(window r4 r2)
	(window r2 r4)
	(switch-to-door d1 s1)
	(switch-to-door d2 s2)
	(switch-to-door d3 s3)
	(locked d1)
	(locked d2)
	(locked d3)
	(not-lighted l1)
	(not-lighted l2)
	(not-lighted l3)
	(room-with-light r5)
	(in p1 r1)
	(in l1 r1)
	(in l2 r2)
	(in l3 r2)
	(without-lantern p1)
)
(:goal
	(and
		(lighted l1)
		(lighted l2)
		(lighted l3)
	)
)
)