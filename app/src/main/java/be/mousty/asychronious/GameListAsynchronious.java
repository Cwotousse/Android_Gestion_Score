package be.mousty.asychronious;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import be.mousty.intent.AddScoreActivity;

//http://stackoverflow.com/questions/6053602/what-arguments-are-passed-into-asynctaskarg1-arg2-arg3
// On remplace les XYZ par le type objet nécéssaire
// X -> Recu en paramètre  du doInBackground
// Y -> non utilisé, pour le onProgressUpdate
// Z -> Résultat, placé dans onPostExecute
public class GameListAsynchronious extends AsyncTask<Void, Void, ArrayList<String>> {
    private AddScoreActivity screen = null;

    /*@Override protected void onPreExecute() {
        // Prétraitement de l'appel
    }

    @Override protected void onProgressUpdate(Y... progress) {
        // Gestion de l'avancement de la tâche
    }*/

    public GameListAsynchronious(AddScoreActivity s) {
        screen = s;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> game_list = new ArrayList<String>();
        try {
            URL url = new URL("http://lesqua.16mb.com/projet_android/lister_jeux.php");

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
                        case 0:
                            game_list.add("OK");
                            if (jsonReader.hasNext()) {
                                label = jsonReader.nextName();
                                // Si le code est bon on peut retourner les jeux
                                jsonReader.beginArray();
                                // Les tableaux
                                while (jsonReader.hasNext()) {
                                    jsonReader.beginObject();
                                    // Pas de nextname pour le nom
                                    // Les éléments dans le tableau
                                    while (jsonReader.hasNext()) {
                                        //Tant qu'il y a des éléments on ajoute à la liste
                                        jsonReader.nextName();
                                        String el = jsonReader.nextString();
                                        game_list.add(el);
                                    }
                                    // Fermeture des éléments dans le tableau
                                    jsonReader.endObject();
                                }
                                // Fermeture du tableau
                                jsonReader.endArray();
                            }
                            break;
                        case 300:
                            game_list.add("Aucun jeu trouvé");
                            break;
                        case 500:
                            game_list.add("Session non valide");
                            break;
                        case 1000:
                            game_list.add("Problème de connexion à la DB");
                            break;
                        default:
                            game_list.add(label + " / " + code);
                            break;
                    }
                }
                // Fin de la récuperation
                jsonReader.endObject();
            }
            connection.disconnect();
        } catch (
                MalformedURLException e
                )

        {
            e.printStackTrace();
            game_list.add(e.getMessage());
        } catch (
                Exception e
                )

        {
            e.getStackTrace();
            game_list.add(e.getMessage());
        }

        return game_list;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        // Callback
        // Renvoie les informations dans la fonction populate du mainactivity
        try {
            screen.populate_game_list(result);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}