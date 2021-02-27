package com.example.garbout.UserPanal;

import com.google.firebase.firestore.DocumentId;

public class upload {
    private String UserName;
    private String Date;
    private String Url1;
    private String time;
    private String Url;
    private String complainAddress;
    private String name;
    private String phoneNumber;
    private String uid;
    private String userLat;
    private String userLan;
    private String status;
    @DocumentId
    private String docId;




    public upload() {
    }

    public upload(String userName, String date, String url, String time, String userPhoto, String Complainaddress, String Name, String PhoneNumber, String Uid,String Lat,String Lan,String docId,String Status) {
        UserName = userName;
        Date = date;
        Url1 = url;
        this.time = time;
        this.docId=docId;
        Url = userPhoto;
        complainAddress = Complainaddress;
        name = Name;
        phoneNumber = PhoneNumber;
        uid = Uid;
        userLan=Lan;
        userLat=Lan;
        status=Status;
    }

    public String getDocId() {
        return docId;
    }

    public String getUserName() {
        return UserName;
    }

    public String getDate() {
        return Date;
    }

    public String getUrl1() {
        return Url1;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return Url;
    }

    public String getComplainAddress() {
        return complainAddress;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public String getUserLat() {
        return userLat;
    }

    public String getUserLan() {
        return userLan;
    }

    public String getStatus() {
        return status;
    }
}



