function info() {
	return "buildabox platesource: Build";
}

function run(e, factory, values) {
	var log = e.log().instance("platesource build " + factory);

	var plateid = factory.getParameter('plateid');
	log.info("plate partid " + plateid);

	var part = factory.getPart(plateid);
	factory.getPool().addPart(plateid, part);

	log.info("Build platesource done");
}
