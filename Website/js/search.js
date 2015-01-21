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
	$('.pcbuilder-search .tab-pane.active').submit();
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

	function getNumFilter($filters,$key,$selector) {
		var $val = getNumber($selector);
		if(!isNaN($val)) {
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
		if($filters.merk !== undefined) {
			getFilter($filters,'Socket','#processorkoeler .pcbuilder-processor-socket');
		}
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
		getFilter($filters,'socket','#hardeschijf .pcbuilder-hdd-interface');
		submitPart(this,$e,'searchhardeschijf','hardeschijf-minprijs','hardeschijf-maxprijs',$filters);
	}

	function submitCase($e) {
		var $filters = {
			component: 'Behuizingen'
		};
		getFilter($filters,'Bouwvorm','#behuizing .pcbuilder-case');
		submitPart(this,$e,'searchbehuizing','behuizing-minprijs','behuizing-maxprijs',$filters);
	}

	function submitPSU($e) {
		var $filters = {
			component: 'Voedingen'
		};
		getRangeFilter($filters,'Vermogen','#voeding .voeding-minwattage','#voeding .voeding-maxwattage');
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
		console.log($out);
		return $out;
	}

	function getSearchResultReceiver($form) {
		return function($msg) {
			var $results,$n,$json,$table,$tbody;
			if($msg.data !== undefined) {
				$json = getJSON($msg.data);
				if($json !== null && $json.resultaten !== undefined) {
					$results = $($form).parent().find('.search-results');
					$results.empty();
					$table = $('<table style="width:100%;"><thead><tr><th>Naam</th><th>Merk</th><th>Prijs</th><th></th></tr></thead></table>');
					$table.find('th').append(' <span class="glyphicon glyphicon-chevron-up"></span><span class="glyphicon glyphicon-chevron-down"></span>');
					$results.append($table);
					$tbody = $('<tbody></tbody>');
					$table.append($tbody);
					for($n=0;$n < $json.resultaten.length;$n++) {
						$tbody.append(getItemHTML($json.resultaten[$n]));
					}
					$table.tablesorter();
				}
			}
		};
	}

	function getJSON($string) {
		try {
			return JSON.parse($string);
		} catch($e) {
			return null;
		}
	}

	 function getComponentId($component){
        
        if ($component == 'Moederborden'){
            return 'moederbordselectie';
        } else if ($component == 'Processoren'){
            return 'processorselectie';
        } else if ($component == 'Koeling'){
            return  'processorkoelerselectie';
        } else if ($component == 'Geheugen'){
            return 'geheugenselectie';
        } else if ($component == 'Grafische kaarten'){
            return 'grafischekaartselectie';
        } else if ($component.indexOf('schijven') != -1){
            return 'hardeschijfselectie';
        } else if ($component == 'Behuizingen'){
            return 'behuizingselectie';
        } else if ($component == 'Voedingen'){
            return 'voedingselectie';
        } else if ($component == 'Besturingssystemen'){
            return 'besturingssysteemselectie'
        }
                
    }

	function getItemHTML($item) {
		var $zoekResultaten = $('<tr class="item"><td>'+$item.name+'</td><td>'+$item.brand+'</td><td>&euro; '+$item.euro+','+$item.cent+'</td><td class="text-right"><button class="btn btn-default" title="Voeg toe aan systeem"><span class="glyphicon glyphicon-shopping-cart" style="vertical-align:middle"></span></button></td></tr>');
        
        $zoekResultaten.find('.btn').click(function (){
            $('#'+getComponentId($item.component)+' .selection-title').text($item.name);
        });
        return $zoekResultaten;
	}
}
