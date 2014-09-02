function info() {
	return "buildabox platesource: Order";
}

function run(e, factory, values) {
	var tool = values.get('tool');
	var log = e.log().instance("" + tool);

	log.info("tool " + tool);

	factory.addTask("build", values);

	log.info("Order done");
}
