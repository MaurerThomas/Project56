(function() {
	$(window).on('hashchange',function() {
		var $page = window.location.hash.replace('#','');
		if(['adminfunctions'].indexOf($page) != -1) {
			$webSocket.send({switchDashboard: $page});
		}
	});
})();