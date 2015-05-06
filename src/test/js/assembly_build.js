function info() {
	return "assembly build";
}

function run(runenv, factory, values) {
	var log = runenv.log().instance("Assembly build");

	log.info("values " + values);

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

	while (pool.countPool(poolid) < 2) {

		var toolstate = values.get('tool');

		var partid = values.get('partid');
		if (partid == null) {
			partid = factory.getParameter('partid');
		}

		var part = factory.getPart(partid);
		log.setInfo("Assembly build " + part.getName())
		log.info("poolid " + poolid);
		log.info("partid " + partid + " -> part " + part);
		log.info("factory " + factory);

		var destpartstate = factory.newPart();
		destpartstate.setLocation(factory.getVector('buildingpartlocation'));

		log.info("Running through all subparts. Destination part "
				+ destpartstate);
		_.each(part.getSubParts().toArray(), function(subpart) {
			var callvalues = values.copy();
			callvalues.put('subpart', subpart);
			callvalues.put('destpart', destpartstate);
			callvalues.put('partid', subpart.getPart().getID());
			factory.getFactory("source").call('order', callvalues);

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

				if (ab.length() < 0.01) {
					return true;
				} else {
					ab.normalize();
					ab.scale(step * 3);
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
