function info() {
	return "buildabox platesource: Build";
}

function run(e, params) {
	var log = e.log();
	var tool = params[0];
	log.info("Platesource tool " + tool);

	var plateid = e.getParameter('plateid');
	log.info("plate id " + plateid);

	var part = e.getPart(plateid);
	tool.getPool().addPart("ready",  part);
	
	e.log().info("Build platesource done");
}
