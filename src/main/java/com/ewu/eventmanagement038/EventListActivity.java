package com.ewu.eventmanagement038;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private ListView lvEvents;
    private Button btnAddNew, btnExit;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);


        // initialize list-reference by ListView object defined in XML
        lvEvents = findViewById(R.id.lvEvents);
        btnAddNew = findViewById(R.id.btnAddNew);
        btnExit = findViewById(R.id.btnExit);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventListActivity.this, CreateEventActivity.class);
                startActivity(i);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
        // load events from database if there is any
        events = new ArrayList<>();
        adapter = new CustomEventAdapter(this, events);
        lvEvents.setAdapter(adapter);

        // handle the click on an event-list item

        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // String item = (String) parent.getItemAtPosition(position);
                System.out.println(position);
                System.out.println(events.get(position).key);
                Intent i = new Intent(EventListActivity.this, CreateEventActivity.class);
                i.putExtra("EVENT_KEY", events.get(position).key);
                startActivity(i);
            }
        });

        // handle the long-click on an event-list item
            lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //String message = "Do you want to delete event - "+events[position].name +" ?";
                String message = "Do you want to delete event - "+events.get(position).name +" ?";
                System.out.println(message);
                showDialog(message, "Delete Event", events.get(position).key);
                return true;
            }
        });

        loadData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] keys = {"action", "id", "semester"};
        String[] values = {"bakcup", "2019360018", "2023-1"};
        httpRequest(keys, values);
    }

    private void loadData() {
        events.clear();

        KeyValueDB db = new KeyValueDB(this);
        Cursor rows = db.execute("SELECT * FROM key_value_pairs");
        if (rows.getCount() == 0) {
            return;
        }
        //events = new Event[rows.getCount()];
        while (rows.moveToNext()) {
            String key = rows.getString(0);
            String eventData = rows.getString(1);

            String[] fieldValues = eventData.split("---");

            String name = fieldValues[0];
            String place = fieldValues[1];
            String eventType = fieldValues[2];
            String dateTime = fieldValues[3];
            String capacity = fieldValues[4];
            String budget = fieldValues[5];
            String email = fieldValues[6];
            String phone = fieldValues[7];
            String description = fieldValues[8];

            Event e = new Event(key, name, place, dateTime, capacity, budget, email, phone, description, eventType);
            events.add(e);
        }
        db.close();

        adapter.notifyDataSetChanged();
    }
    private void showDialog(String message, String title,String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        KeyValueDB db = new KeyValueDB(EventListActivity.this);
                        db.deleteDataByKey(key);
                        db.close();
                        dialog.cancel();
                        loadData();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert  = builder.create();
        alert.show();
    }
    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    public void updateEventListByServerData(String data){
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("events")) {
                events.clear();
                JSONArray ja = jo.getJSONArray("events");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject event = ja.getJSONObject(i);
                    String eventKey = event.getString("e_key");
                    String eventValue = event.getString("e_value");
                    // split eventValue to show in event list
                    String[] fieldValues = eventValue.split("---");

                    String name = fieldValues[0];
                    String place = fieldValues[1];
                    String eventType = fieldValues[2];
                    String dateTime = fieldValues[3];
                    String capacity = fieldValues[4];
                    String budget = fieldValues[5];
                    String email = fieldValues[6];
                    String phone = fieldValues[7];
                    String description = fieldValues[8];

                    Event e = new Event(eventKey, name, place, dateTime, capacity, budget, email, phone, description, eventType);
                    events.add(e);
                }
            }
        }
        catch(Exception e){

        }
        adapter.notifyDataSetChanged();
    }
}