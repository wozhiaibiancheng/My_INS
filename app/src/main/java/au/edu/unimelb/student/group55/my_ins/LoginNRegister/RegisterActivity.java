package au.edu.unimelb.student.group55.my_ins.LoginNRegister;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.MainActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import java.util.Random;
import java.util.zip.Inflater;


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
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Random rand = new Random();
    private int randomSize = 9999999;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.register_layout);
        myContext = RegisterActivity.this;
        firebaseMethods = new FirebaseMethods(myContext);
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
                    Log.d(TAG,"register input valid");

                    databaseReference.child("users");
                    firebaseMethods.registerNewEmail(email, password, username);
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
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        myAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    System.out.println("user != null");
                    // User is signed in

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            check if username exist
                            if(firebaseMethods.usernameExists(username,dataSnapshot)) {
                                System.out.println("username exists");
                                username += username + Integer.toString(rand.nextInt(randomSize));
                                Toast.makeText(myContext, "The username isn't available, suffix appended.", Toast.LENGTH_SHORT).show();
                            }else {
                                System.out.println("username not exist");}


                            firebaseMethods.addUser(email,username,"","");

                            SharedPreferences prefs = getSharedPreferences(MainActivity.MySharedPrefs, Context.MODE_PRIVATE);
                            prefs.edit().putBoolean( "firststart", false );
                            //Intent intent = new Intent( RegisterActivity.this, MainActivity.class );
                            Toast.makeText(myContext, "You have successfully registered as" + myUsername, Toast.LENGTH_LONG).show();
                            finish();
                            }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    System.out.println("username == null");
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