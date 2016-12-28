package be.mousty.intent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import be.mousty.asychronious.LoginAsynchronious;
import be.mousty.asychronious.SignUpAsynchronious;
import mousty.condorcet.be.gestion_score.R;

import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {


    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Au chargement on cache une partie de l'écran
        make_invisble_or_visible_sign_up_screen(false);

        // Connection
        Button btn_connect = (Button) findViewById(R.id.btn_connect);
        Button btn_sign_up = (Button) findViewById(R.id.btn_sign_up);

        // Try to establish a connection
        btn_connect.setOnClickListener(check_connexion);

        // change screen visibility
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                make_invisble_or_visible_sign_up_screen(true);
            }
        });

        // Enregistrement
        Button btn_new_sign_up = (Button) findViewById(R.id.btn_new_sign_up);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);

        // Create new account
        btn_new_sign_up.setOnClickListener(sign_up);

        // change screen visibility
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                make_invisble_or_visible_sign_up_screen(false);
            }
        });
    }

    public void populate_connect(ArrayList<String> res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
        String color_code = "#9dc94f";

        if (!res.get(0).equals("OK")) {
            tv_error.setText("[UNABLE TO CONNECT]" + res);
            color_code = "#ff0000";
        }
        else{
            tv_error.setText("[CONNECT ESTABLISHED]");

            // Si la connexion a fonctionné, on affiche le home screen AVEC LE PSEUDO ET MDP POUR LA SESSION
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

            // POUR GARDER LID DE LUTILISATEUR
            intent.putExtra("id_utilisateur", res.get(1).toString());

            startActivity(intent);
        }
        tv_error.setTextColor(Color.parseColor(color_code));
    }

    // Inscription
    public void populate_sign_in(ArrayList<String> res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
        String color_code = "#9dc94f";


        if (!res.get(0).equals("OK")) {
            tv_error.setText("[UNABLE TO CREATE THE ACCOUNT]" + res.get(0));
            color_code = "#ff0000";
        }
        else{
            // On cache le formulaire
            make_invisble_or_visible_sign_up_screen(false);
            String id_user = res.get(1);
            tv_error.setText("[ACCOUNT CREATED (" + id_user + ")]");
        }
        tv_error.setTextColor(Color.parseColor(color_code));
    }

    private View.OnClickListener check_connexion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et_login_name = (EditText) findViewById(R.id.et_login_username);
            EditText et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
            TextView tv_error = (TextView) findViewById(R.id.tv_error);
            try {
                // Appel de la tâche async avec ses paramètres
                tv_error.setText("");
                new LoginAsynchronious(LoginActivity.this).execute("pseudo", et_login_name.getText().toString(), "mdp", et_login_pwd.getText().toString());
            } catch (Exception e) {
                tv_error.setText(e.getMessage());
            }
        }
    };

    private View.OnClickListener sign_up = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et_new_username = (EditText) findViewById(R.id.et_new_username);
            EditText et_new_pwd1 = (EditText) findViewById(R.id.et_new_pwd1);
            EditText et_new_pwd2 = (EditText) findViewById(R.id.et_new_pwd2);
            TextView tv_error = (TextView) findViewById(R.id.tv_error);
            try {
                if(et_new_pwd1.getText().toString().equals(et_new_pwd2.getText().toString())) {
                    // Appel de la tâche async avec ses paramètres
                    tv_error.setText("");
                    new SignUpAsynchronious(LoginActivity.this).execute("pseudo", et_new_username.getText().toString(), "mdp", et_new_pwd1.getText().toString());
                }
                else{
                    tv_error.setText("[PASSWORD NOT MATCHING]");
                }
            } catch (Exception e) {
                tv_error.setText(e.getMessage());
            }
        }
    };

    public void make_invisble_or_visible_sign_up_screen(Boolean visible) {
        // Enregistrement
        EditText et_new_username = (EditText) findViewById(R.id.et_new_username);
        EditText et_new_pwd1 = (EditText) findViewById(R.id.et_new_pwd1);
        EditText et_new_pwd2 = (EditText) findViewById(R.id.et_new_pwd2);
        Button btn_new_sign_up = (Button) findViewById(R.id.btn_new_sign_up);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);

        // Invisible
        if (!visible) {
            et_new_username.setVisibility(View.GONE);
            et_new_pwd1.setVisibility(View.GONE);
            et_new_pwd2.setVisibility(View.GONE);
            btn_new_sign_up.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);
        }
        // Visible
        else {
            et_new_username.setVisibility(VISIBLE);
            et_new_pwd1.setVisibility(VISIBLE);
            et_new_pwd2.setVisibility(VISIBLE);
            btn_new_sign_up.setVisibility(VISIBLE);
            btn_cancel.setVisibility(VISIBLE);

        }
    }
}
