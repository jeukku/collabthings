function info() {
	return "buildabox: move and attach";
}

function run(e, params) {
	var subpart = params[0];
	var destpart = params[1];
	
	e.log().info("subpart " + subpart);
	e.log().info("destpart " + destpart);
	
	var tool = e.getTool('tool');
	var partsource = e.getTool('source');
	partsource.call(e, 'need', subpart);
	tool.moveTo(partsource.getLocation());
	destpart.addSubPart(subpart);
	e.log().info("moveAndAttach done");
}

