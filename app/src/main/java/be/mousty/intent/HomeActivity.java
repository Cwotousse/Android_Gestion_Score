package be.mousty.intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mousty.condorcet.be.gestion_score.R;

/**
 * Created by Admin on 26/12/2016.
 */

public class HomeActivity extends AppCompatActivity {
    public final static int NUM_REQUETE = 1;
    String id_utilisateur;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setID(savedInstanceState);

        Button btn_add_new_score        = (Button)findViewById(R.id.btn_add_new_score);
        Button btn_display_top_ten      = (Button)findViewById(R.id.btn_display_top_ten);
        Button btn_display_game_list    = (Button)findViewById(R.id.btn_display_game_list);

        Button btn_display_user_list    = (Button)findViewById(R.id.btn_display_user_list);

        btn_add_new_score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // Start a new intent to add a brand new score
                Intent intent = new Intent(HomeActivity.this, AddScoreActivity.class);
                // POUR GARDER LA VARIABLE DE SESSION ACTIVEE
                intent.putExtra("id_utilisateur", id_utilisateur);
                startActivityForResult(intent, NUM_REQUETE);
            }
        });

        btn_display_top_ten.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // Start a new intent to add a brand new score
                Intent intent = new Intent(HomeActivity.this, DisplayTopTenActivity.class);
                startActivityForResult(intent, NUM_REQUETE);
            }
        });
    }

    public void setID(Bundle savedInstanceState){
        // Reconnection
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id_utilisateur= null;

            } else {
                id_utilisateur= extras.getString("id_utilisateur");

            }
        } else {
            id_utilisateur= (String) savedInstanceState.getSerializable("id_utilisateur");
        }

        TextView tv_error = (TextView)findViewById(R.id.tv_error);
        tv_error.setText(id_utilisateur);
    }
}
