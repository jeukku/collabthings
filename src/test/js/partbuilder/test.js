function info() {
	return "Partbuilder";
}

function run(part, e) {
	var log = e.log().instance("Partbuilder running " + part);
	var log = e.log().instance("env : " + e);

	log.info("Partbuilder done " + part + " name " + part.getName());
}
