package com.nirkov.phonecontacts.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nirkov.phonecontacts.R;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(@NonNull Context context, ArrayList<Contact> contacts) {
        super(context,0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
        }

        // Get the data item for this position
        Contact contact = getItem(position);

        // Lookup view for data population and fill the text views
        ((TextView) convertView.findViewById(R.id.name)).setText(contact.getmName());
        ((TextView) convertView.findViewById(R.id.phone)).setText(contact.getmPhoneNumber());

        return convertView;
    }


}
