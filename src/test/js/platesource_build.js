function info() {
	return "buildabox platesource: Build";
}

function run(e, factory, values) {
	var log = e.log().instance("platesource build " + factory);

	var platebm = factory.getParameter('bmplate');
	log.info("plate bookmark " + platebm);
	
	var plateid = factory.readStorage(platebm);
	log.info("plate partid " + plateid);

	var part = factory.getPart(plateid);
	factory.getPool().addPart(plateid, part);

	log.info("Build platesource done " + platebm + " id " + plateid);
}
