function info() {
	return "assembly: move and attach";
}

function run(runenv, factory, values) {
	var log = runenv.log().instance("Move and attach");

	var subpart = values.get('subpart'); // subpart of the model box
	var destpart = values.get('destpart'); // box we are building

	log.info("moveandattach subpart " + subpart);
	log.info("moveandattach destpart " + destpart);

	var tool = factory.getTool('tool');
	tool.setInUse();

	var subpartid = subpart.getPart().getID();

	var pickupvalues = values.copy();
	var partsource = factory.getFactory('source');
	var ordervalues = values.copy();
	ordervalues.put('partid', subpartid)
	partsource.call('order', ordervalues);

	log.info("moving to partsource location " + partsource.getLocation());
	tool.moveTo(partsource.getLocation());
	pickupvalues.put('source', partsource);
	pickupvalues.put('partid', subpartid);
	tool.call('pickup', pickupvalues);
	//
	var destination = destpart.getAbsoluteLocation();
	destination.add(subpart.getLocation());
	log.info("moveandattach part destination " + destination);

	tool.moveTo(destination);
	tool.call('attach', values.copy());
	tool.setAvailable();

	log.info("moveandattach done");
}
