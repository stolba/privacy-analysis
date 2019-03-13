(define (problem BLOCKS-4-0) (:domain blocks)
(:objects
	a - block
	b - block
	c - block
	d - block

	(:private
		a2 - agent
	)
)
(:init
	(handempty a2)
	(clear a)
	(ontable a)
	(clear b)
	(ontable b)
	(ontable c)
	(on d c)
	(clear d)
)
(:goal
	(and
		
		(on b a)
		(on c b)
		(on d c)
		
	)
)
)
