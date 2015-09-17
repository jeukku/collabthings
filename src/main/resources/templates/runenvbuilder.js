function run(envbuilder) {
	envbuilder.printOut();

	var boxid = envbuilder
			.readStorage("self/published/factory/boxfactory/latest");
	if (boxid != null) {
		var order = envbuilder.getEnvironment().getScript("addorder");

		var state = envbuilder.createFactoryState("boxfactory", boxid);

		envbuilder.printOut();

		state.addTask("addorder", null);

		return state.getRunEnvironment();
	} else {
		return null;
	}
}

function info() {
	return "boxfactory runtime builder";
}
