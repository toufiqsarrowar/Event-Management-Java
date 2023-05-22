package com.ewu.eventmanagement038;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private Button btnToggle,btnGo,btnExit;
    private EditText etName, etMail, etPhone, etPassword, etRepassword, etUserid;
    private TextView tvTitle,tvExist;
    private LinearLayout llName, llMail, llPhone, llPassword, llRepassword, llUserid, llExist, llRemid, llRemlogin;
    private CheckBox chkRemid,chkRemlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("user_account", MODE_PRIVATE);
        boolean is_login_check = sp.getBoolean("remlogin", false);
        if(is_login_check){
            Intent i = new Intent (SignupActivity.this, EventListActivity.class);
            startActivity(i);
            return;
        }
        boolean is_userid_check = sp.getBoolean("remid", false);
        setContentView(R.layout.activity_signup);
        btnToggle = findViewById(R.id.btnToggle);
        btnExit = findViewById(R.id.btnExit);
        btnGo = findViewById(R.id.btnGo);
        etName = findViewById(R.id.etName);
        etMail = findViewById(R.id.etMail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etRepassword = findViewById(R.id.etRepassword);
        etUserid = findViewById(R.id.etUserid);
        tvTitle = findViewById(R.id.tvTitle);
        tvExist = findViewById(R.id.tvExist);
        llName = findViewById(R.id.llName);
        llMail = findViewById(R.id.llMail);
        llPhone = findViewById(R.id.llPhone);
        llPassword = findViewById(R.id.llPassword);
        llRepassword = findViewById(R.id.llRepassword);
        llUserid = findViewById(R.id.llUserid);
        llExist = findViewById(R.id.llExist);
        llRemid = findViewById(R.id.llRemid);
        llRemlogin = findViewById(R.id.llRemlogin);
        chkRemid = findViewById(R.id.chkRemid);
        chkRemlogin = findViewById(R.id.chkRemlogin);


        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toggleValue = btnToggle.getText().toString();
                boolean islogin = toggleValue.equalsIgnoreCase("login");
                if(islogin){
                    tvTitle.setText("Login");
                    tvExist.setText("Don't Have an Account");
                    btnToggle.setText("signup");
                    //hide field
                    llName.setVisibility(View.GONE);
                    llMail.setVisibility(View.GONE);
                    llPhone.setVisibility(View.GONE);
                    llRepassword.setVisibility(View.GONE);
                }
                else{
                    tvTitle.setText("SignUp");
                    tvExist.setText("Already Have an Account?");
                    btnToggle.setText("Login");
                    llName.setVisibility(View.VISIBLE);
                    llMail.setVisibility(View.VISIBLE);
                    llPhone.setVisibility(View.VISIBLE);
                    llRepassword.setVisibility(View.VISIBLE);
                }
            }
        });
        if(is_userid_check){
            btnToggle.callOnClick();
            etUserid.setText(sp.getString("user_id", ""));
        }

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toggleValue = btnToggle.getText().toString();
                boolean islogin = toggleValue.equalsIgnoreCase("login");
                boolean isremid = chkRemid.isChecked();
                boolean isremlogin = chkRemlogin.isChecked();

                if(islogin){

                    String error = "";
                    String name_text = etName.getText().toString();
                    String mail_text = etMail.getText().toString();
                    String phone_text = etPhone.getText().toString();
                    String userid_text = etUserid.getText().toString();
                    String password_text = etPassword.getText().toString();
                    String repassword_text = etRepassword.getText().toString();
                    if(name_text.isEmpty() || mail_text.isEmpty() || phone_text.isEmpty() || userid_text.isEmpty() || password_text.isEmpty() || repassword_text.isEmpty()){
                        error += "Please Fill-up All Fieldsn";
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(mail_text).matches()){
                        error += "Invalid Emailn";
                    }
                    if (!phone_text.startsWith("01") || phone_text.length()!=11) {
                        error += "Phone Invalidn";
                    }
                    if(!password_text.equals(repassword_text)){
                        error += "Password Did Not matchn";
                    }
                    if(error.length()>0){
                        Toast.makeText(SignupActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                    else {
                        SharedPreferences sp = getSharedPreferences("user_account", MODE_PRIVATE);
                        SharedPreferences.Editor spEditor = sp.edit();
                        spEditor.putString("name", name_text);
                        spEditor.putString("mail", mail_text);
                        spEditor.putString("phone", phone_text);
                        spEditor.putString("user_id", userid_text);
                        spEditor.putString("password", password_text);
                        spEditor.putBoolean("remid", isremid);
                        spEditor.putBoolean("remlogin", isremlogin);
                        spEditor.apply();
                        etName.setText("");etMail.setText("");etPhone.setText("");etUserid.setText("");etPassword.setText("");etRepassword.setText("");
                        Intent i = new Intent (SignupActivity.this, EventListActivity.class);
                        i.putExtra("userid", userid_text);
                        i.putExtra("mail", mail_text);
                        startActivity(i);
                    }
                }
                else{

                    String userid_text = etUserid.getText().toString();
                    String password_text = etPassword.getText().toString();
                    SharedPreferences sp = getSharedPreferences("user_account", MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sp.edit();
                    String userid_check = sp.getString("user_id", "");
                    String mail_check = sp.getString("mail","");
                    String password_check = sp.getString("password", "");
                    if(userid_check.equals(userid_text) && password_check.equals(password_text)){
                        spEditor.putBoolean("remid", isremid);
                        spEditor.putBoolean("remlogin", isremlogin);
                        spEditor.apply();
                        etUserid.setText("");etPassword.setText("");
                        Intent i = new Intent (SignupActivity.this, EventListActivity.class);
                        i.putExtra("userid", userid_check);
                        i.putExtra("mail", mail_check);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(SignupActivity.this, "User Authentication Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }

}
