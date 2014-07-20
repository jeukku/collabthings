function info() {
	return "buildabox platesource: Order";
}

function run(e, params) {
	var tool = params[0];
	e.log().info("tool " + tool);

	tool.addTask("build");

	e.log().info("Order done");
}
