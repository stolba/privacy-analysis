(define (problem strips-sat-x-1) (:domain satellite)
(:objects
	
	phenomenon8 - direction
	thermograph0 - mode
	star3 - direction
	groundstation2 - direction
	groundstation1 - direction
	groundstation0 - direction

	(:private
		satellite2 - satellite
		instrument8 - instrument
		instrument6 - instrument
		instrument7 - instrument
	)
)
(:init
	
	(calibration_target instrument6 groundstation1)
	(supports instrument7 thermograph0)
	(calibration_target instrument7 groundstation1)
	(supports instrument8 thermograph0)
	(calibration_target instrument8 groundstation0)
	(on_board instrument6 satellite2)
	(on_board instrument7 satellite2)
	(on_board instrument8 satellite2)
	(power_avail satellite2)
	(pointing satellite2 phenomenon8)
)
(:goal
	(and
		(have_image star3 thermograph0)
	)
)
)
