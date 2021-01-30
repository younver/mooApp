package com.example.inekfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //Views
    Button mRegisterButton, mLoginButton;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init views
        mRegisterButton = findViewById(R.id.register_button);
        mLoginButton = findViewById(R.id.login_button);

        //handle register button click
        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Start Register Activity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));

            }
        });
        //handle login button click
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Login Activity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
