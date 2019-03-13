(define (problem BLOCKS-4-0) (:domain blocks)
(:objects
	a - block
	b - block

	(:private
		a1 - agent
	)
)
(:init
	(handempty a1)
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
