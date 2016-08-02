function info() {
	return "Partbuilder";
}

function run(e, part) {
	var log = e.log().instance("Partbuilder running " + part);
	var log = e.log().instance("env : " + e);

	var radius = 500000;
	
	while(part.getSubParts().size()<6) {
		var sub1 = part.newSubPart();
		log.info("subpart " + sub1);
	}
	
	var firstpart = part.getSubParts().get(0).getPart();
	var anglestep = 2 * Math.PI / part.getSubParts().size();
	var angle = 0;
	
	for(var i=0; i<part.getSubParts().size(); i++) {
		var subp = part.getSubParts().get(i);
		subp.setPart(firstpart);
	
		var x = Math.cos(angle) * radius;
		var z = Math.sin(angle) * radius;
		
		subp.getLocation().set(x,0,z);
		
		subp.setAngle(-angle + Math.PI/2);
		
		angle += anglestep;
	}
	
	log.info("Partbuilder done " + part + " name " + part.getName());
}