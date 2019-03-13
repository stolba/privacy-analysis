(define (problem BLOCKS-4-0) (:domain blocks)
(:objects
	a - block
	b - block

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
)
(:goal
	(and
		
		(on b a)
		
	)
)
)
