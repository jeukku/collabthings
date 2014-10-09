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

	// var ordervalues = values.copy();
	// ordervalues.put('partid', subpartid)
	// partsource.call('order', ordervalues);

	log
			.info("moving to partsource location "
					+ partsource.getVector("storage"));

	var storagelocation = partsource.getVector("storage");
	storagelocation = partsource.getTransformedVector(storagelocation);

	tool.moveTo(storagelocation);
	pickupvalues.put('source', partsource);
	// pickupvalues.put('partid', subpartid);
	tool.call('pickup', pickupvalues);
	//

	var normaldestdot = subpart.getNormal().dot(subpart.getLocation());
	var normalv = subpart.getNormal().copy();
	if (normaldestdot < 0.1) {
		normaldestdot = 2;
	}

	normalv.scale(normaldestdot * 2);

	log.info("dot " + normaldestdot + " " + normalv);

	var adestination = destpart.getLocation().getAdd(
			subpart.getLocation().getAdd(normalv));
	var destination = destpart.getLocation().getAdd(subpart.getLocation());

	log.info("moveandattach part first destination " + adestination);
	tool.moveTo(adestination, subpart.getNormal(), subpart.getAngle());

	log.info("moveandattach part destination " + destination);
	tool.moveTo(destination);
	tool.call('attach', values.copy());

	tool.moveTo(adestination);

	tool.setAvailable();

	log.info("moveandattach done");
}
