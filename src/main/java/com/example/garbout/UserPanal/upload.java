package com.example.garbout.UserPanal;

public class  upload {
    private String UserName;
    private  String Date;
    private String Url1;
    private String time;
    private  String Url;
    private String complainAddress;
    private  String name;
    private  String phoneNumber;


    public upload() {
    }

    public upload(String userName, String date, String url, String time,String userPhoto,String Complainaddress,String Name,String PhoneNumber) {
        UserName = userName;
        Date = date;
        Url1 = url;
        this.time = time;
        Url =userPhoto;
        complainAddress=Complainaddress;
        name=Name;
        phoneNumber=PhoneNumber;

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
}



