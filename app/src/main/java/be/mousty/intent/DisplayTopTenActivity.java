package be.mousty.intent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import be.mousty.asychronious.DisplayTopTenAccordingToGameAsynchronious;
import be.mousty.asychronious.GameListTopSearchAsynchronious;
import mousty.condorcet.be.gestion_score.R;

import static android.view.View.VISIBLE;
import static android.view.View.generateViewId;

/**
 * Created by Admin on 26/12/2016.
 */

public class DisplayTopTenActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    String game = null;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

        setGame(savedInstanceState);

        // If the game text isn't null, it's because the data come from DisplayGameListActivitiy,
        // We have to display immediatly the top ten
        if(game != null){
            TextView tv_error = (TextView) findViewById(R.id.tv_error);
            try {
                // Call the async task with parameters
                tv_error.setText("");
                new DisplayTopTenAccordingToGameAsynchronious(DisplayTopTenActivity.this).execute(game);
            } catch (Exception e) { tv_error.setText(e.getMessage()); }
        }
        // Else, we need to hide the WV
        else{
            WebView vue_web = (WebView) findViewById(R.id.wv_top_ten);
            vue_web.setVisibility(View.GONE);
        }

        //Import the game list
        new GameListTopSearchAsynchronious(DisplayTopTenActivity.this).execute();

        // BUTTON
        Button btn_display_top = (Button) findViewById(R.id.btn_display_top);
        btn_display_top.setOnClickListener(display_top);

        Button btn_return = (Button) findViewById(R.id.btn_ret);
        btn_return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent return_intent = new Intent(DisplayTopTenActivity.this, HomeActivity.class);
                setResult(RESULT_CANCELED, return_intent);
                finish();
            }
        });

    }

    // Add score within the DB
    private View.OnClickListener display_top = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AutoCompleteTextView actv_jeu = (AutoCompleteTextView) findViewById(R.id.game_title);
            TextView tv_error = (TextView) findViewById(R.id.tv_error);
            try {
                // Appel de la tâche async avec ses paramètres
                tv_error.setText("");
                // Sometimes the param is inserted like this [pokemon]
                String param = actv_jeu.getText().toString();
                // Me must extrat [ and ]
                param.replace("[", "");
                param.replace("]", "");
                new DisplayTopTenAccordingToGameAsynchronious(DisplayTopTenActivity.this).execute(param);
            } catch (Exception e) { tv_error.setText(e.getMessage()); }
        }
    };

    // Display game list
    public void populate_game_list(ArrayList<String> res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);

        if (!res.get(0).equals("OK")) {
            tv_error.setText("[UNABLE TO LOAD THE LIST]" + res.get(0));
            tv_error.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            // removes the first item because it contains just "ok"
            res.remove(0);

            // NO DOUBLES
            // add elements to al, including duplicates
            Set<String> hs = new HashSet<>();
            hs.addAll(res);
            res.clear();
            res.addAll(hs);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, res);

            //Find TextView control
            AutoCompleteTextView acTextView = (AutoCompleteTextView) findViewById(R.id.game_title);
            //Set the number of characters the user must type before the drop down list is shown
            acTextView.setThreshold(1);
            //Set the adapter
            acTextView.setAdapter(adapter);
        }
    }

    // Add score and return to the previous intent
    public void populate_display_top(ArrayList<String> res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
        if (!res.get(0).equals("OK")) {
            tv_error.setText("[UNABLE TO DISPLAY THE TOP TEN] " + res);
            tv_error.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            // Display top
            // On retire le "ok"
            res.remove(0);
            ini_table(res);
        }
    }

    public void ini_table(ArrayList<String> top_ten){
        try {
            // Build the table
            String htmlTable =
                    "<!-- CSS Code -->\n" +
                            "<style type=\"text/css\" scoped>\n" +
                            "table.GeneratedTable {\n" +
                            "width:100%;\n" +
                            "background-color:#FFFFFF;\n" +
                            "border-collapse:collapse;border-width:0px;\n" +
                            "border-color:#FFFFFF;\n" +
                            "border-style:solid;\n" +
                            "color:#000000;\n" +
                            "}\n" +
                            "\n" +
                            "table.GeneratedTable td, table.GeneratedTable th {\n" +
                            "border-width:0px;\n" +
                            "border-color:#FFFFFF;\n" +
                            "border-style:solid;\n" +
                            "padding:3px;\n" +
                            "}\n" +
                            "\n" +
                            "table.GeneratedTable thead {\n" +
                            "background-color:#99FF00;\n" +
                            "}\n" +
                            "</style>\n" +
                            "\n" +
                            "<!-- HTML Code -->\n" +
                            "<table class=\"GeneratedTable\">\n" +
                            "<thead>\n" +
                            "<tr>\n" +
                                "<th>NUM</th>\n" +
                                "<th>USERNAME</th>"+
                                "<th>SCORE</th>\n" +
                            "</thead>\n" +
                            "<tbody>\n";
            int i = 0;

            for (String s : top_ten) {
                // ONLY 10 ELEMENTS
                if (i < 10) {
                    String[] parts  = s.split("~");
                    String pseudo   = parts[0]; // 004
                    String score    = parts[1]; // 034556
                    htmlTable+=
                        "<tr>\n" +
                            "<td>"+ (i+1) +"</td>\n" +
                            "<td>"+ pseudo +"</td>\n" +
                            "<td>"+ score +"</td>\n" +
                         "</tr>\n";
                }
                i++;
            }
            htmlTable+=
                    "</tbody>\n" +
                    "</table>\n";
            WebView vue_web = (WebView) findViewById(R.id.wv_top_ten);
            vue_web.loadData(htmlTable, "text/html" ,"UTF-8");
            vue_web.setVisibility(VISIBLE);
        }
        catch (Exception e) { e.getStackTrace(); }
    }

    public void setGame(Bundle savedInstanceState){
        // Reconnection
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) { game= null; }
            else { game= extras.getString("game"); }
        }
        else { game= (String) savedInstanceState.getSerializable("game"); }
    }
}
