function info() {
	return "assembly: Start";
}

function run(e, factory, values) {
	var log = e.log().instance("Assembly start");
	var f = values.get('factory');
	
	factory.addTool("tool", f.getTool("pickuptool"));
	factory.addSuperheroRobot();
	
	log.info("start done");
}

