function info() {
	return "testing box -building";
}

function run(e) {
	var part = e.getPart(e.getParameter('partid'));
	var destinationpart = e.getPart('destinationpart');
	e.log().info("script going to a loop!!!");
	_.each(part.getSubParts().toArray(), function(subpart) {
		e.log().info('script test ' + subpart);
		moveAndAttach(e, subpart, destinationpart);
	});
	e.log().info("script end!!!");
}

function moveAndAttach(e, subpart, destpart) {
	e.addTask(e.getScript("MoveAndAttach"), subpart, destpart);
}
