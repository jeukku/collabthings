function info() {
	return "testing box -building";
}

function run(runenv, params) {
	var log = runenv.log();
	
	log.info("params " + params);
	
	var toolstate = params[0];
	
	var partid = params[1];
	log.info("partid " + partid)
	var part = runenv.getPart(partid);
	
	var destpartstate = runenv.newPart();
	
	log.info("Running through all subparts. Destination part " + destpartstate);
	_.each(part.getSubParts().toArray(), function(subpart) {
		log.info('script test ' + subpart);
		moveandattach(runenv, subpart, destpartstate);
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

function moveandattach(runenv, subpart, destpart) {
	runenv.addTask(runenv.getScript("moveandattach"), subpart, destpart).waitUntilFinished();
}
