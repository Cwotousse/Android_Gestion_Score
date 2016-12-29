package be.mousty.asychronious;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import be.mousty.intent.DisplayGameListActivity;
import be.mousty.intent.DisplayUserListActivity;
import be.mousty.intent.LoginActivity;

//http://stackoverflow.com/questions/6053602/what-arguments-are-passed-into-asynctaskarg1-arg2-arg3
// On remplace les XYZ par le type objet nécéssaire
// X -> Recu en paramètre  du doInBackground
// Y -> non utilisé, pour le onProgressUpdate
// Z -> Résultat, placé dans onPostExecute
public class DisplayUserListAsynchronious extends AsyncTask<Void, Void, ArrayList<String>> {
    private DisplayUserListActivity screen = null;

    ProgressDialog progress;
    public DisplayUserListAsynchronious(DisplayUserListActivity s) {
        screen = s;
        progress = new ProgressDialog(screen);
    }

    @Override protected void onPreExecute() {
        // Prétraitement de l'appel
        progress.setTitle("WAIT PLEASE");
        progress.setMessage("WE ARE SEARCHING OUR DATA...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    /*@Override protected void onProgressUpdate(Y... progress) {
        // Gestion de l'avancement de la tâche
    }*/


    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> pseudo_list = new ArrayList<String>();
        try {
            URL url = new URL("http://lesqua.16mb.com/projet_android/lister_pseudos.php?");

            // instancier l'objet grâce à la méthode "openConnection()"
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
                            pseudo_list.add("OK");
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
                                        String lbl = jsonReader.nextName();
                                        String el = jsonReader.nextString();
                                        pseudo_list.add(el);
                                    }
                                    // Fermeture des éléments dans le tableau
                                    jsonReader.endObject();
                                }
                                // Fermeture du tableau
                                jsonReader.endArray();
                            }
                            break;
                        case 300:
                            pseudo_list.add("Aucun pseudo trouvé) ");
                            break;
                        case 1000:
                            pseudo_list.add("Problème de connexion à la DB");
                            break;
                        default:
                            pseudo_list.add(label + " / " + code);
                            break;
                    }
                }
                // Fin de la récuperation
                jsonReader.endObject();
            }
            connection.disconnect();
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
            pseudo_list.add(e.getMessage());
        }
        catch (Exception e)
        {
            e.getStackTrace();
            pseudo_list.add(e.getMessage());
        }
        return pseudo_list;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        // Callback
        // Renvoie les informations dans la fonction populate du mainactivity
        try {
            if(progress.isShowing()) { progress.dismiss(); }
            screen.populate_user_list(result);
        }
        catch (Exception e) { e.getStackTrace(); }
    }
}