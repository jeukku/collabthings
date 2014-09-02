function info() {
	return "assembly: Order";
}

function run(e, factory, values) {
	var partid = values.get('partid');
	var poolid = values.get('poolid');
	
	var log = e.log().instance("assembly order " + factory);

	if(!partid) {
		partid = factory.getParameter('partid');
	}
	
	log.info("factory order " + factory + " partid " + partid);

	factory.addTask("build", values);

	log.info("Order done " + partid);
}
