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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import be.mousty.asychronious.AddScoreAsynchronious;
import be.mousty.asychronious.GameListAsynchronious;
import mousty.condorcet.be.gestion_score.R;

/**
 * Created by Admin on 26/12/2016.
 */

public class AddScoreActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);

        //Import the game list
        new GameListAsynchronious(AddScoreActivity.this).execute();

        // BUTTON
        Button btn_Add_new_Score = (Button) findViewById(R.id.btn_Add_new_Score);
        btn_Add_new_Score.setOnClickListener(add__new_score);

        Button btn_return = (Button) findViewById(R.id.btn_ret);
        btn_return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent return_intent = new Intent(AddScoreActivity.this, HomeActivity.class);
                setResult(RESULT_CANCELED, return_intent);
                finish();
            }
        });

    }

    // Add score within the DB
    private View.OnClickListener add__new_score = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et_new_score = (EditText) findViewById(R.id.et_new_score);
            AutoCompleteTextView actv_jeu = (AutoCompleteTextView) findViewById(R.id.game_title);
            TextView tv_error = (TextView) findViewById(R.id.tv_error);
            try {
                // Appel de la tâche async avec ses paramètres
                tv_error.setText("");
                new AddScoreAsynchronious(AddScoreActivity.this).execute(et_new_score.getText().toString(), actv_jeu.getText().toString());
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
    public void populate_add_score(String res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
        if (!res.equals("OK")) {
            tv_error.setText("[UNABLE TO ADD THE SCORE] " + res);
            tv_error.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            // Return to the previous intent
            Intent return_intent = new Intent(AddScoreActivity.this, HomeActivity.class);
            setResult(RESULT_OK, return_intent);
            finish();
        }
    }
}
