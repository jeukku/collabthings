function run(env, factory, values) {
	env.log().info('calling order ' + factory + ' values ' + values);
	factory.call('order');
}

function info() {
	return 'calling order';
}
