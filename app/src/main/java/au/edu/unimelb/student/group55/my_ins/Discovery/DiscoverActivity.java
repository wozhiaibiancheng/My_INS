package au.edu.unimelb.student.group55.my_ins.Discovery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    private static final int ACTIVITY_NUM = 1;

    private Context mContext = DiscoverActivity.this;

    //widgets
    private EditText mSearchParam;
    private ListView mListView;

    //vars
    private List<User> mUserList;
    private UserAdapter mAdapter;

    private TextView suggestText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("INFO","onCreate started!");
        setContentView(R.layout.activity_discovery);
        mSearchParam = (EditText) findViewById(R.id.search);
        mListView = (ListView) findViewById(R.id.listView);
        suggestText = (TextView)findViewById(R.id.textView);
        suggestText.setVisibility(View.GONE);

        setBottom();
        initTextListener();
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();

        mSearchParam.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (hasWindowFocus()){
                    suggestText.setVisibility(View.VISIBLE);
                    String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                    searchForMatch(text);
                }
            }
        });

        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });
    }



    private void searchForMatch(String keyword){
        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        //update the users list view
        if(keyword.length() ==0){
            suggestText.setVisibility(View.VISIBLE);
//            suggest friends
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child("users");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                        Log.d(TAG, "suggest friends:" + singleSnapshot.getValue(User.class).toString());

                        mUserList.add(singleSnapshot.getValue(User.class));
                        //update the users list view
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            suggestText.setVisibility(View.GONE);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child("users")
                    .orderByChild("username").equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());

                        mUserList.add(singleSnapshot.getValue(User.class));
                        //update the users list view
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");

        mAdapter = new UserAdapter(mContext, R.layout.discovery_userlist, mUserList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());

                //navigate to profile activity
                Intent intent =  new Intent(mContext, ViewProfileActivity.class);
                intent.putExtra("calling_activity", "discover_activity");
                intent.putExtra("intent_user", mUserList.get(position) );
                Log.d(TAG, mUserList.get(position).getUsername());
                startActivity(intent);
            }
        });
    }

    //    set up bottom view
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(DiscoverActivity.this,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }
}

