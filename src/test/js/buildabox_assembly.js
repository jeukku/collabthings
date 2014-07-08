function info() {
	return "testing box -building";
}

function run(runenv) {
	var part = runenv.getOriginalPart(runenv.getParameter('partid'));
	var destinationpart = runenv.getPart('destinationpart');
	runenv.log().info("script going to a loop!!!");
	_.each(part.getSubParts().toArray(), function(subpart) {
		runenv.log().info('script test ' + subpart);
		moveAndAttach(runenv, subpart, destinationpart);
	});
	runenv.log().info("script end!!!");
}

function moveAndAttach(runenv, subpart, destpart) {
	runenv.addTask(runenv.getScript("MoveAndAttach"), subpart, destpart).waitUntilFinished();
}
