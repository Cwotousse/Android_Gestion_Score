package be.mousty.asychronious;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import be.mousty.intent.AddScoreActivity;

//http://stackoverflow.com/questions/6053602/what-arguments-are-passed-into-asynctaskarg1-arg2-arg3
// On remplace les XYZ par le type objet nécéssaire
// X -> Recu en paramètre  du doInBackground
// Y -> non utilisé, pour le onProgressUpdate
// Z -> Résultat, placé dans onPostExecute
public class AddScoreAsynchronious extends AsyncTask<String, Void, String> {
    private AddScoreActivity screen = null;

    /*@Override protected void onPreExecute() {
        // Prétraitement de l'appel
    }

    @Override protected void onProgressUpdate(Y... progress) {
        // Gestion de l'avancement de la tâche
    }*/

    public AddScoreAsynchronious(AddScoreActivity s) {
        screen = s;
    }

    @Override
    protected String doInBackground(String... params) {
        String error_message = "";
        try {
            // WATCH OUT PARAM INVERTED
            URL url = new URL("http://www.lesqua.16mb.com/projet_android/ajouter_score.php?jeu=" + params[1] + "&score=" + params[0]);

            // instantier l'objet grâce à la méthode "openConnection()"
            HttpURLConnection connection;

            // effectuer une connexion au serveur web spécifié dans l'URL. Retourne un objet de type "URLConnexion".
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            // Fixer le nombre de milisecondes qu'on est prêt à attendre pour effectuer une connection.
            connection.setConnectTimeout(10000);

            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                InputStream in = new BufferedInputStream(connection.getInputStream());

                // Retourne la réponse du serveur

                connection.disconnect();
                Scanner scanner = new Scanner(in);

                if (scanner.hasNext()) {
                    String value = scanner.next();
                    switch (value) {
                        case "0":
                            error_message = "OK";
                            break;
                        case "100":
                            error_message = "problème $_GET['score']";
                            break;
                        case "110":
                            error_message = "problème $_GET['jeu']";
                            break;
                        case "500":
                            error_message = "problème de SESSION";
                            break;
                        case "1000":
                            error_message = "Problème de connexion à la DB";
                            break;
                        case "2002":
                            error_message = "Problème lors de l'insert";
                            break;
                        default:
                            error_message = "~> " + value;
                            break;
                    }
                }

            } else {
                error_message = "ResponseCode différent de 200";
            }
            // Déconnecte la connection
            connection.disconnect();
        } catch (
                MalformedURLException e
                )

        {
            e.printStackTrace();
            error_message = e.getMessage();
        } catch (
                Exception e
                )

        {
            e.getStackTrace();
            error_message = e.getMessage();
        }

        return error_message;
    }

    @Override
    protected void onPostExecute(String result) {
        // Callback
        // Renvoie les informations dans la fonction populate du mainactivity
        try {
            screen.populate_add_score(result);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}