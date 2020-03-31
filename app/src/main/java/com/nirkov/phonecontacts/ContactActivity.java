package com.nirkov.phonecontacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Intent intent = getIntent();

        // In case the language of the phone isn't english
        if(!Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("english")){
            ((TextView) findViewById(R.id.name)).setText("שם");
            ((TextView) findViewById(R.id.phone)).setText("מספר טלפון");
        }
        ((TextView) findViewById(R.id.phone_to_fill)).setText(intent.getStringExtra("phone"));
        ((TextView) findViewById(R.id.name_to_fill)).setText(intent.getStringExtra("name"));
    }
}
