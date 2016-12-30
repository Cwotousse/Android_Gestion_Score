<?php
	session_start();
	/* ETAPE 0 : TINCLUDE DE FONCTIONS ET PARAMETRAGE */
	$GLOBALS['json']=1;
	include('inc/erreurs.inc');



	/* ETAPE 1 : TEST DES PARAMETRES ET DE LA SESSION */
	include('inc/session.inc');
	if (!isset($_GET['jeu']) || empty($_GET['jeu'])){
		RetournerErreur(100);
	}
	$jeu = $_GET['jeu'];



	/* ETAPE 2 : CONNEXION A LA BASE DE DONNEES */
	include('inc/db.inc');



	/* ETAPE 3 : RECUPERATION DU TOP10 DES SCORES*/
	// 3.1. On exÃ©cute la requÃªte
	// DU PLUS GRAND AU PLUS PETIT
	try{	
		$requete="SELECT pseudo, score FROM scores S INNER JOIN utilisateurs U ON S.id_utilisateur = U.id_utilisateur
				  WHERE jeu=? ORDER BY score ASC";
		$stm= $bdd->prepare($requete);
		$stm->execute(array($jeu));	
	}catch(Exception $e){
		RetournerErreur(2004);
	}

	// 3.2. On vÃ©rifie si on a trouvÃ© au moins un score
	if($row = $stm->fetch()){
		$chaine = '{"pseudo": "' . $row["pseudo"] . '", "score": ' . $row["score"] . '}';
	}else{
		RetournerErreur(300);
	}

	// 3.3. On construit le fichier JSON des jeux
	// ON NE MET QUE LES 10 PREMIERS ELEMENTS CEST UN TOP 10
    $i = 0;
	while ($i < 10 AND $row = $stm->fetch()) {
		$chaine .= ', {"pseudo": "' . $row["pseudo"] . '", "score": ' . $row["score"] . '}';
        $i++;
	}



	/* ETAPE 4 : SI ON EST ARRIVE JUSQU'ICI, C'EST QUE TOUT EST CORRECT */
	$resultat='{ "code": 0, "joueurs": [' . $chaine . '] }';
	echo $resultat;


	/* Valeurs de retour
	 * 00 : OK
	 * 100 : Nom du jeu non transmis
	 * 300 : Aucun score trouvÃ©
	 * 1000 : problÃ¨me de connexion Ã  la DB
	 * 20XX : autre problÃ¨me
	 */
?>
