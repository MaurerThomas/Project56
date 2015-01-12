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
		var $val = $('.pcbuilder-search .pcbuilder-case').val();
		if($val && $val != 'none') {
			$filters[$key] = $val;
		}
	}

	function submitMotherboard($e) {
		var $filters = {
			component: 'moederbord'
		};
		getFilter($filters,'behuizing','#moederbord .pcbuilder-case');
		getFilter($filters,'merk','#moederbord .pcbuilder-processor-brand');
		if($filter.merk !== undefined) {
			getFilter($filters,'socket','#moederbord .pcbuilder-processor-socket');
		}
		submitPart($e,'searchmoederbord','moederbord-minprijs','moederbord-maxprijs',$filters);
	}

	function submitCPU($e) {
		var $filters = {
			component: 'processor'
		};
		getFilter($filters,'merk','#processor .pcbuilder-processor-brand');
		if($filter.merk !== undefined) {
			getFilter($filters,'socket','#processor .pcbuilder-processor-socket');
		}
		submitPart($e,'searchprocessor','processor-minprijs','processor-maxprijs',$filters);
	}

	function submitProcessorCooler($e) {
		getFilter($filters,'merk','#processorkoeler .pcbuilder-processor-brand');
		if($filter.merk !== undefined) {
			getFilter($filters,'socket','#processorkoeler .pcbuilder-processor-socket');
		}
		submitPart($e,'searchprocessorkoeler','processorkoeler-minprijs','processorkoeler-maxprijs',$filters);
	}

	function submitRAM($e) {
		var $filters = {
			component: 'geheugen'
		};
		getFilter($filter,'geheugentype','#geheugen .pcbuilder-ram');
		submitPart($e,'searchgeheugen','geheugen-minprijs','geheugen-maxprijs',$filters);
	}

	function submitGPU($e) {
		var $filters = {
			component: 'grafischekaart'
		};
		submitPart($e,'searchgrafischekaart','grafischekaart-minprijs','grafischekaart-maxprijs',$filters);
	}

	function submitHDD($e) {
		var $filters = {
			component: 'hardeschijf'
		};
		submitPart($e,'searchhardeschijf','hardeschijf-minprijs','hardeschijf-maxprijs',$filters);
	}

	function submitCase($e) {
		var $filters = {
			component: 'behuizing'
		};
		submitPart($e,'searchbehuizing','behuizing-minprijs','behuizing-maxprijs',$filters);
	}

	function submitPSU($e) {
		var $filters = {
			component: 'voeding'
		};
		submitPart($e,'searchvoeding','voeding-minprijs','voeding-maxprijs',$filters);
	}

	function submitOS($e) {
		var $filters = {
			component: 'besturingssysteem'
		};
		submitPart($e,'searchbesturingssysteem','besturingssysteem-minprijs','besturingssysteem-maxprijs',$filters);
	}

	function submitPart($e,$nameId,$minId,$maxId,$filters) {
		$e.preventDefault();
	}
}