(define (problem p03) (:domain manufact)
(:objects
	id-1 - id
	id-2 - id
	console-1 - console
	cylinder-1 - cylinder
	metal-plate-1 - metal-plate
	metal-plate-2 - metal-plate
	extra-1 - extra
	extra-2 - extra
	console-2 - console
	cylinder-2 - cylinder
	metal-plate-3 - metal-plate
	metal-plate-4 - metal-plate
	extra-3 - extra
	extra-4 - extra
	supplier-location-1 - supplier-location
	supplier-location-2 - supplier-location
	factory-location-1 - factory-location
	assembly-location-1 - assembly-location

	(:private
		supplier-1 - supplier
	)
)
(:init
	(available id-1)
	(available id-2)
	(uses-console factory-location-1)
	(uses-extra factory-location-1)
	(uses-cylinder factory-location-1)
	(uses-metal-plate factory-location-1)
	(no-console-at-factory factory-location-1)
	(no-cylinder-at-factory factory-location-1)
	(no-metal-plate-at-factory factory-location-1)
	(no-extra-at-factory factory-location-1)
	(no-part-at-factory factory-location-1)
	(no-engine-at-assembly-line assembly-location-1)
	(no-interior-at-assembly-line assembly-location-1)
	(no-chassis-at-assembly-line assembly-location-1)
	(component-at-supplier console-1 supplier-location-2)
	(component-at-supplier cylinder-1 supplier-location-1)
	(component-at-supplier console-2 supplier-location-2)
	(component-at-supplier cylinder-2 supplier-location-1)
	(component-at-supplier metal-plate-1 supplier-location-1)
	(component-at-supplier metal-plate-2 supplier-location-1)
	(component-at-supplier metal-plate-3 supplier-location-1)
	(component-at-supplier metal-plate-4 supplier-location-1)
	(component-at-supplier extra-1 supplier-location-1)
	(component-at-supplier extra-2 supplier-location-1)
	(component-at-supplier extra-3 supplier-location-1)
	(component-at-supplier extra-4 supplier-location-1)
	(supplier-at supplier-1 supplier-location-1)
)
(:goal
	(and
		(car-assembled-with-id id-1)
		(car-assembled-with-id id-2)
	)
)
)