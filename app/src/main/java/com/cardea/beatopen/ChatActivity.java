package com.cardea.beatopen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cardea.beatopen.myasynctask.MyAsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    String from=null,to=null;

    ListView listView;
    ImageView send;
    EditText message;
    String[] chats;
    ArrayList<String> chatsList=null;
    ProgressDialog progressdialog;
    ArrayAdapter<String> chatting=null;
    String url;
    String res;
    ImageView back;
    Context context;
    ImageView emoji;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // MyAsyncTask myAsyncTask=new MyAsyncTask(this);
        setContentView(R.layout.activity_chat);

        context=this;
        listView=(ListView)findViewById(R.id.chatlist);
        send=(ImageView)findViewById(R.id.send);
        emoji=(ImageView)findViewById(R.id.emoji);
        message=(EditText)findViewById(R.id.chattext);

        back = (ImageView)findViewById(R.id.backimage);


        from=MainActivity.chatname;
        to=getIntent().getExtras().getString("toname");


        chatsendrequest();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendurl= null;
                try {
                    sendurl = "https://www.snowbarter.com/gdghack/postchat.php?" +
                            "from="+from+"&to="+to+"&message="+ URLEncoder.encode(message.getText().toString(), "UTF-32");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                };


                Log.v("senurl",sendurl);

                // chats.add(message.getText().toString());


                String tem = ("You: "+message.getText().toString());
                chatsList.add(tem);

                Log.v("list",chatsList.toString());


                chatting.notifyDataSetChanged();

                notifychangerequest(sendurl);
            }
        });

        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showalert();

            }
        });
       // MyTasks myTask=new MyTasks();
       // myTask.execute();
    }

    public void chatsendrequest(){
        progressdialog=new ProgressDialog(this);
        progressdialog.setMessage("Wait");
        progressdialog.show();
        url="https://www.snowbarter.com/gdghack/fetchchat.php?from="+from+"&to="+to;
        RequestQueue queue= Volley.newRequestQueue(this);
        //String url="https://www.snowbarter.com/mickey/sendmessage.php";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();

                progressdialog.dismiss();

                Log.v("chat",url+" "+response.toString());

                try {
                    res = URLDecoder.decode(response.toString(), "UTF-32");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    res=response.toString();
                }

                chats= res.split(",");

                chatsList= new ArrayList<String>(Arrays.asList(chats));

                Log.v("list",chatsList.toString());

                //Log.v("chat",chats[0]);

              // chatting=new ArrayAdapter<String>(getBaseContext(),R.layout.chatlayout,R.id.text, (String[]) chatsList.toArray());

                chatting=new ArrayAdapter<String>(getApplicationContext(),R.layout.chatlayout,R.id.text,chatsList);

                listView.setAdapter(chatting);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        });

        RetryPolicy policy=new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        queue.add(stringRequest);

    }
    public void notifychangerequest(String url){

        RequestQueue queue= Volley.newRequestQueue(this);
        //String url="https://www.snowbarter.com/mickey/sendmessage.php";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();
                message.setText("");
                Log.v("chat",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RetryPolicy policy=new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        queue.add(stringRequest);

    }

    public static void namefunc(String name){

        Log.v("mood",name);

    }

    public void showalert(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        ImageView image=new ImageView(this);
        if(Globals.mood.equals("sad")){
            image.setImageResource(R.drawable.sademoji);
        }
        else if(Globals.mood.equals("happy")){
            image.setImageResource(R.drawable.happyemoji);
        }else{
            image.setImageResource(R.drawable.normalemoji);
        }
        alert.setMessage("Suggested emoji").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setView(image);
        alert.create().show();
    }




}
