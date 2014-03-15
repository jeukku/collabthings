function info() {
	return "buildabox: move and attach";
}

function run(e, params) {
	var tool = params[0];
	var subpart = params[1];
	var source  = params[2];
	
	e.log().info("tool " + tool);
	e.log().info("subpart " + subpart);
	e.log().info("source " + source);
	
	tool.moveTo(subpart.getLocation());
	
	e.log().info("moveAndAttach done");
}

