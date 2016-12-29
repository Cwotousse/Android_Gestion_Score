package be.mousty.intent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import be.mousty.asychronious.DisplayGameAndBestUserAsynchronious;
import be.mousty.asychronious.DisplayTopTenAccordingToGameAsynchronious;
import be.mousty.asychronious.GameListTopSearchAsynchronious;
import mousty.condorcet.be.gestion_score.R;

/**
 * Created by Admin on 26/12/2016.
 */

public class DisplayGameListActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayList<String> game_list = new ArrayList<>();
    int pageActuelle = 1;
    int maxPage = 1;

    public final static int NUM_REQUETE = 1;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        final TextView tv_error = (TextView) findViewById(R.id.tv_error);

        //Import the game list
        new DisplayGameAndBestUserAsynchronious(DisplayGameListActivity.this).execute();

        // BUTTON
        Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pageActuelle < maxPage){
                    pageActuelle++;
                    initTable(game_list);
                    tv_error.setText("");
                }
                else{ tv_error.setText("No further pages exists yet.");}

            }
        });

        Button btn_previous = (Button) findViewById(R.id.btn_previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pageActuelle > 1 ){
                    pageActuelle--;
                    initTable(game_list);
                    tv_error.setText("");
                }
                else{ tv_error.setText("Minimum is reached.");}

            }
        });

        Button btn_return = (Button) findViewById(R.id.btn_ret);
        btn_return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent return_intent = new Intent(DisplayGameListActivity.this, HomeActivity.class);
                setResult(RESULT_CANCELED, return_intent);
                finish();
            }
        });

    }

    // Add score and return to the previous intent
    public void populate_game_list(ArrayList<String> res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
        if (!res.get(0).equals("OK")) {
            tv_error.setText("[UNABLE TO DISPLAY THE GAME LIST] " + res);
            tv_error.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            // Display top
            // On retire le "ok"
            res.remove(0);
            game_list = res;
            initTable(game_list);
        }
    }

    public void initTable(ArrayList<String> game_list){
        try {
            TableLayout tl = (TableLayout) findViewById(R.id.tableLayoutGame);
            tl.removeAllViews();
            int i = 0;
            int nbrPages = 0;
            for (String s : game_list) {
                // IF THE PAGE IS THE CURRENT PAGE WE DISPLAY INFORMATION
                // IF NOT WE DISPLAY NOTHING
                if(nbrPages == (pageActuelle-1)) {
                    String[] parts = s.split("~");
                    String pseudo = parts[0];
                    final String game = parts[1];

                    TableRow newRow = new TableRow(this);

                    TextView column1 = new TextView(this);
                    TextView column2 = new TextView(this);
                    Button button1 = new Button(this);

                    column1.setText(game);
                    column2.setText(pseudo);
                    button1.setText("Top 10");


                    column1.setPadding(35, 0, 0, 0);
                    column2.setPadding(100, 0, 125, 0);


                    // Add intent click
                    button1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Start a new intent to add a brand new score
                            Intent intent = new Intent(DisplayGameListActivity.this, DisplayTopTenActivity.class);
                            intent.putExtra("game", game);
                            startActivityForResult(intent, NUM_REQUETE);
                        }
                    });

                    newRow.addView(column1);
                    newRow.addView(column2);
                    newRow.addView(button1);

                    tl.addView(newRow, new TableLayout.LayoutParams());
                }
                // ONLY FIVE ELEMENT PER PAGES
                if((i+1)%5 == 0){ nbrPages++; }
                i++;
            }

            // Display number of pages + current page
            TextView tv_page = (TextView) findViewById(R.id.tv_page);
            TextView tv_totPages = (TextView) findViewById(R.id.tv_totPages);
            maxPage = (nbrPages+1);
            tv_page.setText("Page : " + pageActuelle);
            tv_totPages.setText("/" + maxPage);
        }
        catch (Exception e) { e.getStackTrace(); }
    }
}
