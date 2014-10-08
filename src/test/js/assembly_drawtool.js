function info() {
	return "assembly: Draw tool";
}

function run(runenv, tool, values) {

	var view = values.get('view');
	var tstack = values.get('tstack');
	var tool = values.get('tool');

	var p = tool.getPool().peekPart("pickedup");
	if (p) {
		var log = runenv.log().instance("draw");
		log.info("view " + view);
		log.info("tstack " + tstack);
		log.info("tool " + tool);
		log.info("p " + p);
		view.drawPart(tstack, tool, p);
	}
}
