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

    public void populate_connect(String res) {
        TextView tv_error = (TextView) findViewById(R.id.tv_error);
        String color_code = "#9dc94f";

        if (!res.equals("OK")) {
            tv_error.setText("[UNABLE TO CONNECT]" + res);
            color_code = "#ff0000";
        }
        else{
            tv_error.setText("[CONNECT ESTABLISHED]");
            EditText et_login_name = (EditText) findViewById(R.id.et_login_username);
            EditText et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
            // Si la connexion a fonctionné, on affiche le home screen
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            // POUR GARDER LA VARIABLE DE SESSION ACTIVEE


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

    public void save_data(View view){
        EditText et_login_name = (EditText) findViewById(R.id.et_login_username);
        EditText et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);

        // Only the application can access it
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pseudo", et_login_name.getText().toString());
        editor.putString("mdp", et_login_pwd.getText().toString());

        editor.apply();
    }

    public void display_saved_Data(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String pseudo = sharedPreferences.getString("pseudo","");
        String mdp = sharedPreferences.getString("mdp","");
    }

    public void make_invisble_or_visible_sign_up_screen(Boolean visible) {
        // Enregistrement
        EditText et_new_username = (EditText) findViewById(R.id.et_new_username);
        EditText et_new_pwd1 = (EditText) findViewById(R.id.et_new_pwd1);
        EditText et_new_pwd2 = (EditText) findViewById(R.id.et_new_pwd2);
        Button btn_new_sign_up = (Button) findViewById(R.id.btn_new_sign_up);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);

        // Connexion
        /*EditText et_login_name = (EditText) findViewById(R.id.et_login_username);
        EditText et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        Button btn_connect = (Button) findViewById(R.id.btn_connect);
        Button btn_sign_up = (Button) findViewById(R.id.btn_sign_up);*/

        // Invisible
        if (!visible) {
            et_new_username.setVisibility(View.GONE);
            et_new_pwd1.setVisibility(View.GONE);
            et_new_pwd2.setVisibility(View.GONE);
            btn_new_sign_up.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);

            /*et_login_name.setVisibility(VISIBLE);
            et_login_pwd.setVisibility(VISIBLE);
            btn_connect.setVisibility(VISIBLE);
            btn_sign_up.setVisibility(VISIBLE);*/
        }
        // Visible
        else {
            et_new_username.setVisibility(VISIBLE);
            et_new_pwd1.setVisibility(VISIBLE);
            et_new_pwd2.setVisibility(VISIBLE);
            btn_new_sign_up.setVisibility(VISIBLE);
            btn_cancel.setVisibility(VISIBLE);

            /*et_login_name.setVisibility(View.GONE);
            et_login_pwd.setVisibility(View.GONE);
            btn_connect.setVisibility(View.GONE);
            btn_sign_up.setVisibility(View.GONE);*/
        }
    }

        /*private View.OnClickListener toggle_visibility = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                EditText et_new_username = (EditText) findViewById(R.id.et_new_username);
                // S'il est déjà visible on le cache, sinon on l'affiche.
                boolean toggle_visibility = et_new_username.getVisibility() == VISIBLE ? false : true;
                make_invisble_or_visible_sign_up_screen(toggle_visibility);
            } catch (Exception e) {
                TextView tv_error = (TextView) findViewById(R.id.tv_error);
                tv_error.setText(e.getMessage());
            }
        }
    };*/
}
