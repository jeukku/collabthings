function info() {
	return "buildabox: pickup";
}

function run(e, factory, values) {
	var tool = values.get('tool');
	var subpart = values.get('subpart');
	var source = values.get('source');
	var partid = values.get('partid');

	e.log().info("Pickup tool " + tool);
	e.log().info("Pickup subpart " + subpart);
	e.log().info("Pickup source " + source);

	source.getPool().waitForPart(partid);
	var platepart = source.getPool().getPart(partid);
	tool.getPool().addPart("pickedup", platepart);

	e.log().info("pickup done");
}
