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
		(factory-produces-engine ?f - factory)
		(factory-produces-interior ?f - factory)
		(factory-produces-chassis ?f - factory)
		(factory-at ?f - factory ?at - factory-location)
	)
)

(:action ship-interior
	:parameters (?f - factory ?at - factory-location ?to - assembly-location)
	:precondition (and
		(factory-at ?f ?at)
		(interior-at-factory ?at)
		(no-interior-at-assembly-line ?to)
	)
	:effect (and
		(not (interior-at-factory ?at))
		(not (no-interior-at-assembly-line ?to))
		(no-part-at-factory ?at)
		(interior-at-assembly-line ?to)
	)
)


(:action ship-engine
	:parameters (?f - factory ?at - factory-location ?to - assembly-location)
	:precondition (and
		(factory-at ?f ?at)
		(engine-at-factory ?at)
		(no-engine-at-assembly-line ?to)
	)
	:effect (and
		(not (engine-at-factory ?at))
		(not (no-engine-at-assembly-line ?to))
		(no-part-at-factory ?at)
		(engine-at-assembly-line ?to)
	)
)


(:action ship-chassis
	:parameters (?f - factory ?at - factory-location ?to - assembly-location)
	:precondition (and
		(factory-at ?f ?at)
		(chassis-at-factory ?at)
		(no-chassis-at-assembly-line ?to)
	)
	:effect (and
		(not (chassis-at-factory ?at))
		(not (no-chassis-at-assembly-line ?to))
		(no-part-at-factory ?at)
		(chassis-at-assembly-line ?to)
	)
)


(:action produce-interior
	:parameters (?f - factory ?req1 - console ?req2 - extra ?at - factory-location)
	:precondition (and
		(no-part-at-factory ?at)
		(factory-produces-interior ?f)
		(component-at-factory ?req1 ?at)
		(component-at-factory ?req2 ?at)
		(factory-at ?f ?at)
	)
	:effect (and
		(not (component-at-factory ?req1 ?at))
		(not (component-at-factory ?req2 ?at))
		(not (no-part-at-factory ?at))
		(no-console-at-factory ?at)
		(no-extra-at-factory ?at)
		(interior-at-factory ?at)
	)
)


(:action produce-engine
	:parameters (?f - factory ?req1 - cylinder ?req2 - metal-plate ?at - factory-location)
	:precondition (and
		(no-part-at-factory ?at)
		(factory-produces-engine ?f)
		(component-at-factory ?req1 ?at)
		(component-at-factory ?req2 ?at)
		(factory-at ?f ?at)
	)
	:effect (and
		(not (component-at-factory ?req1 ?at))
		(not (component-at-factory ?req2 ?at))
		(not (no-part-at-factory ?at))
		(no-metal-plate-at-factory ?at)
		(no-cylinder-at-factory ?at)
		(engine-at-factory ?at)
	)
)


(:action produce-chassis
	:parameters (?f - factory ?req1 - metal-plate ?req2 - extra ?at - factory-location)
	:precondition (and
		(no-part-at-factory ?at)
		(factory-produces-chassis ?f)
		(component-at-factory ?req1 ?at)
		(component-at-factory ?req2 ?at)
		(factory-at ?f ?at)
	)
	:effect (and
		(not (component-at-factory ?req1 ?at))
		(not (component-at-factory ?req2 ?at))
		(not (no-part-at-factory ?at))
		(no-metal-plate-at-factory ?at)
		(no-extra-at-factory ?at)
		(chassis-at-factory ?at)
	)
)

)