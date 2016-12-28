package be.mousty.intent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

/**
 * Created by Admin on 26/12/2016.
 */

public class DisplayTopTenActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

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
            EditText et_new_score = (EditText) findViewById(R.id.et_new_score);
            AutoCompleteTextView actv_jeu = (AutoCompleteTextView) findViewById(R.id.game_title);
            TextView tv_error = (TextView) findViewById(R.id.tv_error);
            try {
                // Appel de la tâche async avec ses paramètres
                tv_error.setText("");
                // Param le param est entré comme ceci [pokemon]
                String param = actv_jeu.getText().toString();
                param.replace("[", "");
                param.replace("]", "");
                new DisplayTopTenAccordingToGameAsynchronious(DisplayTopTenActivity.this).execute(param);
            } catch (Exception e) {
                tv_error.setText(e.getMessage());
            }
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

            //Create Array Adapter
            String debug ="";
            for(String e : res){
                debug += e + "";
            }
            tv_error.setText(debug);

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


            init(res);
        }
    }

    /*public void init(ArrayList<String> listeClients){
        try {
            TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
            for (String s : listeClients) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                // On split les éléments

                // Chaque mot est placé dans un textView
                //for(int i = 0; i < 2; i++) {
                TextView tv = new TextView(this);
                tv.setText(s);
                row.addView(tv);
                //}
                tl.addView(row);
            }
        }
        catch (Exception e) { e.getStackTrace(); }
    }*/

    public void init(ArrayList<String> top_ten){
        try {
            TableLayout tl = (TableLayout) findViewById(R.id.tableLayoutScore);
            tl.removeAllViews();
            int i = 0;

            for (String s : top_ten) {
                String[] parts = s.split("~");
                String pseudo   = parts[0]; // 004
                String score    = parts[1]; // 034556

                TableRow newRow = new TableRow(this);

                TextView column1 = new TextView(this);
                TextView column2 = new TextView(this);
                TextView column3 = new TextView(this);

                column1.setText(""+ (i +1));
                column2.setText(pseudo);
                column3.setText(score);


                column1.setPadding(35,0,0,0);
                column2.setPadding(230,0,0,0);
                column3.setPadding(185,0,0,0);

                newRow.addView(column1);
                newRow.addView(column2);
                newRow.addView(column3);

                tl.addView(newRow, new TableLayout.LayoutParams());

                i++;
            }
        }
        catch (Exception e) { e.getStackTrace(); }
    }
}
