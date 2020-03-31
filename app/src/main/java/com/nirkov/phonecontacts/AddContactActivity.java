package com.nirkov.phonecontacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class AddContactActivity extends AppCompatActivity {
    private Button mAddButton;
    private EditText mPhoneNumber, mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mPhoneNumber = (EditText) findViewById(R.id.contact_phone_box);
        mName        = (EditText) findViewById(R.id.contact_name_box);
        mAddButton   = (Button)   findViewById(R.id.add_contact_button);

        // In case the language of the phone isn't english
        if(!Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("english")){
            ((TextView) findViewById(R.id.contact_name)).setText("שם");
            ((TextView) findViewById(R.id.contact_phone)).setText("מספר טלפון");
            mAddButton.setText("הוסף");
        }

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // Add new contact to list - create it as ContentProviderOperation array of operation
            public void onClick(View view) {
                try {
                    ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build());

                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    mName.getText().toString()).build());

                    ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mPhoneNumber.getText().toString())
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());

                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", mName.getText().toString());
                resultIntent.putExtra("phone", mPhoneNumber.getText().toString());
                setResult(1, resultIntent);
                finish();
            }
        });
    }
}
