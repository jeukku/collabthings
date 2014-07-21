function info() {
	return "buildabox: Order";
}

function run(e, params) {
	var tool = params[0];
	
	var partid;
	if(params.length>1) {
		partid = params[1];
	} else {
		partid = e.getParameter('partid');
	}
	
	e.log().info("tool order " + tool + " partid " + partid);

	tool.addTask("build", partid);

	e.log().info("Order done");
}
