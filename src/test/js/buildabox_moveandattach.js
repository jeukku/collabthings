function info() {
	return "buildabox: move and attach";
}

function run(runenv, params) {
	var subpart = params[0];
	var destpart = params[1];
	
	runenv.log().info("subpart " + subpart);
	runenv.log().info("destpart " + destpart);
	
	var tool = runenv.getTool('tool');
	var partsource = runenv.getTool('source');
	partsource.call('need', subpart);
	tool.moveTo(partsource.getLocation());
	tool.call('pickup', subpart, partsource);
	tool.moveTo(destpart.getLocation());
	tool.call('attach', subpart, destpart);
	
	runenv.log().info("moveAndAttach done");
}