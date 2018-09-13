package au.edu.unimelb.student.group55.my_ins.LoginNRegister;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseAuthentication;
import au.edu.unimelb.student.group55.my_ins.MainActivity;
import au.edu.unimelb.student.group55.my_ins.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context myContext;
    private String email, username, password;
    private EditText myEmail, myPassword, myUsername;
    //private TextView loadingPleaseWait;
    private Button Register_button;
    //private ProgressBar mProgressBar;


    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private FirebaseAuthentication firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.register_layout);
        myContext = RegisterActivity.this;
        firebaseAuth = new FirebaseAuthentication(myContext);
        getInputValue();
        setupFirebaseAuth();
        initialization();
    }

    private void initialization(){
        Register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = myEmail.getText().toString();
                username = myUsername.getText().toString();
                password = myPassword.getText().toString();

                if(checkInputs(email, username, password)){
                    firebaseAuth.registerNewEmail(email, password, username);
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password){
        //Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(myContext, "You need to input all the required information!.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void getInputValue(){
        Log.d(TAG, "getInputValue");
        myEmail = (EditText) findViewById(R.id.input_email);
        myUsername = (EditText) findViewById(R.id.input_username);
        Register_button = (Button) findViewById(R.id.btn_register);
        myPassword = (EditText) findViewById(R.id.input_password);
        myContext = RegisterActivity.this;
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        myAuth = FirebaseAuth.getInstance();

        myAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in

                    SharedPreferences prefs = getSharedPreferences(MainActivity.MySharedPrefs, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean( "firststart", false );

                    //Intent intent = new Intent( RegisterActivity.this, MainActivity.class );
                    Toast.makeText(myContext, "You have successfully registered as" + myUsername, Toast.LENGTH_SHORT).show();
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(myAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myAuthListener != null) {
            myAuth.removeAuthStateListener(myAuthListener);
        }
    }


}
