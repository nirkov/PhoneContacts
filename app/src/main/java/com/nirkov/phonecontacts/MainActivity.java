package com.nirkov.phonecontacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nirkov.phonecontacts.model.Contact;
import com.nirkov.phonecontacts.model.ContactAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSIONS_ID_REQUEST_READ_CONTACTS  = 0;
    private final int PERMISSIONS_ID_REQUEST_WRITE_CONTACTS = 1;

    private ListView mContactsListView;
    private FloatingActionButton mAddButton;

    private ArrayList<Contact> mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize fields
        mContacts = new ArrayList<>();

        // Check for permission for reading the contacts
        if (checkPermissions()){
            getAllContacts();
        }else {
            getPermission();
        }

        mContactsListView = (ListView) findViewById(R.id.list);
        mContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Contact contact = (Contact)adapterView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra("name", contact.getmName());
                intent.putExtra("phone", contact.getmPhoneNumber());
                startActivity(intent);
            }
        });

        mAddButton = findViewById(R.id.add_fab);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
    }


    private void getPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_ID_REQUEST_READ_CONTACTS);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    PERMISSIONS_ID_REQUEST_WRITE_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int      requestCode,
                                           String[] permissions,
                                           int[]    grantResults) {
        switch (requestCode) {
            case PERMISSIONS_ID_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts();
                    ContactAdapter contactsAdapter = new ContactAdapter(this, mContacts);
                    mContactsListView.setAdapter(contactsAdapter);
                }
                return;
            }
            case PERMISSIONS_ID_REQUEST_WRITE_CONTACTS: {
                return;
            }
        }
    }

    private void getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();

        // The Content Resolver accepts requests from clients, and resolves these requests
        // by directing them to the content provider with a distinct authority.
        // The Content Resolver includes the following methods : create, read, update, delete.
        ContentResolver cr = getContentResolver();

        // We can't query the Content Provider directly, so we use the Content Resolver
        // for sending a query about the underlying data.
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if(cursor != null && cursor.getCount() > 0){
            String phoneNumber = "";
            String contacID = "";
            String contactName;
            Cursor phoneNumberCursor;
            while (cursor != null && cursor.moveToNext()) {
                // Get contact name
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // Get all numbers relate to this cursor instance, if exists
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    // Get the id of this item for the query.
                    contacID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    phoneNumberCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contacID}, null);

                    // Find all the phone number of this contact
                    while (phoneNumberCursor.moveToNext()) {
                        phoneNumber = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        break;
                    }
                    phoneNumberCursor.close();
                }
                mContacts.add(new Contact(contacID, contactName, phoneNumber));
            }
            if (cursor != null) cursor.close();
            Collections.sort(mContacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact thisContact, Contact other) {
                    return thisContact.getmName().compareTo(other.getmName());
                }
            });
        }
    }

    private boolean checkPermissions(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED);
    }


}
