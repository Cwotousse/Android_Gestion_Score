package be.mousty.intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mousty.condorcet.be.gestion_score.R;

/**
 * Created by Admin on 26/12/2016.
 */

public class HomeActivity extends AppCompatActivity {
    public final static int NUM_REQUETE = 1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btn_add_new_score        = (Button)findViewById(R.id.btn_add_new_score);
        Button btn_display_game_list    = (Button)findViewById(R.id.btn_display_game_list);
        Button btn_display_top_ten      = (Button)findViewById(R.id.btn_display_top_ten);
        Button btn_display_user_list    = (Button)findViewById(R.id.btn_display_user_list);

        btn_add_new_score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // Start a new intent to add a brand new score
                Intent intent = new Intent(HomeActivity.this, AddScoreActivity.class);
                startActivityForResult(intent, NUM_REQUETE);
            }
        });
    }
}
