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
	}

	function getPriceString($euro,$cent) {
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

	$(function() {
		$('.pcbuilder-clear-selection').click(clear);
	});

	return {add: add, remove: remove, getPriceString: getPriceString, restore: restore};
})();