package be.mousty.asychronious;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import be.mousty.intent.LoginActivity;

//http://stackoverflow.com/questions/6053602/what-arguments-are-passed-into-asynctaskarg1-arg2-arg3
// On remplace les XYZ par le type objet nécéssaire
// X -> Recu en paramètre  du doInBackground
// Y -> non utilisé, pour le onProgressUpdate
// Z -> Résultat, placé dans onPostExecute
public class LoginAsynchronious extends AsyncTask<String, Void , String> {

    private LoginActivity screen = null;

    /*@Override protected void onPreExecute() {
        // Prétraitement de l'appel
    }

    @Override protected void onProgressUpdate(Y... progress) {
        // Gestion de l'avancement de la tâche
    }*/

    public LoginAsynchronious(LoginActivity s) {
        screen = s;
    }

    @Override protected String doInBackground(String... params) {
        String strRep = params[1] + " " + params[3];
        try {
            URL url = new URL("http://lesqua.16mb.com/projet_android/se_connecter.php?");
            //pseudo=" + params[0] + "&mdp="+ params[1]

            // instantier l'objet grâce à la méthode "openConnection()"
            HttpURLConnection connection;

            // effectuer une connexion au serveur web spécifié dans l'URL. Retourne un objet de type "URLConnexion".
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            // On envoie les paramètres parr la suite
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            //String parametres_post ="";
            writer.write(getPostDataString(params));
            writer.flush();
            writer.close();

            // Fixer le nombre de milisecondes qu'on est prêt à attendre pour effectuer une connection.
            connection.setConnectTimeout(10000);

            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == 200)
            {
                InputStream in =  new BufferedInputStream(connection.getInputStream());

                // Retourne la réponse du serveur

                connection.disconnect();
                Scanner scanner = new Scanner(in);

                if (scanner.hasNext()){
                    String error_txt = scanner.next();
                    switch (error_txt){
                        case "0"   : strRep = "OK"; break;
                        case "100"  : strRep = "Problème avec le pseudo"; break;
                        case "110"  : strRep = "Problème avec le mdp"; break;
                        case "200"  : strRep = "Combinaison login/password incorrecte"; break;
                        case "1000" : strRep = "Problème de connexion à la DB"; break;
                        default     : strRep =  error_txt; break;
                    }
                }

            }
            else{ strRep = "ResponseCode différent de 200"; }
            // Déconnecte la connection


            connection.disconnect();
        }
        catch (MalformedURLException e) { e.printStackTrace(); strRep = e.getMessage(); }
        catch (Exception e){ e.getStackTrace(); strRep = e.getMessage();}
        return strRep;
    }

    @Override
    protected void onPostExecute(String result) {
        // Callback
        // Renvoie les informations dans la fonction populate du mainactivity
        try { screen.populate_connect(result); }
        catch (Exception e) { e.getStackTrace(); }
    }

    // Pour manipuler une liste de paramètres tel que l'username et le password
    private String getPostDataString(String[] params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(int i = 0; i < params.length; i = i+2){
            if (first)
                first = false;
            else
                result.append("&");

            // Chaque élément pair contient la clé 'pseudo', 'mdp' et les éléments impairs représentent la valeur
            result.append(URLEncoder.encode(params[i], "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params[i+1], "UTF-8"));
        }

        return result.toString();
    }
}