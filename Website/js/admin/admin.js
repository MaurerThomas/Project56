(function() {
	$(window).on('hashchange',function() {
		var $page = window.location.hash.replace('#','');
		if($page === '' || $page == 'main') {
			$webSocket.send({switchDashboard: 'main'});
		} else if(['adminfunctions'].indexOf($page) != -1) {
			$webSocket.send({switchDashboard: $page});
		} else if (['grafieken'].indexOf($page) != -1) {
			$webSocket.send({switchDashboard: $page});
		}
	});
})();
