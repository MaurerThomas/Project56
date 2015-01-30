function initSearch() {
	$('#moederbord form').submit(submitMotherboard);
	$('#processor form').submit(submitCPU);
	$('#processorkoeler form').submit(submitProcessorCooler);
	$('#geheugen form').submit(submitRAM);
	$('#grafischekaart form').submit(submitGPU);
	$('#hardeschijf form').submit(submitHDD);
	$('#behuizing form').submit(submitCase);
	$('#voeding form').submit(submitPSU);
	$('#besturingssysteem form').submit(submitOS);
	setTimeout(function() {
		$('.pcbuilder-search .tab-pane.active form').submit();
	},200);
	initSearch = undefined;

	function getNumber($selector) {
		return parseInt($($selector).val(),10);
	}

	function getFilter($filters,$key,$selector) {
		var $val = $($selector).val();
		if($val && $val != 'none') {
			$filters[$key] = $val;
		}
	}

	function getRangeFilter($filters,$key,$selectorMin,$selectorMax) {
		var $min = getNumber($selectorMin), $max = getNumber($selectorMax);
		if(!isNaN($min) && !isNaN($max)) {
			$filters[$key] = [$min,$max];
		}
	}

	function submitMotherboard($e) {
		var $filters = {
			component: 'Moederborden'
		};
		getFilter($filters,'Bouwvorm','#moederbord .pcbuilder-case');
		getFilter($filters,'merk','#moederbord .pcbuilder-processor-brand');
		if($filters.merk !== undefined) {
			getFilter($filters,'Socket','#moederbord .pcbuilder-processor-socket');
		}
		submitPart(this,$e,'searchmoederbord','moederbord-minprijs','moederbord-maxprijs',$filters);
	}

	function submitCPU($e) {
		var $filters = {
			component: 'Processoren'
		};
		getFilter($filters,'merk','#processor .pcbuilder-processor-brand');
		if($filters.merk !== undefined) {
			getFilter($filters,'Socket','#processor .pcbuilder-processor-socket');
		}
		submitPart(this,$e,'searchprocessor','processor-minprijs','processor-maxprijs',$filters);
	}

	function submitProcessorCooler($e) {
		var $filters = {
			component: 'Koeling'
		};
		getFilter($filters,'merk','#processorkoeler .pcbuilder-processor-brand');
		getFilter($filters,'max, hoogte CPU koeler');
		if($filters.merk !== undefined) {
			getFilter($filters,'Socket','#processorkoeler .pcbuilder-processor-socket');
			if($filters.Socket === undefined) {
				$filters.Socket = '';
				$('#processorkoeler .pcbuilder-processor-socket [data-parent="'+$filters.merk+'"]').each(function() {
					$filters.Socket += ' '+$(this).val();
				});
			}
		}
		delete $filters.merk;
		submitPart(this,$e,'searchprocessorkoeler','processorkoeler-minprijs','processorkoeler-maxprijs',$filters);
	}

	function submitRAM($e) {
		var $filters = {
			component: 'Geheugen'
		};
		getFilter($filters,'geheugentype','#geheugen .pcbuilder-ram');
		submitPart(this,$e,'searchgeheugen','geheugen-minprijs','geheugen-maxprijs',$filters);
	}

	function submitGPU($e) {
		var $filters = {
			component: 'Grafische kaarten'
		};
		getFilter($filters,'merk','#grafischekaart .pcbuilder-gpu-brand');
		getFilter($filters,'Aansluiting','#grafischekaart .pcbuilder-gpu-interface');
		submitPart(this,$e,'searchgrafischekaart','grafischekaart-minprijs','grafischekaart-maxprijs',$filters);
	}

	function submitHDD($e) {
		var $filters = {
			component: 'schijven'
		};
		getFilter($filters,'component','#hardeschijf .pcbuilder-hdd-type');
		getFilter($filters,'Interface','#hardeschijf .pcbuilder-hdd-interface');
		submitPart(this,$e,'searchhardeschijf','hardeschijf-minprijs','hardeschijf-maxprijs',$filters);
	}

	function submitCase($e) {
		var $filters = {
			component: 'Behuizingen'
		};
		getFilter($filters,'Bouwvorm','#behuizing .pcbuilder-case');
		getFilter($filters,'max, hoogte CPU koeler');
		submitPart(this,$e,'searchbehuizing','behuizing-minprijs','behuizing-maxprijs',$filters);
	}

	function submitPSU($e) {
		var $filters = {
			component: 'Voedingen'
		};
		getRangeFilter($filters,'Vermogen','#voeding-minwattage','#voeding-maxwattage');
		submitPart(this,$e,'searchvoeding','voeding-minprijs','voeding-maxprijs',$filters);
	}

	function submitOS($e) {
		var $filters = {
			component: 'Besturingssystemen'
		};
		submitPart(this,$e,'searchbesturingssysteem','besturingssysteem-minprijs','besturingssysteem-maxprijs',$filters);
	}

	function submitPart($form,$e,$nameId,$minId,$maxId,$filters) {
		var $val;
		$e.preventDefault();
		$val = $('#'+$nameId).val();
		if($val && $val.trim() !== '') {
			$filters.naam = $val;
		}
		getRangeFilter($filters,'price','#'+$minId,'#'+$maxId);
		handleSearch($form,$filters);
	}

	function handleSearch($form,$filters) {
		$webSocket.receive = getSearchResultReceiver($form);
		$webSocket.send({
			action: 'filter',
			filters: getFilters($filters)
		});
	}

	function getFilters($filters) {
		var $key,$out = [];
		for($key in $filters) {
			$out.push({
				key: $key,
				value: $filters[$key]
			});
		}
		return $out;
	}

	function getSearchResultReceiver($form) {
		var $f = $($form),
		$results = $f.parent().find('.search-results'),
		$submit = $('input[type="submit"]');
		$results.html('<p>Onderdelen worden geladen...</p><p class="loader"></p>');
		$submit.attr('disabled',true);
		return function($msg) {
			var $json;
			if($msg.data !== undefined) {
				$json = getJSON($msg.data);
				if($json !== null) {
					if($json.resultaten !== undefined) {
						$results.empty();
						if(!isNaN($json.resultaten.length) && $json.resultaten.length > 0) {
							displayResults($json.resultaten,$results);
						} else {
							$results.append('<p class="pcbuilder-no-results">Er zijn geen onderdelen gevonden met deze filters.</p>');
						}
						$submit.attr('disabled',false);
					} else {
						handleReceive($json);
					}
				}
			}
		};
	}

	function handleReceive($json) {
		if($json.pricesForComp !== undefined && $json.pricesForEAN !== undefined) {
			handlePricesForComp($json.pricesForComp,$json.pricesForEAN);
		}
	}

	function handlePricesForComp($prices,$ean) {
		var $graph,$date,$step,$height,$n,
		$sortedPrices = [],
		$high = -Infinity,
		$low = Infinity,
		$tab = $('#pcbuilder-item-prices[data-ean="'+$ean+'"]');
		if($tab.length !== 0) {
			for($date in $prices) {
				$prices[$date] = Math.round($prices[$date]*100)/100;
				if($prices[$date] < $low) {
					$low = $prices[$date];
				}
				if($prices[$date] > $high) {
					$high = $prices[$date];
				}
				$sortedPrices.push({price: $prices[$date], date: $date});
			}
			if($high !== -Infinity && $low !== Infinity) {
				$sortedPrices.sort(function($a,$b) {
					return $a.date.localeCompare($b.date);
				});
				$low = getMin($low);
				$high = $low+getMax($high-$low);
				$step = $high-$low;
				if($step === 0) {
					$step = 1;
				}
				$graph = $('<div class="pcbuilder-graph"></div>');
				for($n=0;$n < $sortedPrices.length;$n++) {
					$height = ($sortedPrices[$n].price-$low)/$step*100;
					$graph.append('<div class="data"><div class="bar"><div style="height: '+$height+'%" title="&euro; '+$componentSelection.getPriceString($sortedPrices[$n].price)+'"></div></div><div class="legend">'+$sortedPrices[$n].date+'</div></div>');
				}
				$tab.empty();
				$tab.append($graph);
			}
		}

		function getMin($value) {
			var $mult = getLogMult($value);
			return Math.floor($value/$mult)*$mult;
		}

		function getMax($value) {
			var $mult = getLogMult($value);
			return Math.ceil($value/$mult)*$mult;
		}

		function getLogMult($value) {
			return Math.pow(10,Math.ceil(Math.log($value)/Math.log(10)));
		}
	}

	function displayResults($resultaten,$results) {
		var $n,
		$table = $('<table style="width:100%;"><thead><tr><th>Naam</th><th>Merk</th><th>Prijs</th><th></th></tr></thead></table>'),
		$tbody = $('<tbody></tbody>');
		$table.find('th').append(' <span class="glyphicon glyphicon-chevron-up"></span><span class="glyphicon glyphicon-chevron-down"></span>');
		$results.append($table);
		$table.append($tbody);
		for($n=0;$n < $resultaten.length;$n++) {
			$tbody.append(getItemHTML($resultaten[$n]));
		}
		$table.tablesorter();
	}

	function getJSON($string) {
		try {
			return JSON.parse($string);
		} catch($e) {
			return null;
		}
	}

	function getItemHTML($item) {
		var $zoekResultaten = $('<tr class="item"><td>'+$item.name+'</td><td>'+$item.brand+'</td><td>&euro; '+$componentSelection.getPriceString($item.euro,$item.cent)+'</td><td class="text-right"><button class="btn btn-default" title="Voeg toe aan systeem"><span class="glyphicon glyphicon-shopping-cart" style="vertical-align:middle"></span></button></td></tr>');
		$zoekResultaten.find('.btn').click(function() {
			$componentSelection.add($item);
		});
		$zoekResultaten.find('td:first').click(function() {
			$componentSelection.showSpecs($item);
		});
		return $zoekResultaten;
	}
}
