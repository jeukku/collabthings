function info() {
	return "buildabox: attach";
}

function run(e, params) {
	var tool = params[0];
	var subpart = params[1];
	var destpart = params[2];
	
	e.log().info("tool " + tool);
	e.log().info("subpart " + subpart);
	e.log().info("destination " + destpart);
	
	var loc = destpart.getLocation().copy();
	loc.add(subpart.getLocation());
	e.log().info("attaching to " + loc);
	// should move tool to exact place so that subpart is correct orientation 
	// and location relative to parent part
	tool.moveTo(loc, subpart.getNormal());
	
	destpart.addPart(subpart);
	
	e.log().info("attach done");
}

