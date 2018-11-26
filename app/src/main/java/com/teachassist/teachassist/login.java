package com.teachassist.teachassist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {
    EditText usernameInput;
    EditText passwordInput;
    Button submit_button;
    String username, password;

    private void submit_buttonClicked(){
        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();
        //MainActivity main = new MainActivity(username,password);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        usernameInput = (EditText) findViewById(R.id.editText1);
        passwordInput = (EditText) findViewById(R.id.editText2);

        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    usernameInput.setHint("");
                else
                    usernameInput.setHint("Username");
            }
        });
        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    passwordInput.setHint("");
                else
                    passwordInput.setHint("Password");
            }
        });

        submit_button = (Button) findViewById(R.id.login_button);
        //submit_button = new Button(this);
        submit_button.setOnClickListener(new submit_buttonClick());
        //findViewById(R.id.login_button).setOnClickListener(this);
    }

    class submit_buttonClick implements View.OnClickListener {

        @Override
        public void onClick(View v){
            submit_buttonClicked();
            //ProgressDialog dialog = ProgressDialog.show(login.this, "",
            //        "Signing in...", true);
            Intent myIntent = new Intent(login.this, MainActivity.class);
            myIntent.putExtra("username", username);
            myIntent.putExtra("password", password);
            System.out.println(username);
            System.out.println(password);
            //login.this.startActivityForResult(myIntent, 10101010); //random int i set
            startActivity(myIntent);

            //return;
        }
    }
}
