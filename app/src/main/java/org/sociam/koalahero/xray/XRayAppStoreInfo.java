package org.sociam.koalahero.xray;

import java.util.Date;

public class XRayAppStoreInfo {
    /**
     *  EXAMPLE API JSON RESPONSE FOR STORE INFO
     *
     {"title":"Messenger",
     "summary":"Messenger — a faster way to message.",
     "description":"Instantly reach the people in your life—for free. Messenger is just like texting, but you don't have to pay for every message (it works with your data plan).Not just for Facebook friends: Message people in your phone book and just enter a phone number to add a new contact.Group chats: Create groups for the people you message most. Name them, set group photos and keep them all in one place.Photos and videos: Shoot videos and snap selfies or other photos right from the app and send them with one tap.Chat heads: Keep the conversation going while you use other apps. Free calls: Talk as long as you want, even with people in other countries. (Calls are free over Wi-Fi. Otherwise, standard data charges apply.)Even more ways to message: Bring your conversations to life with stickers. Preview your gallery photos and videos without leaving the conversation--then choose the perfect ones to send.Record voice messages when you have more to say.Extra features:Know when people have seen your messages.Forward messages or photos to people who weren't in the conversation.Search for people and groups to quickly get back to them.Turn on location to let people know when you're nearby.See who's available on Messenger and who's active on Facebook. Create shortcuts to get to any conversation right from your home screen.Turn off notifications when you're working, sleeping or just need a break.Stay logged in so you never miss a message.",
     "storeURL":"https://play.google.com/store/apps/details?id=com.facebook.orca&hl=en&gl=us",
     "price":"0",
     "free":true,
     "rating":"4.0",
     "numReviews":46504100,
     "genre":"COMMUNICATION",
     "familyGenre":"",
     "installs":{"min":1000000000,"max":5000000000},
     "developer":0,
     "updated":"2017-07-26T00:00:00Z",
     "androidVer":"VARY",
     "contentRating":"Everyone",
     "screenshots":null,
     "video":"",
     "recentChanges":["Now you can see your call history and missed calls—all in one place."],
     "crawlDate":"0001-01-01T00:00:00Z",
     "permissions":null}
     */

    public String title;
    public String summary;
    public String storeURL;
    public boolean isFree;
    public double rating;
    public String genre;
    public long numberOfReviews;
    public long maxInstalls;
    public long minInstalls;
    public Date updated;

    XRayAppStoreInfo() {
        title = "";
        summary = "";
        storeURL = "";
        isFree = false;
        rating = 0.0f;
        numberOfReviews = 0;
        maxInstalls = 0;
        minInstalls = 0;
        updated = new Date();
    }

    XRayAppStoreInfo(String title) {
        this.title = title;
        this.summary = "";
        this.storeURL = "";
        this.isFree = false;
        this.rating = 0.0f;
        this.numberOfReviews = 0;
        this.maxInstalls = 0;
        this.minInstalls = 0;
        this.updated = new Date();
        this.genre = "";

    }
    XRayAppStoreInfo(String title, String summary) {
        this.title = title;
        this.summary = summary;
        this.storeURL = "";
        this.isFree = false;
        this.rating = 0.0f;
        this.numberOfReviews = 0;
        this.maxInstalls = 0;
        this.minInstalls = 0;
        this.updated = new Date();
        this.genre = "";
    }
}
