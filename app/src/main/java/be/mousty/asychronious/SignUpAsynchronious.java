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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import be.mousty.intent.LoginActivity;

//http://stackoverflow.com/questions/6053602/what-arguments-are-passed-into-asynctaskarg1-arg2-arg3
// On remplace les XYZ par le type objet nécéssaire
// X -> Recu en paramètre  du doInBackground
// Y -> non utilisé, pour le onProgressUpdate
// Z -> Résultat, placé dans onPostExecute
public class SignUpAsynchronious extends AsyncTask<String, Void , ArrayList<String>> {
    private LoginActivity screen = null;

    /*@Override protected void onPreExecute() {
        // Prétraitement de l'appel
    }

    @Override protected void onProgressUpdate(Y... progress) {
        // Gestion de l'avancement de la tâche
    }*/

    public SignUpAsynchronious(LoginActivity s) {
        screen = s;
    }

    @Override protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> strRep = new ArrayList<String>();
        try {
            URL url = new URL("http://lesqua.16mb.com/projet_android/creer_compte.php");
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
                // Retourne la réponse du serveur
                InputStream in = connection.getInputStream();

                // Recuperation du texte au format JSON
                JsonReader jsonReader = new JsonReader(new InputStreamReader(in, "UTF-8"));

                // Début de la récuperation
                jsonReader.beginObject();

                if (jsonReader.hasNext()){
                    String label = jsonReader.nextName();
                    int code = jsonReader.nextInt();

                    switch (code){
                        case 0    : strRep.add("OK"); break;
                        case 100  : strRep.add("Problème avec le pseudo"); break;
                        case 110  : strRep.add("Problème avec le mdp"); break;
                        case 200  : strRep.add("Login déjà existant"); break;
                        case 1000 : strRep.add("Problème de connexion à la DB"); break;
                        default   : strRep.add("~> " + code); break;
                    }

                    if(strRep.equals("OK")){
                        // On lui ajoute aussi l'ID
                        if (jsonReader.hasNext()){
                            String lbl = jsonReader.nextName();
                            int id = jsonReader.nextInt();
                            strRep.add(id + "");
                        }
                    }
                }

                // Fin de la récuperation
                jsonReader.endObject();
                connection.disconnect();
            }
            else{ strRep.add("ResponseCode différent de 200"); }
            // Déconnecte la connection
            connection.disconnect();
        }
        catch (MalformedURLException e) { e.printStackTrace(); strRep.add(e.getMessage()); }
        catch (Exception e){ e.getStackTrace(); strRep.add(e.getMessage());}
        return strRep;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        // Callback
        // Renvoie les informations dans la fonction populate du mainactivity
        try { screen.populate_sign_in(result); }
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