function run(envbuilder) {
	envbuilder.printOut();

	var factoryid = envbuilder
			.readStorage("self/published/factory/boxsetfactory/latest");
	var order = envbuilder.getEnvironment().getScript("addorder");

	var state = envbuilder.createFactoryState("boxsetfactory", factoryid);

	envbuilder.printOut();

	state.getRunEnvironment().getEnvironment().addScript("ordertask", order);
	state.addTask("ordertask", null);

	return state.getRunEnvironment();
}

function info() {
	return "boxfactory runtime builder";
}
