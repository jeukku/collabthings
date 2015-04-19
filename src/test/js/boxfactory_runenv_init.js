function run(envbuilder) {
	envbuilder.printOut();

	var boxid = envbuilder.getEnvironment().getParameter("boxfactoryid");
	var order = envbuilder.getEnvironment().getScript("addorder");

	var state = envbuilder.createFactoryState("boxfactory", boxid);

	envbuilder.printOut();

	state.getRunEnvironment().getEnvironment().addScript("ordertask", order);
	state.addTask("ordertask", null);
}

function info() {
	return "boxfactory runtime builder";
}
