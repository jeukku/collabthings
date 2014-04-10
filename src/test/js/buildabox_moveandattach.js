function info() {
	return "buildabox: move and attach";
}

function run(runenv, params) {
	var subpart = params[0];
	var destpart = params[1];
	
	runenv.log().info("moveandattach subpart " + subpart);
	runenv.log().info("moveandattach destpart " + destpart);
	
	var tool = runenv.getTool('tool');
	var partsource = runenv.getTool('source');
	partsource.call('need', subpart);
	tool.moveTo(partsource.getLocation());
	tool.call('pickup', subpart, partsource);
	//
	var destination = destpart.getPart().getLocation();
	destination.add(subpart.getLocation());
	runenv.log().info("moveandattach part destination " + destination);
			
	tool.moveTo(destpart.getPart().getLocation());
	tool.call('attach', subpart, destpart);
	
	runenv.log().info("moveAndAttach done");
}
