<?php
	function tester_session(){
		if (!isset($_SESSION))
			return 0;
		else
			return 1;
	}

	if (tester_session()==0){
		RetournerErreur(500);
	}
?>