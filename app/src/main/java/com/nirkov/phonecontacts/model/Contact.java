package com.nirkov.phonecontacts.model;

public class Contact {
    private String mID;
    private String mName;
    private String mPhoneNumber;

    public Contact(String id, String name, String phone){
        mID          = id;
        mName        = name;
        mPhoneNumber = phone;
    }


    @Override
    public String toString() {
        return "Contact{" +
                "mID='" + mID + '\'' +
                ", mName='" + mName + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                '}';
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }
}
