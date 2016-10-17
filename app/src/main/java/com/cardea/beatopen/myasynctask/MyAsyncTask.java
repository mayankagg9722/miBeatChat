package com.cardea.beatopen.myasynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cardea.beatopen.BluetoothSetupService;
import com.cardea.beatopen.ChatActivity;
import com.cardea.beatopen.Globals;
import com.cardea.beatopen.HomeActivity;
import com.cardea.beatopen.MainActivity;
import com.cardea.beatopen.PlottingActivity;
import com.cardea.beatopen.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cardea.beatopen.Globals.START;
import static com.cardea.beatopen.Globals.STATE_CONNECTED;
import static com.cardea.beatopen.Globals.selectedSignalType;
import static com.cardea.beatopen.HomeActivity.btService;

/**
 * Created by mayank on 23-07-2016.
 */
public class MyAsyncTask extends AsyncTask<Void,Integer,String> {
    Context context;
    int i=0;
    String realString;
    String[] realStringArray;
    int[] realIntegerArray=new int[500];
    long sum=0;
    int thresholdAvg=0;
    ArrayList<Integer> peaksIndex=new ArrayList<Integer>();
    int max;
    public static BluetoothSetupService btService = null;
    ArrayList<Integer> heartRate=new ArrayList<Integer>();
    static ArrayList<Integer> mainHeartRate=new ArrayList<Integer>();


    public MyAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(Void... voids) {
        synchronized (this){
            while(true) {
                try {
                    HomeActivity.btService.write(START.getBytes());
                    wait(1000);

                    peaksIndex.clear();
                    heartRate.clear();
                    sum=0;
                    //Log.v("Asynk", "YES");
                    Log.v("realtimeecg", getRealArray());

                    realString=getRealArray();
                    realString=realString.substring(0,(realString.length()-1));
                    realStringArray=realString.split(",");
                    makeNull(realIntegerArray);
                    for(i=0;i<realStringArray.length;i++){
                        realIntegerArray[i]=Integer.parseInt(realStringArray[i]);
                        sum+=Integer.parseInt(realStringArray[i]);
                    }
                    Log.v("real", Arrays.toString(realIntegerArray));

                    if(realIntegerArray.length>0)
                    thresholdAvg= (int) (sum/realIntegerArray.length);

                    Log.v("threshhold", String.valueOf(thresholdAvg));


                    for(i=0;i<realIntegerArray.length;i++) {
                        if(realIntegerArray[i]>thresholdAvg){
                            if((i>0)&&(i<realIntegerArray.length-1)&&(realIntegerArray[i]>realIntegerArray[i-1])
                                    &&(realIntegerArray[i]>realIntegerArray[i+1])){
                                peaksIndex.add(i);
                            }
                        }
                    }

                    Log.v("peaksIndex", Arrays.toString(peaksIndex.toArray()));


                    for(i=1;i<peaksIndex.size();i++){
                        int diff=peaksIndex.get(i)-peaksIndex.get(i-1);
                        if(diff>7){
                        int hr= (int)(((float)(60.00)/(float)(diff))*(float)15);
                        //if(hr<110 && hr>50) {
                            heartRate.add(hr);}
                        //}
                    }

                    Log.v("hearRate", Arrays.toString(heartRate.toArray()));

                    sum=0;
                    for(i=0;i<heartRate.size();i++){
                        sum+=heartRate.get(i);
                    }
                    if(heartRate.size()>0) {
                        detectMood((int) (sum / heartRate.size()));
                        mainHeartRate.add((int) (sum / heartRate.size()));
                    }

                    Log.v("mainhearRate", Arrays.toString(mainHeartRate.toArray()));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getRealArray(){
        String temp="";
        for(i=(Globals.location+1);i<250;i++){
            temp=temp+ String.valueOf(Globals.ecgvalues[i])+",";
        }
        for(i=0;i<=Globals.location;i++){
            temp=temp+ String.valueOf(Globals.ecgvalues[i])+",";
        }
        return temp;
    }

    public void makeNull(int[] name){
        for (int i:name)
        {
            i= -1;
        }
    }
    public void detectMood(int newHeartRate){
        if(newHeartRate<60){
            Globals.mood="sad";
            ChatActivity.namefunc("sad");
           // if(context instanceof ChatActivity){
           //     ((ChatActivity)context).namefunc("sad");
           // }

        }
        else if(newHeartRate>90){
            Globals.mood="happy";
            ChatActivity.namefunc("happy");
        }
        else{
            Globals.mood="none";
            ChatActivity.namefunc("normal");
        }

    }
}
