var $componentSelection = (function() {
	var $selection = get();

	function get() {
		return $EEstore.getObject('selection',{});
	}

	function save() {
		$EEstore.setObject('selection',$selection);
	}

	function remove($component) {
		delete $selection[$component];
		$('#'+getComponentId($component)+' .selection-title').text('Geen selectie');
		$('#'+getComponentId($component)).removeClass('selected');
		save();
	}

	function add($item) {
		$selection[$item.component] = $item;
		save();
		updateSelection($item);
		updatePrice();
	}

	function restore() {
		for(var $key in $selection) {
			updateSelection($selection[$key]);
		}
		updatePrice();
	}

	function clear() {
		for(var $key in $selection) {
			remove($key);
		}
		updatePrice();
	}

	function getComponentId($component) {
		if($component == 'Moederborden') {
			return 'moederbordselectie';
		} else if($component == 'Processoren') {
			return 'processorselectie';
		} else if($component == 'Koeling') {
			return  'processorkoelerselectie';
		} else if($component == 'Geheugen') {
			return 'geheugenselectie';
		} else if($component == 'Grafische kaarten') {
			return 'grafischekaartselectie';
		} else if($component.indexOf('schijven') != -1) {
			return 'hardeschijfselectie';
		} else if($component == 'Behuizingen') {
			return 'behuizingselectie';
		} else if($component == 'Voedingen') {
			return 'voedingselectie';
		} else if($component == 'Besturingssystemen') {
			return 'besturingssysteemselectie';
		}
	}

	function updateSelection($item) {
		$('#'+getComponentId($item.component)+' .selection-title').text($item.name);
		$('#'+getComponentId($item.component)).addClass('selected');
	}

	function updatePrice() {
		var $key,$price,
		$euro = 0,
		$cent = 0;
		for($key in $selection) {
			$euro += $selection[$key].euro;
			$cent += $selection[$key].cent;
		}
		if($cent >= 100) {
			$euro += Math.floor($cent/100);
			$cent %= 100;
		}
		$price = getPriceString($euro,$cent).split(',');
		$('.totaal-prijs .euro').text($price[0]);
		$('.totaal-prijs .cent').text($price[1]);
		checkCompatibility();
	}

	function getPriceString($euro,$cent) {
		if($cent !== undefined) {
			return getPriceStringEC($euro,$cent);
		} else {
			$cent = $euro;
			$euro = Math.floor($euro);
			$cent = Math.round(($cent-$euro)*100);
			return getPriceStringEC($euro,$cent);
		}
	}

	function getPriceStringEC($euro,$cent) {
		var $out = $euro+',';
		if($cent === 0) {
			$out += '-';
		} else if($cent < 10) {
			$out += '0'+$cent;
		} else {
			$out += $cent;
		}
		return $out;
	}

	function checkCompatibility() {
		var $n,$incompatible = [];
		if($selection.Koeling !== undefined && $selection.Processoren !== undefined) {
			if($selection.Koeling.socket.indexOf($selection.Processoren.socket) == -1) {
				$incompatible.push('Koeling');
				$incompatible.push('Processoren');
			}
		}if($selection.Moederborden !== undefined && $selection.Processoren !== undefined){
		    if ($selection.Moederborden.socket.indexOf($selection.Processoren.socket) == -1){
		        $incompatible.push('Moederborden');
		        $incompatible.push('Processoren');
		    }
		}if($selection.Moederborden !== undefined && $selection.Behuizingen !== undefined){
         	if ($selection.Moederborden.bouwvorm.indexOf($selection.Behuizingen.bouwvorm) == -1){
         	    $incompatible.push('Moederborden');
         		$incompatible.push('Behuizingen');
         	}
        }if($selection.Koeling !== undefined && $selection.Behuizingen !== undefined){
            if ($selection.Koeling.hoogte.indexOf($selection.Behuizingen.hoogte) == -1){
                $incompatible.push('Koeling');
                $incompatible.push('Behuizingen');
            }

		$('#pcbuilder-selection .row > div > div').removeClass('warning');
		for($n=0;$n < $incompatible.length;$n++) {
			$('#'+getComponentId($incompatible[$n])).addClass('warning');
		}
	}

	function selectionPopUp() {
		var $key,
		$lightbox = $('#pcbuilder-lightbox'),
		$content = $lightbox.find('.content-wrapper > .content'),
		$table = $('<table class="search-results"><thead><tr><th>Naam</th><th>Component</th><th>Prijs</th><th>Website</th></tr></thead><tbody></tbody></table>'),
		$tbody = $table.find('tbody');
		$table.find('th').append(' <span class="glyphicon glyphicon-chevron-up"></span><span class="glyphicon glyphicon-chevron-down"></span>');
		$content.empty();
		$content.append($table);
		for($key in $selection) {
			$tbody.append(getPopUpItem($selection[$key]));
		}
		$table.tablesorter();
		if($key !== undefined) {
			$lightbox.removeClass('hidden').hide().fadeIn(1000);
		}
	}

	function getPopUpItem($item) {
		return '<tr><td>'+$item.name+'</td><td>'+$item.component+'</td><td>&euro; '+getPriceString($item.euro,$item.cent)+'</td><td><a href="'+$item.url+'">'+getSite($item.url)+'</a></td></tr>';
	}

	function getSite($url) {
		if($url.indexOf('alternate.nl') != -1) {
			return 'Alternate';
		} else if($url.indexOf('cdromland.nl') != 1) {
			return 'CD-ROM-LAND';
		} else {
			return 'de website';
		}
	}

	function showSpecs($item) {
		var $key,$comp,$prices,$tabs,
		$lightbox = $('#pcbuilder-lightbox'),
		$content = $lightbox.find('.content-wrapper > .content');
		$content.empty();
		$content.append('<ul class="nav nav-tabs"><li class="active"><a href="#pcbuilder-item-specs">Component</a></li><li><a href="#pcbuilder-item-prices">Prijsverloop</a></li></ul>');
		$tabs = $('<div class="tab-content"></div>');
		$comp = $('<div id="pcbuilder-item-specs" class="tab-pane active"></div>');
		$comp.append('<h1>'+$item.name+'</h1>');
		for($key in $item) {
			if($key != 'name' && $key != 'euro' && $key != 'cent' && $key != 'crawlDate' && $key != 'url') {
				$comp.append('<dl><dt class="text-capitalize">'+$key+'</dt><dd>'+$item[$key]+'</dd></dl>');
			}
		}
		$comp.append('<div class="row"><div class="col-sm-10"><a href="'+$item.url+'">Bekijk dit product op '+getSite($item.url)+'.</a></div><div class="col-sm-2 text-right"><strong>&euro; '+getPriceString($item.euro,$item.cent)+'</strong></div></div>');
		$prices = $('<div id="pcbuilder-item-prices" class="tab-pane" data-ean="'+$item.ean+'"><p>Prijzen worden geladen...</p><p class="loader"></p></div>');
		$tabs.append($comp);
		$tabs.append($prices);
		$content.append($tabs);
		$content.find('.nav-tabs a').click(function($e) {
			var $this = $(this);
			$e.preventDefault();
			$this.tab('show');
			if($this.attr('href') == '#pcbuilder-item-prices' && $content.find('.loader').length !== 0) {
				$webSocket.send({action: 'getPricesForComp', ean: $item.ean});
			}
		});
		$lightbox.removeClass('hidden').hide().fadeIn(1000);
	}

	$(function() {
		$('.pcbuilder-clear-selection').click(clear);
		$('.pcbuilder-show-selection').click(selectionPopUp);
	});

	return {add: add, remove: remove, getPriceString: getPriceString, restore: restore, showSpecs: showSpecs};
})();