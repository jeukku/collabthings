function run(envbuilder) {
	envbuilder.printOut();

	var factorybookmark = "$SELF/published/factory/boxsetfactory/latest";
	var factoryid = envbuilder.readStorage(factorybookmark);
	assert(factorybookmark, factoryid);

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
