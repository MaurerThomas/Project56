(function($) {
	$(function() {
		function attemptToRestoreConnection($msg) {
			var $reconnectAttempts = 0,
			$timeout = 500,
			$lightbox = $('#pcbuilder-lightbox'),
			$content = $lightbox.find('.content');
			$content.empty();
			if($webSocket.getReadyState() != 3) {
				return;
			}
			$content.html('<h1 class="text-danger"><span class="glyphicon glyphicon-warning-sign"></span> De verbinding met de server is verbroken.</h1><p>Er is geen data verloren gegaan. We proberen de verbinding te herstellen...</p>');
			$lightBoxButton.attr('disabled',true);
			if($lightbox.hasClass('hidden') || $lightbox.is(':hidden')) {
				$lightbox.removeClass('hidden').hide().fadeIn(1000);
			}
			tryToConnect();
			$webSocket.receiveClose = function() {};

			function tryToConnect() {
				if($reconnectAttempts < 5) {
					$reconnectAttempts++;
					$webSocket.init(window.location.host,'8080','/search');
					setTimeout(checkConnection,$timeout);
				} else {
					$content.html('<h1 class="text-danger"><span class="glyphicon glyphicon-warning-sign"></span> De verbinding met de server is verbroken.</h1><p>Er is geen data verloren gegaan. De verbinding kon niet worden hersteld.</p>');
				}
			}

			function checkConnection() {
				$timeout *= 2;
				if($webSocket.getReadyState() === 1) {
					$content.html('<h1 class="text-success">De verbinding is hersteld.</h1>');
					$lightBoxButton.attr('disabled',false);
					$webSocket.receiveClose = attemptToRestoreConnection;
					$reconnectAttempts = 0;
					$timeout = 500;
				} else {
					tryToConnect();
				}
			}
		}

		var $button = $('#start a.btn'),
		$compatibility = checkCompatibility($button),
		$lightBoxButton = $('#pcbuilder-lightbox button[title="Close"]');
		$button.click(function($e) {
			$e.preventDefault();
			initFilters();
			$('#start').hide();
			$('#pcbuilder-loading').removeClass();
			$("#pcbuilder-loading").show().delay(1500).queue(function(n) {
			  $(this).hide();
			  n();
			  $('#pcbuilder').removeClass();
			});
		});
		if($compatibility.WebSocket) {
			$webSocket.init(window.location.host,'8080','/search');
			$webSocket.receiveClose = attemptToRestoreConnection;
		}
		$('#pcbuilder-selection-header h2 a.btn').click(function($e) {
			var $btn = $(this);
			$e.preventDefault();
			if($btn.hasClass('glyphicon-chevron-up')) {
				$btn.removeClass('glyphicon-chevron-up');
				$btn.addClass('glyphicon-chevron-down');
				$btn.attr('title','Geef uw selectie weer');
			} else {
				$btn.removeClass('glyphicon-chevron-down');
				$btn.addClass('glyphicon-chevron-up');
				$btn.attr('title','Verberg uw selectie');
			}
			$('#pcbuilder-selection').animate({height: 'toggle'});
		});
		
		$('#moederbordselectie').click(function() {
			$('a[href="#moederbord"]').click();
		});
		$('#processorselectie').click(function() {
			$('a[href="#processor"]').click();
		});
		$('#processorkoelerselectie').click(function() {
			$('a[href="#processorkoeler"]').click();
		});
		$('#hardeschijfselectie').click(function() {
			$('a[href="#hardeschijf"]').click();
		});
		$('#geheugenselectie').click(function() {
			$('a[href="#geheugen"]').click();
		});
		$('#grafischekaartselectie').click(function() {
			$('a[href="#grafischekaart"]').click();
		});
		$('#voedingselectie').click(function() {
			$('a[href="#voeding"]').click();
		});
		$('#behuizingselectie').click(function() {
			$('a[href="#behuizing"]').click();
		});
		$('#besturingssysteemselectie').click(function() {
			$('a[href="#besturingssysteem"]').click();
		});
		$('.pcbuilder-tabs li:not(.active) a').click(loadInitialItems);
		$lightBoxButton.click(function($e) {
			var $lightbox = $('#pcbuilder-lightbox');
			$e.preventDefault();
			$lightbox.fadeOut(1000,function() {
				$lightbox.addClass('hidden');
			});
		});
		$(window).keyup(function($e) {
			if($e.key == 'Esc' || $e.keyCode == 27 || $e.which == 27) {
				$lightBoxButton.click();
			}
		});

		if($EEstore.getItem('selection',false) !== false) {
			$componentSelection.restore();
			setTimeout(function() {$button.click();},200);
		}
	});

	function loadInitialItems() {
		var $this = $(this);
		$($this.attr('href')+' form').submit();
		$this.unbind('click',loadInitialItems);
	}

	$(document).tooltip({position: {my: 'left top', at: 'right top'}});
})(jQuery);