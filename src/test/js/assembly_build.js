function info() {
	return "assembly build";
}

function run(runenv, factory, values) {
	var log = runenv.log().instance("Assembly build");

	log.info("values " + values);

	var partid = values.get('partid');
	if (partid == null) {
		partid = factory.getParameter('partid');
	}

	var pool = factory.getPool();
	var poolid = values.get('poolid');
	if (poolid == null) {
		poolid = partid;
	}

	factory.stepWhile(function(step) {
		var currentbusy = "" + factory.getStateParameter("busy");
		return currentbusy != "true";
	});

	factory.setStateParameter("busy", "true");

	var fillpool = factory.getParameter("fillpool")
	if(fillpool==null) {
		fillpool = 1;
	}
	
	while (pool.countParts(poolid) < fillpool) {
		log.info("count pool " + poolid + " " + pool.countParts(poolid));
		log.info("pool " + pool.printOut());

		var toolstate = values.get('tool');

		var part = factory.getPart(partid);
		log.setInfo("Assembly build " + part.getName())
		log.info("poolid " + poolid);
		log.info("partid " + partid + " -> part " + part);
		log.info("factory " + factory);

		var destpartstate = factory.newPart();
		destpartstate.setLocation(factory.getVector('buildingpartlocation'));

		log.info("Running through all subparts. Destination part "
				+ destpartstate + " subparts:" + part.getSubParts());
		_.each(part.getSubParts().toArray(), function(subpart) {
			var callvalues = values.copy();
			callvalues.put('subpart', subpart);
			callvalues.put('destpart', destpartstate);
			callvalues.put('partid', subpart.getPart().getID());
			
			log.info("Running through all subparts: calling order with " + callvalues);
			factory.getFactory("source").call('order', callvalues);
			
			if (factory.getFactory("source2") != null) {
				factory.getFactory("source2").call('order', callvalues);
			}
			
			moveandattach(runenv, factory, callvalues);
		});

		log.info("Assembly done " + destpartstate);

		var destpart = destpartstate.getPart();

		if (part.isAnEqualPart(destpart)) {
			log.info("Adding part to " + poolid + " -pool");

			destlocation = factory.getVector("storage");
			factory.stepWhile(function(step) {
				var partl = destpartstate.getLocation();
				var ab = destlocation.getSub(partl);

				var movedistance = step  * 10;
				if (ab.length() < movedistance) {
					return true;
				} else {
					ab.normalize();
					ab.scale(movedistance);
					destpartstate.setLocation(partl.getAdd(ab));
					return false;
				}
			});

			pool.addPart(poolid, part);
			destpart.destroy();
		} else {
			log.info("Parts are not equal.");
			pool.addPart("trash", destpart);
		}

		destpartstate.destroy();
	}

	factory.setStateParameter("busy", "false");

	log.info("Done.");
}

function moveandattach(runenv, factory, values) {
	factory.addTask("moveandattach", values).waitUntilFinished();
}
