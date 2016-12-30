<?php   
    /************************************************/
    /*                                              */
    /* 1. On se connecte à la DB                    */
    /*                                              */
    /************************************************/
    try
    {
        $pdo_options[PDO::ATTR_ERRMODE] = PDO::ERRMODE_EXCEPTION;
	    $bdd = new PDO('mysql:host=mysql.hostinger.fr;dbname=u698768157_proj', 'u698768157_user', 'RootPwd', $pdo_options);
    }
    catch (Exception $e)
    {
        RetournerErreur(1000);
    }
              
    /************************************************/
    /*                                              */
    /* 2. On force l'UTF-8                          */
    /*                                              */
    /************************************************/
    try
    {
        $sql="SET NAMES 'UTF8';";
        $bdd->exec($sql);
    }
    catch (Exception $e)
    {
        RetournerErreur(1001);
    }    
?>