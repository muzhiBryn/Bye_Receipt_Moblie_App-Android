package edu.dartmouth.cs.donewithreceipt;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class HistoryEntry implements Parcelable {
    private String cloudKey;
    private double subtotal;
    private String dateStr;
    //private String timeStr;
    private String storeName;

    public HistoryEntry(){ }

    //这个是和firebase做交互的
    public HistoryEntry (DataSnapshot snapshot){
        HashMap<String, Object> entryHashMap = (HashMap<String, Object>)(snapshot.getValue());
        cloudKey = snapshot.getKey();
        subtotal = Double.parseDouble(entryHashMap.get("subtotal").toString());
        dateStr = (String) entryHashMap.get("dateStr");
        //timeStr = (String) entryHashMap.get("timeStr");
        storeName = (String) entryHashMap.get("storeName");
    }

    //想把一个object放在一个Extra(StartActivity之后)里面
    protected HistoryEntry(Parcel in) {
        cloudKey = in.readString();
        subtotal = in.readDouble();
        dateStr = in.readString();
        //timeStr = in.readString();
        storeName = in.readString();
    }

    //感觉是把它放在historyFragment里面的
    public void setHistoryEntry(HistoryEntry entry){
        cloudKey = entry.getCloudKey();
        subtotal = entry.getSubtotal();
        dateStr = entry.getDateStr();
        //timeStr = entry.getTimeStr();
        storeName = entry.getStoreName();
    }



    String getStoreName() {
        return storeName;
    }

    public String getCloudKey() {
        return cloudKey;
    }


//    String getTimeStr() {
//        return timeStr;
//    }

    String getDateStr() {
        return dateStr;
    }

    double getSubtotal() {
        return subtotal;
    }


    public void setSubtotal(double subtotal){
        this.subtotal = subtotal;
    }

    public void setDateStr(String dateStr){
        this.dateStr = dateStr;
    }

//    public void setTimeStr(String timeStr){
//        this.timeStr = timeStr;
//    }

    public void setStoreName(String storeName){
        this.storeName = storeName;
    }

    public void setCloudKey(String cloudKey) {
        this.cloudKey = cloudKey;
    }


    public static final Creator<HistoryEntry> CREATOR = new Creator<HistoryEntry>() {
        @Override
        public HistoryEntry createFromParcel(Parcel in) {
            return new HistoryEntry(in);
        }

        @Override
        public HistoryEntry[] newArray(int size) {
            return new HistoryEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cloudKey);
        dest.writeDouble(subtotal);
        dest.writeString(dateStr);
        //dest.writeString(timeStr);
        dest.writeString(storeName);
    }
}
