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
		(supplier-at ?s - supplier ?at - supplier-location)
	)
)

(:action ship-console
	:parameters (?s - supplier ?c - console ?from - supplier-location ?to - factory-location)
	:precondition (and
		(supplier-at ?s ?from)
		(uses-console ?to)
		(component-at-supplier ?c ?from)
		(no-console-at-factory ?to)
	)
	:effect (and
		(not (component-at-supplier ?c ?from))
		(component-at-factory ?c ?to)
		(not (no-console-at-factory ?to))
	)
)


(:action ship-metal-plate
	:parameters (?s - supplier ?c - metal-plate ?from - supplier-location ?to - factory-location)
	:precondition (and
		(supplier-at ?s ?from)
		(uses-metal-plate ?to)
		(component-at-supplier ?c ?from)
		(no-metal-plate-at-factory ?to)
	)
	:effect (and
		(not (component-at-supplier ?c ?from))
		(component-at-factory ?c ?to)
		(not (no-metal-plate-at-factory ?to))
	)
)


(:action ship-extra
	:parameters (?s - supplier ?c - extra ?from - supplier-location ?to - factory-location)
	:precondition (and
		(supplier-at ?s ?from)
		(uses-extra ?to)
		(component-at-supplier ?c ?from)
		(no-extra-at-factory ?to)
	)
	:effect (and
		(not (component-at-supplier ?c ?from))
		(component-at-factory ?c ?to)
		(not (no-extra-at-factory ?to))
	)
)


(:action ship-cylinder
	:parameters (?s - supplier ?c - cylinder ?from - supplier-location ?to - factory-location)
	:precondition (and
		(supplier-at ?s ?from)
		(uses-cylinder ?to)
		(component-at-supplier ?c ?from)
		(no-cylinder-at-factory ?to)
	)
	:effect (and
		(not (component-at-supplier ?c ?from))
		(component-at-factory ?c ?to)
		(not (no-cylinder-at-factory ?to))
	)
)

)