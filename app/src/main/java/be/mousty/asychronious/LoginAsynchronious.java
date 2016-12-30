package be.mousty.asychronious;

import android.app.ProgressDialog;
import android.content.Context;
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
public class LoginAsynchronious extends AsyncTask<String, Void , ArrayList<String>> {

    private LoginActivity screen = null;
    ProgressDialog progress;
    public LoginAsynchronious(LoginActivity s) {
        screen = s;
        progress = new ProgressDialog(screen);
    }



    @Override protected void onPreExecute() {
        // Prétraitement de l'appel
        progress.setTitle("WAIT PLEASE");
        progress.setMessage("WE ARE CURRENTLY RETREIVING YOUR DATA...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    /*@Override protected void onProgressUpdate(Y... progress) {
        // Gestion de l'avancement de la tâche
    }*/



    @Override protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> listRep = new ArrayList<String>();
        try {
            URL url = new URL("http://lesqua.16mb.com/projet_android/se_connecter.php?");

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

                if (jsonReader.hasNext()) {
                    String label = jsonReader.nextName();
                    int code = jsonReader.nextInt();
                    switch (code) {
                        case 0   :
                            listRep.add("OK");
                            if (jsonReader.hasNext()) {
                                label = jsonReader.nextName();
                                int id = jsonReader.nextInt();
                                listRep.add(id + "");
                            }
                            break;
                        case 100  : listRep.add("Problème avec le pseudo"); break;
                        case 110  : listRep.add("Problème avec le mdp"); break;
                        case 200  : listRep.add("Combinaison login/password incorrecte"); break;
                        case 1000 : listRep.add("Problème de connexion à la DB"); break;
                        default     : listRep.add(code + ""); break;
                    }
                }
                // Fin de la récuperation
                jsonReader.endObject();
            }
            else{ listRep.add("ResponseCode différent de 200"); }
            connection.disconnect();

                /*InputStream in =  new BufferedInputStream(connection.getInputStream());

                // Retourne la réponse du serveur

                connection.disconnect();
                Scanner scanner = new Scanner(in);

                if (scanner.hasNext()){
                    String error_txt = scanner.next();
                    switch (error_txt){
                        case "0"   :
                            listRep.add("OK");

                            break;
                        case "100"  : listRep.add("Problème avec le pseudo"); break;
                        case "110"  : listRep.add("Problème avec le mdp"); break;
                        case "200"  : listRep.add("Combinaison login/password incorrecte"); break;
                        case "1000" : listRep.add("Problème de connexion à la DB"); break;
                        default     : listRep.add(error_txt); break;
                    }
                }

            }
            else{ listRep.add("ResponseCode différent de 200"); }
            // Déconnecte la connection


            connection.disconnect();*/
        }
        catch (MalformedURLException e) { e.printStackTrace(); listRep.add(e.getMessage()); }
        catch (Exception e){ e.getStackTrace(); listRep.add(e.getMessage());}
        return listRep;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        // Callback
        // Renvoie les informations dans la fonction populate du mainactivity
        try {
            if(progress.isShowing()) { progress.dismiss(); }
            screen.populate_connect(result);
        }
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