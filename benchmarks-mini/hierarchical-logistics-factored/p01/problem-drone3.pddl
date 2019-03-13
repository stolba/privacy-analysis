(define (problem hierarchical-logistics-0-1) (:domain hierarchical-logistics)
(:objects
	main - warehouse
	region1 - warehouse
	region2 - warehouse
	local1 - warehouse
	local2 - warehouse
	local3 - warehouse
	dest1 - destination
	dest2 - destination
	dest3 - destination
	dest4 - destination
	package1 - package
	package2 - package
	package3 - package
	package4 - package
	package5 - package
	package6 - package

	(:private
		drone3 - drone
	)
)
(:init
	(map main region1)
	(map main region2)
	(map region1 local1)
	(map region1 local2)
	(map region2 local2)
	(map region2 local3)
	(map local1 dest1)
	(map local1 dest2)
	(map local2 dest2)
	(map local2 dest3)
	(map local3 dest3)
	(map local3 dest4)
	(depo drone3 local3)
	(at-depo drone3)
	(address package1 dest1)
	(address package2 dest2)
	(address package3 dest3)
	(address package4 dest4)
	(address package5 dest1)
	(address package6 dest2)
	(at package1 main)
	(at package2 main)
	(at package3 main)
	(at package4 main)
	(at package5 main)
	(at package6 main)
)
(:goal
	(and
		(delivered package1)
		(delivered package2)
		(delivered package3)
		(delivered package4)
		(delivered package5)
		(delivered package6)
	)
)
)