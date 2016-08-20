function info() {
	return "Partbuilder";
}

function run(e, part) {
	var log = e.log().instance("Partbuilder running " + part);
	var log = e.log().instance("env : " + e);

	var spart = part.newSubPart().getPart();

	var count = 8;
	var length = 100;

	// create a sector
	psector = part.newSubPart().getPart();
	psector_first = psector.newSubPart().getLocation().set(10,0,0);
	psector_second = psector.newSubPart().getLocation().set(20,0,0);

	// create a circle
	var i = 0;
	while(i++ < count) {
		var angle = 2.0 * i / count * Math.PI;
		var dangle = 1.0 * i / count * 360.0;

		var x = Math.cos(angle) * length;
		var z = Math.sin(angle) * length;
		var y = 0;

		var sub1 = part.newSubPart();
		sub1.getNormal().set(0,1,0);
		sub1.setAngle(dangle);

		sub1.setPart(psector);
	}
	
	log.info("Partbuilder done " + part + " name " + part.getName());
}
