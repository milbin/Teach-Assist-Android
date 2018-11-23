package com.teachassist.teachassist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {
    EditText usernameInput;
    EditText passwordInput;
    Button submit_button;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        usernameInput = (EditText) findViewById(R.id.editText1);
        passwordInput = (EditText) findViewById(R.id.editText2);

        submit_button = (Button) findViewById(R.id.login_button);
        //submit_button.setOnClickListener(new View.OnClickListener()
        findViewById(R.id.login_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();
        Intent intent = new Intent(this, OtherActivity.class);
        intent.putExtra("key", theString);
        startActivity(intent);
    }
}
