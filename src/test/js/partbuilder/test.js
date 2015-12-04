function info() {
	return "Partbuilder";
}

function run(part, e) {
	var log = e.log().instance("Partbuilder running " + part);
	var log = e.log().instance("env : " + e);

	var sub1 = part.newSubPart();
	log.info("subpart " + sub1);
	
	log.info("Partbuilder done " + part + " name " + part.getName());
}
