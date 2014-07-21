function info() {
	return "buildabox: move and attach";
}

function run(runenv, params) {
	var subpart = params[0]; // subpart of the model box
	var destpart = params[1]; // box we are building

	runenv.log().info("moveandattach subpart " + subpart);
	runenv.log().info("moveandattach destpart " + destpart);

	var tool = runenv.getTool('tool');
	tool.setInUse();

	var partsource = runenv.getTool('source');
	partsource.call('order', subpart);
	tool.moveTo(partsource.getLocation());
	tool.call('pickup', subpart, partsource);
	//
	var destination = destpart.getLocation();
	destination.add(subpart.getLocation());
	runenv.log().info("moveandattach part destination " + destination);

	tool.moveTo(destination);
	tool.call('attach', subpart, destpart);
	tool.setAvailable();

	runenv.log().info("moveandattach done");
}
