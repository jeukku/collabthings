function info() {
	return "buildabox: pickup";
}

function run(e, params) {
	var tool = params[0];
	var subpart = params[1];
	var source  = params[2];
	
	e.log().info("Pickup tool " + tool);
	e.log().info("Pickup subpart " + subpart);
	e.log().info("Pickup source " + source);
	
	source.getPool().waitForPart("ready");
	var platepart = source.getPool().getPart("ready");
	tool.getPool().addPart("pickedup", platepart);
	
	e.log().info("pickup done");
}

