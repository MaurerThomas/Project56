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
	initSearch = undefined;

	function getFilter($filters,$key,$selector) {
		var $val = $($selector).val();
		if($val && $val != 'none') {
			$filters[$key] = $val;
		}
	}

	function getNumFilter($filters,$key,$selector) {
		var $val = parseInt($($selector).val(),10);
		if(!isNaN($val)) {
			$filters[$key] = $val;
		}
	}

	function submitMotherboard($e) {
		var $filters = {
			component: 'moederbord'
		};
		getFilter($filters,'behuizing','#moederbord .pcbuilder-case');
		getFilter($filters,'merk','#moederbord .pcbuilder-processor-brand');
		if($filters.merk !== undefined) {
			getFilter($filters,'socket','#moederbord .pcbuilder-processor-socket');
		}
		submitPart($e,'searchmoederbord','moederbord-minprijs','moederbord-maxprijs',$filters);
	}

	function submitCPU($e) {
		var $filters = {
			component: 'processor'
		};
		getFilter($filters,'merk','#processor .pcbuilder-processor-brand');
		if($filters.merk !== undefined) {
			getFilter($filters,'socket','#processor .pcbuilder-processor-socket');
		}
		submitPart($e,'searchprocessor','processor-minprijs','processor-maxprijs',$filters);
	}

	function submitProcessorCooler($e) {
		getFilter($filters,'merk','#processorkoeler .pcbuilder-processor-brand');
		if($filters.merk !== undefined) {
			getFilter($filters,'socket','#processorkoeler .pcbuilder-processor-socket');
		}
		submitPart($e,'searchprocessorkoeler','processorkoeler-minprijs','processorkoeler-maxprijs',$filters);
	}

	function submitRAM($e) {
		var $filters = {
			component: 'geheugen'
		};
		getFilter($filters,'geheugentype','#geheugen .pcbuilder-ram');
		submitPart($e,'searchgeheugen','geheugen-minprijs','geheugen-maxprijs',$filters);
	}

	function submitGPU($e) {
		var $filters = {
			component: 'grafischekaart'
		};
		getFilter($filters,'merk','#grafischekaart .pcbuilder-gpu-brand');
		getFilter($filters,'socket','#grafischekaart .pcbuilder-gpu-interface');
		submitPart($e,'searchgrafischekaart','grafischekaart-minprijs','grafischekaart-maxprijs',$filters);
	}

	function submitHDD($e) {
		var $filters = {
			component: 'hardeschijf'
		};
		getFilter($filters,'hardeschijftype','#hardeschijf .pcbuilder-hdd-type');
		getFilter($filters,'socket','#hardeschijf .pcbuilder-hdd-interface');
		submitPart($e,'searchhardeschijf','hardeschijf-minprijs','hardeschijf-maxprijs',$filters);
	}

	function submitCase($e) {
		var $filters = {
			component: 'behuizing'
		};
		getFilter($filters,'behuizing','#behuizing .pcbuilder-case');
		submitPart($e,'searchbehuizing','behuizing-minprijs','behuizing-maxprijs',$filters);
	}

	function submitPSU($e) {
		var $filters = {
			component: 'voeding'
		};
		getNumFilter($filters,'minwattage','#voeding .voeding-minwattage');
		getNumFilter($filters,'maxwattage','#voeding .voeding-maxwattage');
		submitPart($e,'searchvoeding','voeding-minprijs','voeding-maxprijs',$filters);
	}

	function submitOS($e) {
		var $filters = {
			component: 'besturingssysteem'
		};
		submitPart($e,'searchbesturingssysteem','besturingssysteem-minprijs','besturingssysteem-maxprijs',$filters);
	}

	function submitPart($e,$nameId,$minId,$maxId,$filters) {
		var $val;
		$e.preventDefault();
		$val = $('#'+$nameId).val();
		if($val && $val.trim() !== '') {
			$filters.naam = $val;
		}
		getNumFilter($filters,'minPrice','#'+$minId);
		getNumFilter($filters,'maxPrice','#'+$maxId);
		console.log($filters);
	}
}