function info() {
	return "assembly: attach";
}

function run(e, factory, values) {
	var log = e.log().instance("Assembly attach");

	var tool = values.get('tool');
	var subpart = values.get('subpart');
	var destpart = values.get('destpart');

	log.info("Attach tool " + tool);
	log.info("Attach subpart " + subpart);
	log.info("Attach destination " + destpart);

	var loc = destpart.getLocation().copy();
	loc.add(subpart.getLocation());
	log.info("attaching to " + loc);
	// should move tool to exact place so that subpart is correct orientation
	// and location relative to parent part
	tool.moveTo(loc, subpart.getNormal(), subpart.getAngle());

	var pickedup = tool.getPool().getPart("pickedup");
	destpart.addPart(subpart);

	log.info("attach done");
}
