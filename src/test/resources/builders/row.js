function info() {
	return "Partbuilder";
}

function run(e, part) {
	var log = e.log().instance("Partbuilder running " + part);
	var log = e.log().instance("env : " + e);

	while(part.getSubParts().size()<6) {
		var sub1 = part.newSubPart();
		log.info("subpart " + sub1);
	}
	
	var firstpart = part.getSubParts().get(0).getPart();
	
	for(var i=0; i<part.getSubParts().size(); i++) {
		var subp = part.getSubParts().get(i);
		subp.setPart(firstpart);
		subp.getLocation().set(i*2100, 0,0);
	}
	
	log.info("Partbuilder done " + part + " name " + part.getName());
}