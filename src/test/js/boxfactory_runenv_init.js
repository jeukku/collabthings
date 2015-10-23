function run(envbuilder) {
	envbuilder.printOut();

	var boxid = envbuilder
			.readStorage("$SELF/published/factory/boxfactory/latest");
	var order = envbuilder.getEnvironment().getScript("addorder");

	var state = envbuilder.createFactoryState("boxfactory", boxid);

	envbuilder.printOut();

	state.getRunEnvironment().getEnvironment().addScript("ordertask", order);
	state.addTask("ordertask", null);

	return state.getRunEnvironment();
}

function info() {
	return "boxfactory runtime builder";
}
