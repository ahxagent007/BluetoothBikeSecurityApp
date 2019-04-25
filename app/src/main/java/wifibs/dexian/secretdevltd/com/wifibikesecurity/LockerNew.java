package wifibs.dexian.secretdevltd.com.wifibikesecurity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;

public class LockerNew extends AppCompatActivity {


    private ImageView iv_status,iv_connected,iv_alarm;
    private TextView tv_statusText;//, tv_connected;
    private String status;
    private String password;;
    private Button btn_setting, btn_Connect, btn_Alarm,btn_connectToBike;
    private String keyWord;

    private String BIKE_LOCKED = "LOCKED";
    private String BIKE_UNLOCKED = "UNLOCKED";

    //Server variable
    private boolean serverStatus = false;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //User
    UserData ud;

    //BLUETOOTH
    private BluetoothSPP bt;

    Menu menu;
    Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_locker);
        //TESTING
        //startActivity(new Intent(getApplicationContext(),Login.class));
        //TESTING

        password = getPassword();
        status = getStatus();
        keyWord = getKeyWord();

        iv_status = findViewById(R.id.IV_newStatus);
        tv_statusText = findViewById(R.id.tv_newStatusText);
        //tv_connected = findViewById(R.id.tv_connected);
        btn_setting = findViewById(R.id.btn_neSetting);
        //btn_Connect = findViewById(R.id.btn_Connect);
        iv_alarm = findViewById(R.id.IV_newMakeBeep);
        iv_connected = findViewById(R.id.IV_newConnected);
        btn_connectToBike = findViewById(R.id.btn_connectToBike);

        /*Thread ReceiveThread = new Thread(new MyServerThread());
        ReceiveThread.start();*/


        //BLUE TOOTH *****************************************************************************************
        bt = new BluetoothSPP(getApplicationContext());
        //bt = new BluetoothSPP(this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        //bt.autoConnect("Bike Security");

        bt.setOnDataReceivedListener(new OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                tv_statusText.setText(message);

                if(message.equalsIgnoreCase("LOCKED")){
                    status = BIKE_LOCKED;
                    storeStatus(status);
                }else if(message.equalsIgnoreCase("UNLOCKED")){
                    status = BIKE_UNLOCKED;
                    storeStatus(status);
                }
                serverStatus = true;

                changeLockIMAGE();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                tv_statusText.setText("Bluetooth Not connect");
                //menu.clear();
                //getMenuInflater().inflate(R.menu.menu_connection, menu);
                serverStatus = false;
                changeUI();

            }

            public void onDeviceConnectionFailed() {
                tv_statusText.setText("Bluetooth Connection failed");
                serverStatus = false;
                changeUI();
            }

            public void onDeviceConnected(String name, String address) {
                tv_statusText.setText("Bluetooth Connected to " + name);
                //menu.clear();
                //getMenuInflater().inflate(R.menu.menu_disconnection, menu);
                serverStatus = true;
                changeUI();
            }
        });



        //BLUETOOTH *****************************************************************************************

        changeLockIMAGE();
        changeUI();

        //Connecting to Firebase
        firebaseConnection();

        if(status.equalsIgnoreCase(BIKE_LOCKED)){
            iv_status.setImageResource(R.drawable.new_locked);
            //tv_statusText.setText("Touch to unlock your bike");
        }else if(status.equalsIgnoreCase(BIKE_UNLOCKED)){
            iv_status.setImageResource(R.drawable.new_unlocked);
            //tv_statusText.setText("Touch to lock your bike");
        }


        iv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("XIAN","Line touch isConnected() = "+s.isConnected());
                changeUI();

                Log.i("XIAN","Server connection status : "+serverStatus);

                if(serverStatus){

                    if(status.equalsIgnoreCase(BIKE_LOCKED)){

                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(LockerNew.this);
                        View myView = getLayoutInflater().inflate(R.layout.custom_password_dialog, null);

                        Button btn_passDone = myView.findViewById(R.id.btn_passDone);
                        final EditText et_password = myView.findViewById(R.id.et_password);


                        myBuilder.setView(myView);
                        final AlertDialog Dialog = myBuilder.create();
                        Dialog.show();

                        btn_passDone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String userPass = et_password.getText().toString();

                                if(userPass.equals(password)){

                                    //status = BIKE_UNLOCKED;

                                    //SendDataBT(getKeyWord());

                                    if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                                        SendDataBT(getKeyWord());
                                    }

                                    //storeStatus(status);

                                    changeLockIMAGE();

                                    //iv_status.setImageResource(R.drawable.new_unlocked);
                                    //tv_statusText.setText("Touch to lock your bike");

                                    Dialog.cancel();

                                }else{
                                    Toast.makeText(getApplicationContext(),"Password don't match!",Toast.LENGTH_SHORT).show();
                                    et_password.setText("");
                                }
                            }
                        });

                    }else{

                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(LockerNew.this);
                        View myView = getLayoutInflater().inflate(R.layout.custom_password_dialog, null);

                        Button btn_passDone = myView.findViewById(R.id.btn_passDone);
                        final EditText et_password = myView.findViewById(R.id.et_password);


                        myBuilder.setView(myView);
                        final AlertDialog Dialog = myBuilder.create();
                        Dialog.show();

                        btn_passDone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String userPass = et_password.getText().toString();

                                if(userPass.equals(password)){

                                    //status = BIKE_LOCKED;

                                    if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                                        SendDataBT(getKeyWord());
                                    }



                                    //storeStatus(status);

                                    changeLockIMAGE();

                                    //iv_status.setImageResource(R.drawable.new_locked);
                                    //tv_statusText.setText("Touch to unlock your bike");

                                    Dialog.cancel();

                                }else{
                                    Toast.makeText(getApplicationContext(),"Password don't match!",Toast.LENGTH_SHORT).show();
                                    et_password.setText("");
                                }
                            }
                        });

                    }

                    storeStatus(status);

                }else{
                    Toast.makeText(getApplicationContext(),"Server is not Connected",Toast.LENGTH_SHORT).show();
                    Log.i("XIAN","Server connection status : "+serverStatus);
                    changeUI();
                    changeLockIMAGE();
                    //checkServer();
                    //iv_connected.setImageResource(R.drawable.new_disconnected);
                    //tv_connected.setText("Not Connected!");
                    //tv_connected.setTextColor(Color.parseColor("#c10023"));
                }

            }
        });


        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent at = new Intent(getApplicationContext(), Setting.class);
                startActivity(at);

            }
        });

        iv_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendDataBT("ALARM");
            }
        });

        btn_connectToBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTconnect();
            }
        });



        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                    @Override
                                    public void run() {
                                        //Called each time when 1000 milliseconds (1 second) (the period parameter)

                                        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                                            serverStatus = true;
                                            Log.i("XIAN","TIMER If: State = "+bt.getServiceState()+"\t StayConnected = "+BluetoothState.STATE_CONNECTED);
                                        }else{
                                            Log.i("XIAN","TIMER Else: State = "+bt.getServiceState()+"\t StayConnected = "+BluetoothState.STATE_CONNECTED);
                                        }
                                    }

                                },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                1000);

    }

    private void changeLockIMAGE(){
        if(status.equalsIgnoreCase(BIKE_LOCKED)){
            iv_status.setImageResource(R.drawable.new_locked);
            tv_statusText.setText("Touch to unlock your bike");

        }else if(status.equalsIgnoreCase(BIKE_UNLOCKED)){
            iv_status.setImageResource(R.drawable.new_unlocked);
            tv_statusText.setText("Touch to lock your bike");
        }
    }

    private void buttonChange(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Your code
                if(serverStatus){
                    btn_connectToBike.setEnabled(false);
                }else{
                    btn_connectToBike.setEnabled(true);
                }
            }
        });

    }

    private void BTconnect(){

        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
			if(!serverStatus){
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

            }

        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
			    serverStatus = true;
                changeUI();
			    Log.i("XIAN","if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) === TRUE");
        }else{
            Log.i("XIAN","if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) === FALSE");
            serverStatus = false;
            changeUI();
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_android_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_ANDROID);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(id == R.id.menu_device_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(id == R.id.menu_disconnect) {
            if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
                bt.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!bt.isBluetoothEnabled()) {
            Log.i("XIAN","ONSTART if");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            Log.i("XIAN","ONSTART else");
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                //setup();
                //SendDataBT("BIKE_XIAN");
            }
        }
    }

    public void SendDataBT(String data) {
        if(data.length() != 0) {
        bt.send(data, true);
        }
        Log.i("XIAN","DATA TO BE SEND :" +data);
    }

        @Override
    protected void onRestart() {
        super.onRestart();

/*        this.finish();
        Intent i = getIntent();
        startActivity(i);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
        t.cancel();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("XIAN","onAcivityResult");
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                Log.i("XIAN","DEVICE OTHER DEYA LAGTEPARE EKHANE");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                //setup();
                SendDataBT("BIKE_XIAN");
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    private void storeStatus(String status){
        SharedPreferences mSharedPreferences = getSharedPreferences("IP_PORT", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("STATUS",status);
        mEditor.apply();
    }

    private String getStatus(){
        SharedPreferences mSharedPreferences = getSharedPreferences("IP_PORT", MODE_PRIVATE);
        return mSharedPreferences.getString("STATUS","UNLOCK");
    }

    private String getPassword(){
        SharedPreferences mSharedPreferences = getSharedPreferences("Password", MODE_PRIVATE);
        return mSharedPreferences.getString("PASSWORD","0000");
    }

    private void changeUI(){

        Log.i("XIAN","Change called");

        if(serverStatus){
            iv_connected.setImageResource(R.drawable.new_connected);
            //tv_connected.setText("Connected!");
            //tv_connected.setTextColor(Color.parseColor("#00911f"));
        }else{
            iv_connected.setImageResource(R.drawable.new_disconnected);
            //tv_connected.setText("Not Connected!");
            //tv_connected.setTextColor(Color.parseColor("#c10023"));
        }

    }

    private String getKeyWord(){
        SharedPreferences mSharedPreferences = getSharedPreferences("KeyWord", MODE_PRIVATE);
        return mSharedPreferences.getString("KEYWORD","BIKE_XIAN");
    }



    private void storeKeyWord(String key){
        SharedPreferences mSharedPreferences = getSharedPreferences("KeyWord", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("KEYWORD",key);
        mEditor.apply();
        Log.i("XIAN","new Password store : "+key);
    }


    private void storePassword(String pass){
        SharedPreferences mSharedPreferences = getSharedPreferences("Password", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("PASSWORD",pass);
        mEditor.apply();
        Log.i("XIAN","new Password store : "+pass);
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
