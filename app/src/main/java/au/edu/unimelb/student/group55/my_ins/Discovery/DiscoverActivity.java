package au.edu.unimelb.student.group55.my_ins.Discovery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.edu.unimelb.student.group55.my_ins.Firebase.User;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.Profile.ViewProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;

public class DiscoverActivity extends AppCompatActivity {
    private static final String TAG = "Discover Activity";
    private static final int ACTIVITY_NUMBER = 1;

    private Context mContext = DiscoverActivity.this;
    private EditText mySearchParameter;
    private ListView myListView;
    private List<User> myUserList;
    private UserAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        mySearchParameter = (EditText) findViewById(R.id.search);
        myListView = (ListView) findViewById(R.id.listView);
        //Invoke the functions here
        setBottomNavigation();
        initializeEditListener();
    }

    private void initializeEditListener(){
        myUserList = new ArrayList<>();

        mySearchParameter.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //Once user has input something, search in the Firebase for occurrence
            @Override
            public void afterTextChanged(Editable s) {
                String userName = mySearchParameter.getText().toString().toLowerCase(Locale.getDefault());
                searchForUsername(userName);
            }
        });
    }


    private void searchForUsername(String keyword){
        myUserList.clear();
        if(keyword.length() ==0){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child("users");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        myUserList.add(singleSnapshot.getValue(User.class));
                        //update the users list view
                        refreshUsersList();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child("users")
                    .orderByChild("username").equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        myUserList.add(singleSnapshot.getValue(User.class));
                        //update the users list view
                        refreshUsersList();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void refreshUsersList(){
        myAdapter = new UserAdapter(mContext, R.layout.discovery_userlist, myUserList );
        myListView.setAdapter( myAdapter );
        myListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUserID = myUserList.get( position ).getUser_id();
                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // If the selected user is myself, navigate to my profile directly
                if(selectedUserID.equals( myUserID )){
                    startMyProfileActivity();
                }
                // Otherwise navigate to the selected user profile
                else{
                    startUserActivity( position );
                }

            }
        });
    }

    private void startUserActivity(int position){
        //navigate to profile activity
        Intent intent =  new Intent(mContext, ViewProfileActivity.class);
        intent.putExtra("calling_activity", ACTIVITY_NUMBER);
        intent.putExtra("intent_user", myUserList.get(position) );
        startActivity(intent);
    }

    private void startMyProfileActivity(){
        // Navigate to my profile
        Intent intent = new Intent( mContext, ProfileActivity.class );
        startActivity( intent );
    }

    //    set up bottom view
    private void setBottomNavigation(){
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(DiscoverActivity.this,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }
}

