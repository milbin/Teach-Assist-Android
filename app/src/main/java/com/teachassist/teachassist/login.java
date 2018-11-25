package com.teachassist.teachassist;

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
    private static String username, password;

    private void submit_buttonClicked(){
        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();
        //MainActivity main = new MainActivity(username,password);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        usernameInput = (EditText) findViewById(R.id.editText1);
        passwordInput = (EditText) findViewById(R.id.editText2);

        submit_button = (Button) findViewById(R.id.login_button);
        //submit_button = new Button(this);
        submit_button.setOnClickListener(new submit_buttonClick());
        //findViewById(R.id.login_button).setOnClickListener(this);
    }

    class submit_buttonClick implements View.OnClickListener {

        @Override
        public void onClick(View v){
            submit_buttonClicked();
            //return;
        }
    }
    public static String getUser() {
        return username;
    }
    public static String getPass(){
        return password;
    }
}
