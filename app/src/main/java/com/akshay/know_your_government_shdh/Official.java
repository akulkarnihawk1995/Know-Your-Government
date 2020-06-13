package com.akshay.know_your_government_shdh;


import java.io.Serializable;
import java.util.ArrayList;

public class Official implements Serializable
{
    private String name, title;
    private String address;
    private String party;
    private String phones;
    private String urls;
    private String emails;
    private String photoURL;
    private ArrayList<Channel> channels;



    Official()
    {
        name = "";
        title = "";
        address = "";
        party = "";
        phones = "";
        urls = "";
        emails = "";
        photoURL = "";
        channels = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }

    String getTitle()
    {
        return title;
    }

    public String getAddress() {
        return address;
    }

    String getParty() {
        return party;
    }

    public String getPhones()
    {
        return phones;
    }

    public String getUrls()
    {
        return urls;
    }

    public String getEmails()
    {
        return emails;
    }

    public String getPhotoURL()
    {
        return photoURL;
    }

    public ArrayList<Channel> getChannels()
    {
        return channels;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    void setTitle(String title)
    {
        this.title = title;
    }

    void setAddress(String address) {
        this.address = address;
    }

    void setParty(String party)
    {
        this.party = party;
    }

    void setPhones(String phones)
    {
        this.phones = phones;
    }

    void setUrls(String urls)
    {
        this.urls = urls;
    }

    void setEmails(String emails)
    {
        this.emails = emails;
    }

    void setPhotoURL(String photoURL)
    {
        this.photoURL = photoURL;
    }

    void setChannels(ArrayList<Channel> channels)
    {
        this.channels = channels;
    }
}
