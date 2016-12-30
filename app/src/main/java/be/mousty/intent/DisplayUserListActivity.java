package be.mousty.intent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import be.mousty.asychronious.DisplayGameAndBestUserAsynchronious;
import be.mousty.asychronious.DisplayUserListAsynchronious;
import mousty.condorcet.be.gestion_score.R;

/**
 * Created by Admin on 26/12/2016.
 */

public class DisplayUserListActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    public final static int NUM_REQUETE = 1;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        //Import the game list
        new DisplayUserListAsynchronious(DisplayUserListActivity.this).execute();

        // BUTTON
        Button btn_return = (Button) findViewById(R.id.btn_ret);
        btn_return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent return_intent = new Intent(DisplayUserListActivity.this, HomeActivity.class);
                setResult(RESULT_CANCELED, return_intent);
                finish();
            }
        });

    }

    // Add score and return to the previous intent
    public void populate_user_list(ArrayList<String> res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
        if (!res.get(0).equals("OK")) {
            tv_error.setText("[UNABLE TO DISPLAY THE GAME LIST] " + res);
            tv_error.setTextColor(Color.parseColor("#ff0000"));
        }
        else{
            // Display top
            // On retire le "ok"
            res.remove(0);
            initTable(res);
        }
    }

    public void initTable(ArrayList<String> user_list){
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
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
                        "background-color:#00b2ff;\n" +
                        "}\n" +
                        "</style>\n" +
                        "\n" +
                        "<!-- HTML Code -->\n" +
                        "<table class=\"GeneratedTable\">\n" +
                        "<thead>\n" +
                        "<tr>\n" +
                        "<th>Username</th>\n" +
                        "</tr>\n" +
                        "</thead>\n" +
                        "<tbody>\n";
        try {
            for (String s : user_list) {
                 htmlTable +=
                    "<tr>\n" +
                    "<td>"+ s +"</td>\n" +
                    "</tr>\n";
            }

            // Finish the table
            htmlTable+=
                    "</tbody>\n" +
                    "</table>\n";

            WebView vue_web = (WebView) findViewById(R.id.wb_usertable);
            vue_web.loadData(htmlTable, "text/html" ,"UTF-8");
        }
        catch (Exception e) { e.getStackTrace(); tv_error.setText(e.getMessage());}
    }
}
