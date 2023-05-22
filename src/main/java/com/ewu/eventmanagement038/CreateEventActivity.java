package com.ewu.eventmanagement038;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {
    private TextView errormessage;
    private EditText name, place, datetime,capacity,budget,email,phone,description;
    private Button cancel, share, save;
    private RadioButton rdindoor,rdoutdoor,rdonline;
    private RadioGroup radioGroup;
    String eventname;
    private String existingkey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent i = this.getIntent();
        /*String userid = i.getStringExtra("user_id");
        String mail = i.getStringExtra("mail");*/

        name = findViewById(R.id.name);
        place = findViewById(R.id.place);
        datetime = findViewById(R.id.datetime);
        capacity = findViewById(R.id.capacity);
        budget = findViewById(R.id.budget);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        description = findViewById(R.id.description);
        errormessage = findViewById(R.id.errormessage);
        cancel = findViewById(R.id.cancel);
        share = findViewById(R.id.share);
        save = findViewById(R.id.save);
        rdindoor = findViewById(R.id.rdindoor);
        rdoutdoor = findViewById(R.id.rdoutdoor);
        rdonline = findViewById(R.id.rdonline);

        if(i.hasExtra("EVENT_KEY")) {
            existingkey = i.getStringExtra("EVENT_KEY");
            KeyValueDB db = new KeyValueDB(CreateEventActivity.this);
            String value = db.getValueByKey(existingkey);
            String values[] = value.split("---");
            name.setText(values[0]);
            place.setText(values[1]);
            datetime.setText(values[3]);
            capacity.setText(values[4]);
            budget.setText(values[5]);
            email.setText(values[6]);
            phone.setText(values[7]);
            description.setText(values[8]);
            if (values[2].equals(rdindoor.getText().toString())) {
                rdindoor.setChecked(true);
            } else if (values[2].equals(rdoutdoor.getText().toString())) {
                rdoutdoor.setChecked(true);
            } else if (values[2].equals(rdonline.getText().toString())) {
                rdonline.setChecked(true);
            }
            db.close();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error = "";
                String type = "";
                boolean in = rdindoor.isChecked();
                boolean out = rdoutdoor.isChecked();
                boolean on = rdonline.isChecked();
                if(in){
                    type = rdindoor.getText().toString();
                }if(out){
                    type = rdoutdoor.getText().toString();
                }
                if(on){
                    type = rdonline.getText().toString();
                }
                String name_text = name.getText().toString();
                String place_text = place.getText().toString();
                String datetime_text = datetime.getText().toString();
                String capacity_text = capacity.getText().toString();
                String budget_text = budget.getText().toString();
                String email_text = email.getText().toString();
                String phone_text = phone.getText().toString();
                String description_text = description.getText().toString();

                if (name_text.length()>20){
                    error += "Name field too long\n";
                }
                if (place_text.length()>30){
                    error += "Type shorter address\n";
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()){
                    error += "Invalid Email\n";
                }
                if (!phone_text.startsWith("01") || phone_text.length()!=11) {
                    error += "Phone Invalid\n";
                }
                if(!out && !in && !on){
                    error += "Event type was not selected\n";
                }
                if(error.length()>0){
                    errormessage.setTextColor(Color.RED);
                    errormessage.setText(error);
                }
                else {
                    errormessage.setText("Success");
                    errormessage.setTextColor(Color.parseColor("#76b9ed"));

                    String value = name_text+"---"+place_text+"---"+type+"---"+datetime_text+"---"+capacity_text+"---"+budget_text+"---"+email_text+"---"+phone_text+"---"+description_text;
                    KeyValueDB db = new KeyValueDB(CreateEventActivity.this);
                    /* write code to generate a unique id */
                    if(existingkey.length()==0){
                        String key = name_text + System.currentTimeMillis();
                        existingkey = key;
                        System.out.println(key);
                        // write code to save the information //
                        db.insertKeyValue(key,value);

                    }else{
                        db.updateValueByKey(existingkey,value);

                    }
                    db.close();

                    String[] keys = {"action", "id", "semester", "key", "event"};
                    String[] values = {"bakcup", "2019360018", "2023-1",existingkey, value};
                    httpRequest(keys, values);

                    /*System.out.println(name_text);
                    System.out.println(place_text);
                    System.out.println(datetime_text);
                    System.out.println(capacity_text);
                    System.out.println(budget_text);
                    System.out.println(email_text);
                    System.out.println(phone_text);
                    System.out.println(description_text);
                    System.out.println(key);*/
                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                        //updateEventListByServerData(data);
                        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }

        /*public void updateEventListByServerData(String data){
            try {
                JSONObject jo = new JSONObject(data);
                if (jo.has("events")) {
                    JSONArray ja = jo.getJSONArray("events");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject event = ja.getJSONObject(i);
                        String eventKey = event.getString("e_key");
                        String eventValue = event.getString("e_value");
                        // split eventValue to show in event list
                    }
                }
            }catch(Exception e){}
        }*/

}