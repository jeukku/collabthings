function info() {
	return "Stackbuilder";
}

function run(e, part) {
	var log = e.log().instance("Stackbuilder running " + part);

	while(part.getSubParts().size()<10) {
		var sub1 = part.newSubPart();
		log.info("subpart " + sub1);
	}
	
	var firstpart = part.getSubParts().get(0).getPart();
	
	for(var i=0; i<part.getSubParts().size(); i++) {
		var subp = part.getSubParts().get(i);
		subp.setPart(firstpart);
		subp.getLocation().set(0, i*3000 ,0);
	}
	
	log.info("Stackbuilder done " + part + " name " + part.getName());
}