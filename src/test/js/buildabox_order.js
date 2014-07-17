function info() {
	return "buildabox: Order";
}

function run(e, params) {
	var tool = params[0];	
	e.log().info("tool " + tool);
	
	tool.addTask("Assembly");
	
	e.log().info("Order done");
}

