package com.example.earth_quake_app;

public class ListObject{
    private String mFirstNum;
    private String mState;
    private String mDate;

    ListObject(String mag,String State,String date){
        mFirstNum = mag;
        mState = State;
        mDate = date;
    }
    public String getFirstNum(){return mFirstNum;}
    public String getmState(){return mState;}
    public String getmSecondNum(){return mDate;}
}
