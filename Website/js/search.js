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

	function submitMotherboard($e) {
		$e.preventDefault();
	}

	function submitCPU($e) {
		$e.preventDefault();
	}

	function submitProcessorCooler($e) {
		$e.preventDefault();
	}

	function submitRAM($e) {
		$e.preventDefault();
	}

	function submitGPU($e) {
		$e.preventDefault();
	}

	function submitHDD($e) {
		$e.preventDefault();
	}

	function submitCase($e) {
		$e.preventDefault();
	}

	function submitPSU($e) {
		$e.preventDefault();
	}

	function submitOS($e) {
		$e.preventDefault();
	}
}