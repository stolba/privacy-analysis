(define (domain manufact)
	(:requirements :strips :factored-privacy :typing)
(:types
	 agent location component id - object 
 	 supplier factory assembly-line - agent 
 	 supplier-location factory-location assembly-location - location 
 	 console cylinder metal-plate extra - component 
 )
(:predicates
	(engine-at-factory ?f - factory-location)
	(interior-at-factory ?f - factory-location)
	(chassis-at-factory ?f - factory-location)
	(no-part-at-factory ?f - factory-location)
	(no-console-at-factory ?f - factory-location)
	(no-cylinder-at-factory ?f - factory-location)
	(no-metal-plate-at-factory ?f - factory-location)
	(no-extra-at-factory ?f - factory-location)
	(uses-console ?f - factory-location)
	(uses-cylinder ?f - factory-location)
	(uses-metal-plate ?f - factory-location)
	(uses-extra ?f - factory-location)
	(engine-at-assembly-line ?f - assembly-location)
	(interior-at-assembly-line ?f - assembly-location)
	(chassis-at-assembly-line ?f - assembly-location)
	(no-engine-at-assembly-line ?f - assembly-location)
	(no-interior-at-assembly-line ?f - assembly-location)
	(no-chassis-at-assembly-line ?f - assembly-location)
	(car-assembled-with-id ?id - id)
	(available ?id - id)
	(component-at-supplier ?c - component ?at - supplier-location)
	(component-at-factory ?c - component ?l - factory-location)

	(:private
		(assembly-line-at ?a - assembly-line ?at - assembly-location)
	)
)

(:action assemble
	:parameters (?a - assembly-line ?at - assembly-location ?id - id)
	:precondition (and
		(available ?id)
		(assembly-line-at ?a ?at)
		(engine-at-assembly-line ?at)
		(interior-at-assembly-line ?at)
		(chassis-at-assembly-line ?at)
	)
	:effect (and
		(not (engine-at-assembly-line ?at))
		(not (interior-at-assembly-line ?at))
		(not (chassis-at-assembly-line ?at))
		(not (available ?id))
		(no-engine-at-assembly-line ?at)
		(no-interior-at-assembly-line ?at)
		(no-chassis-at-assembly-line ?at)
		(car-assembled-with-id ?id)
	)
)

)