package wifibs.dexian.secretdevltd.com.wifibikesecurity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    private EditText et_newEmail, et_newPass;
    private Button btn_newLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);



        et_newEmail = findViewById(R.id.et_newEmail);
        et_newPass = findViewById(R.id.et_newPass);
        btn_newLogin = findViewById(R.id.btn_newLogin);

        String log[] = getLogin();

        if(log[0].equals("*") && log[1].equals("*")){

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser() == null){
                        Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"User Logged-in", Toast.LENGTH_LONG).show();
                    }
                }
            };



            btn_newLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String email = et_newEmail.getText().toString();
                    String pass = et_newPass.getText().toString();

                    signIn(email,pass);
                }
            });

        }else{

            Toast.makeText(getApplicationContext(),"Successfully Logged-in",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),LockerNew.class));
            this.finish();

        }
    }

    private void signIn(String email, String pass){

        if(!email.equals("") && !pass.equals("")){


            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_LONG).show();
                        setLogin("SecretDevelopersLtd@gmail.com","YouAreGeniusBro");
                        startActivity(new Intent(getApplicationContext(),LockerNew.class));


                    }else {
                        Toast.makeText(getApplicationContext(),"Wrong Password or Email",Toast.LENGTH_LONG).show();
                        setLogin("*","*");
                    }
                }
            });

        }else {
            Toast.makeText(getApplicationContext(),"Please fill up Email and Password!",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();

        //FirebaseAuth.getInstance().signOut();
    }

    private String[] getLogin(){
        String arr[] = new String[2];
        SharedPreferences mSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        arr[0] = mSharedPreferences.getString("fEmail","*");
        arr[1] = mSharedPreferences.getString("fPass","*");

        return arr;
    }

    private void setLogin(String email, String pass){
        SharedPreferences mSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("fEmail",email);
        mEditor.putString("fPass",pass);

        mEditor.apply();
    }
}


