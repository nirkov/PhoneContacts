package com.nirkov.phonecontacts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nirkov.phonecontacts.model.Contact;
import com.nirkov.phonecontacts.model.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSIONS_ID_REQUEST_READ_CONTACTS  = 0;
    private final int ADD_NEW_CONTACT = 1;

    private ListView mContactsListView;
    private FloatingActionButton mAddButton;

    private ArrayList<Contact> mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize fields
        mContacts         = new ArrayList<>();
        mContactsListView = (ListView) findViewById(R.id.list);
        mContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Contact contact = (Contact)adapterView.getItemAtPosition(position);
                Intent intent;
                intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra("name", contact.getmName());
                intent.putExtra("phone", contact.getmPhoneNumber());
                startActivity(intent);
            }
        });

        // Check for permission for reading the contacts and insert new contact
        if (checkPermissions()){
            getAllContacts();
            ContactAdapter contactsAdapter = new ContactAdapter(this, mContacts);
            mContactsListView.setAdapter(contactsAdapter);
        }else {
            getPermission();
        }

        mAddButton = findViewById(R.id.add_fab);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivityForResult(intent, ADD_NEW_CONTACT);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case ADD_NEW_CONTACT : {
                // In case we succeeded to add new contact to phone contact list, we need to create
                // new Contact object and add it to the mContact list
                if(resultCode == requestCode){
                    final String name = data.getStringExtra("name").toString();
                    final String phone = data.getStringExtra("phone").toString();
                    final int index = binarySearchOnSortedArray(mContacts, name);
                    mContacts.add(index, new Contact(String.valueOf(index), name, phone));
                    mContactsListView.invalidate();
                }
            }

        }
    }

    /**
     * Return the index where we need to insert the new item (to <contacts>)
     * @param contacts
     * @param newName
     * @return
     */
    private int binarySearchOnSortedArray(ArrayList<Contact> contacts, String newName){
        if(contacts == null || contacts.isEmpty()) return 0;
        int start  = 0;
        int end    = contacts.size() - 1;
        int middle = 0;
        int compareResult;
        while(start <= end){
            middle = (start + end) / 2;
            compareResult = contacts.get(middle).getmName().compareTo(newName);
            if(compareResult == 0){
                break;
            }else{
                if(compareResult < 0){
                    start = middle + 1;
                }else{
                    end = middle - 1;
                }
            }
        }
        if(contacts.get(middle).getmName().compareTo(newName) < 0 && middle <= contacts.size()) {
            middle ++;
        }
        return middle;
    }

    /**
     * Ask for permission to read data of contact and insert new contact from the device
     */
    private void getPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
        } else {
            // The permission we need for edit contacts list on phone
            List<String> permissionsNeeded = new ArrayList<>();
            permissionsNeeded.add(Manifest.permission.READ_CONTACTS);
            permissionsNeeded.add(Manifest.permission.WRITE_CONTACTS);

            // Send permissions request
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    PERMISSIONS_ID_REQUEST_READ_CONTACTS);
        }
    }

    /**
     * Chacke the permission type that return to this function. in this case we are waiting to
     * only one permission - read and insert new contact to the phone.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int      requestCode,
                                           String[] permissions,
                                           int[]    grantResults) {
        switch (requestCode) {
            case PERMISSIONS_ID_REQUEST_READ_CONTACTS: {
                Context context = getApplicationContext();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission Received", Toast.LENGTH_SHORT).show();

                    // Read all phone contact and insert them to mContacts list
                    getAllContacts();

                    // Create new contact adapter that will make adaptation between the list view
                    // and the item we want to insert to it - item_contact.xml
                    ContactAdapter contactsAdapter = new ContactAdapter(this, mContacts);
                    mContactsListView.setAdapter(contactsAdapter);
                }else{
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
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
            String contactName = "";
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
                    }
                    phoneNumberCursor.close();
                }
                if(contactName != null){
                    mContacts.add(new Contact(contacID, contactName, phoneNumber));
                }
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

    /**
     * Check for permission to read contacts data and insert new contacts to the device
     * @return <boolean>
     */
    private boolean checkPermissions(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED);
    }
}
