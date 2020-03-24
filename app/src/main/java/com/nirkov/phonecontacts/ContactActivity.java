package com.nirkov.phonecontacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Intent intent = getIntent();
        ((TextView) findViewById(R.id.phone_to_fill)).setText(intent.getStringExtra("phone"));
        ((TextView) findViewById(R.id.name_to_fill)).setText(intent.getStringExtra("name"));
    }
}
