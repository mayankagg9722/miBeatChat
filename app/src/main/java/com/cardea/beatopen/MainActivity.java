package com.cardea.beatopen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    static String chatname=null;
    ProgressDialog progressdialog;
    String link=null;
    ListView listview;
    String[] chatusers=null;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharealert();

  /*      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/


        listview=(ListView)findViewById(R.id.list);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getBaseContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("toname",adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sharealert(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Login");

        Context context = this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText editText=new EditText(this);
        editText.setHint("Username");
        final EditText editText1=new EditText(this);
        editText1.setHint("Password");
        editText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(editText);
        layout.addView(editText1);
        alert.setView(layout);
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name=editText.getText().toString();
                String pass=editText1.getText().toString();
                String url="https://www.snowbarter.com/gdghack/checkUser.php?name="+name+"&password="+pass;
                //Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                if(name.equals("")||pass.equals("")){
                    Toast.makeText(MainActivity.this, "Fill data", Toast.LENGTH_SHORT).show();
                    sharealert();
                }
                else {
                   // Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                    sendrequest(url);
                }
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Enter your name first", Toast.LENGTH_SHORT).show();
                sharealert();
            }
        });
        alert.create().show();
    }

    public void sendrequest(String url){
        progressdialog=new ProgressDialog(this);
        progressdialog.setMessage("Wait");
        progressdialog.show();
        RequestQueue queue= Volley.newRequestQueue(this);
        //String url="https://www.snowbarter.com/mickey/sendmessage.php";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();

                progressdialog.dismiss();

                if(response.toString().equals("notRegistered")){
                    Toast.makeText(MainActivity.this, "Not Registered", Toast.LENGTH_SHORT).show();
                    sharealert();
                }

                else{
                    chatname=response.toString();
                    link="https://www.snowbarter.com/gdghack/fetchchatusers.php?name="+chatname;
                    sendlistrequest(link);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                sharealert();
                progressdialog.dismiss();
            }
        });

        RetryPolicy policy=new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        queue.add(stringRequest);

    }

    public void sendlistrequest(String url){
        progressdialog=new ProgressDialog(this);
        progressdialog.setMessage("Wait");
        progressdialog.show();
        RequestQueue queue= Volley.newRequestQueue(this);
        //String url="https://www.snowbarter.com/mickey/sendmessage.php";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_SHORT).show();

                progressdialog.dismiss();

                chatusers=response.toString().split(",");
                adapter=new ArrayAdapter<String>(getBaseContext(),R.layout.rowlayout,R.id.text,chatusers);
                listview.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        });

        RetryPolicy policy=new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        queue.add(stringRequest);

    }
}
