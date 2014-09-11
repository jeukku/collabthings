function info() {
	return "assembly build";
}

function run(runenv, factory, values) {
	var log = runenv.log().instance("Assembly build");
	
	log.info("values " + values);
	
	var toolstate = values.get('tool');
	
	var partid = values.get('partid');
	if(partid==null) {
		partid = factory.getParameter('partid');
	}
	
	var poolid = values.get('poolid');
	if(poolid==null) {
		poolid = partid;
	}
	log.info("poolid " + poolid);
	
	var part = factory.getPart(partid);
	log.setInfo("Assembly build " + part.getName())	
	log.info("partid " + partid + " -> part " + part);
	
	var destpartstate = factory.newPart();
	
	log.info("Running through all subparts. Destination part " + destpartstate);
	_.each(part.getSubParts().toArray(), function(subpart) {
		var callvalues = values.copy();
		callvalues.put('subpart', subpart);
		callvalues.put('destpart', destpartstate);
		callvalues.put('partid', subpart.getPart().getID());
		factory.call('order', callvalues);
		
		moveandattach(runenv, factory, callvalues);
	});
	
	log.info("Assembly done " + destpartstate);
	
	var destpart = destpartstate.getPart();
	log.info("modelpart bean: " + part.getBean());
	log.info("destpart bean: " + destpart.getBean());
	
	var pool = factory.getPool();
	if(part.isAnEqualPart(destpart)) {
		log.info("Adding part to " + poolid + " -pool");
		pool.addPart(poolid, destpart);
	} else {
		log.info("Parts are not equal.");
		pool.addPart("trash", destpart);
	}
	
	log.info("Done.");
}

function moveandattach(runenv, factory, values) {
	factory.addTask("moveandattach", values).waitUntilFinished();
}
