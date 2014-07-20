function info() {
	return "testing box -building";
}

function run(runenv, boxfactory) {
	var log = runenv.log();
	
	var part = runenv.getPart(runenv.getParameter('partid'));
	
	var destpartstate = runenv.newPart();
	
	log.info("Running through all subparts. Destination part " + destpartstate);
	_.each(part.getSubParts().toArray(), function(subpart) {
		log.info('script test ' + subpart);
		moveAndAttach(runenv, subpart, destpartstate);
	});
	
	log.info("Assembly done " + destpartstate);
	
	var destpart = destpartstate.getPart();
	log.info("modelpart bean: " + part.getBean());
	log.info("destpart bean: " + destpart.getBean());
	
	var pool = runenv.getPool();
	if(part.isAnEqualPart(destpart)) {
		log.info("Adding part to box -pool");
		pool.addPart("box", destpart);
	} else {
		log.info("Parts are not equal.");
		pool.addPart("trash", destpart);
	}
	
	log.info("script end!!!");
}

function moveAndAttach(runenv, subpart, destpart) {
	runenv.addTask(runenv.getScript("MoveAndAttach"), subpart, destpart).waitUntilFinished();
}
