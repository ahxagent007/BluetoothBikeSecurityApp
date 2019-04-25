package wifibs.dexian.secretdevltd.com.wifibikesecurity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.Files;

public class Setting extends AppCompatActivity {

    //private Button btn_changeIP;
    private Button btn_changePassWord;
    private Button btn_changeKeyowrd;
    private Button btn_aboutUser;


    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //User
    UserData ud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firebaseConnection();

        //btn_changeIP = findViewById(R.id.btn_changeIP);
        btn_changePassWord = findViewById(R.id.btn_changePassWord);
        //btn_changeKeyowrd = findViewById(R.id.btn_changeKeyowrd);
        btn_aboutUser = findViewById(R.id.btn_aboutUser);

        btn_aboutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userData = "NAME: "+ud.getUsername()+"\nEMAIL: "+ud.getEmail()+"\nMobile number: "+ ud.getMobile_number()+"\nRegistration Date: "+ud.getReg_date();

                Toast.makeText(getApplicationContext(),""+userData,Toast.LENGTH_LONG).show();
            }
        });

        /*btn_changeIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(Setting.this);
                View myView = getLayoutInflater().inflate(R.layout.custom_port_ip_change, null);

                final EditText et_portChange = myView.findViewById(R.id.et_portChange);
                final EditText et_IPChange = myView.findViewById(R.id.et_IPChange);
                Button btn_ChangeDone = myView.findViewById(R.id.btn_ChangeDone);
                Button btn_ChangeCancel = myView.findViewById(R.id.btn_ChangeCancel);



                Log.i("XIAN","getIP : "+getIP()+"  getPort : "+getPort());

                et_portChange.setText(""+getPort());
                et_IPChange.setText(""+getIP());

                myBuilder.setView(myView);
                final AlertDialog Dialog = myBuilder.create();
                Dialog.show();

                btn_ChangeDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int port = Integer.parseInt(et_portChange.getText().toString());
                        String IP = et_IPChange.getText().toString();

                        storePort(port);
                        storeIP(IP);

                        Toast.makeText(getApplicationContext(),"Change Saved!",Toast.LENGTH_SHORT).show();

                        Dialog.cancel();
                    }
                });

                btn_ChangeCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog.cancel();
                    }
                });


            }
        });*/

        btn_changePassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(Setting.this);
                View myView = getLayoutInflater().inflate(R.layout.custome_pass_change, null);

                final EditText et_oldPassWord = myView.findViewById(R.id.et_oldPassWord);
                final EditText et_newPassWord01 = myView.findViewById(R.id.et_newPassWord01);
                final EditText et_newPassWord02 = myView.findViewById(R.id.et_newPassWord02);
                Button btn_passSave = myView.findViewById(R.id.btn_passSave);
                Button btn_passBack = myView.findViewById(R.id.btn_passBack);

                myBuilder.setView(myView);
                final AlertDialog Dialog = myBuilder.create();
                Dialog.show();

                btn_passSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String oldPass = et_oldPassWord.getText().toString();
                        String newPass01 = "36";
                        String newPass02 = "69";

                        if(!et_newPassWord01.getText().toString().equals(null) || !et_newPassWord02.getText().toString().equals(null)){
                            newPass01 = et_newPassWord01.getText().toString();
                            newPass02 = et_newPassWord02.getText().toString();
                        }


                        if(oldPass.equals(getPassword())){
                            if(newPass01.equals(newPass02)){

                                storePassword(newPass01);

                                Toast.makeText(getApplicationContext(),"Password Changed Successfully!",Toast.LENGTH_SHORT).show();

                                Dialog.cancel();

                            }else if(newPass01.equals("36") || newPass02.equals("69")){
                                Toast.makeText(getApplicationContext(),"Please fill up password fields",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"New password don't match",Toast.LENGTH_SHORT).show();

                                et_newPassWord01.setText("");
                                et_newPassWord02.setText("");
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Old password don't match",Toast.LENGTH_SHORT).show();
                            et_oldPassWord.setText("");
                        }

                    }
                });

                btn_passBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog.cancel();
                    }
                });
            }
        });

        /*btn_changeKeyowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(Setting.this);
                View myView = getLayoutInflater().inflate(R.layout.custom_text_change, null);

                Button btn_keyDone = myView.findViewById(R.id.btn_keyDone);
                final EditText et_keyword = myView.findViewById(R.id.et_keyword);


                myBuilder.setView(myView);
                final AlertDialog Dialog = myBuilder.create();
                Dialog.show();

                btn_keyDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        storeKeyWord(et_keyword.getText().toString());
                        Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                        Dialog.cancel();
                    }
                });


            }
        });*/

    }


    private void storePassword(String pass){
        SharedPreferences mSharedPreferences = getSharedPreferences("Password", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("PASSWORD",pass);
        mEditor.apply();
        Log.i("XIAN","new Password store : "+pass);
    }

    private void storeKeyWord(String key){
        SharedPreferences mSharedPreferences = getSharedPreferences("KeyWord", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("KEYWORD",key);
        mEditor.apply();
        Log.i("XIAN","new Password store : "+key);
    }

    private String getKeyWord(){
        SharedPreferences mSharedPreferences = getSharedPreferences("KeyWord", MODE_PRIVATE);
        return mSharedPreferences.getString("KEYWORD","Bike0001");
    }


    private String getPassword(){
        SharedPreferences mSharedPreferences = getSharedPreferences("Password", MODE_PRIVATE);
        return mSharedPreferences.getString("PASSWORD","0000");
    }



    public void firebaseConnection(){

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser fu = mAuth.getCurrentUser();
        String uID = fu.getUid();

        /*UserData ud = new UserData("XIAN","liya@liya.com","key0001","199.22.66.33",1000,"passs","10 JUNE 2018");

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uID);*/


        //Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uID);


        Log.i("XIAN","FIREBASE DATABASE "+mDatabase.child(uID).getKey());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ud = dataSnapshot.getValue(UserData.class);

                Log.i("XIAN", "usER NAME IS :: " + ud.username);
                Log.i("XIAN"," "+dataSnapshot.toString());

                //storeIP(ud.getIp());
                //storePort(ud.getPort());
                storeKeyWord(ud.getKey());
                storePassword(ud.getPass());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}


