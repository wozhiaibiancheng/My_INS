package au.edu.unimelb.student.group55.my_ins.Discovery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import au.edu.unimelb.student.group55.my_ins.Firebase.User;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.Profile.ViewProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;

public class DiscoverActivity extends AppCompatActivity {
    private static final String TAG = "Discover Activity";

    private Context mContext = DiscoverActivity.this;

    private EditText mSearchParam;
    private ListView mListView;

    private List<User> mUserList;
    private UserAdapter mAdapter;
    private Set<String> suggestID;

    private TextView suggestText;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("INFO", "onCreate started!");
        setContentView(R.layout.activity_discovery);


        mSearchParam = (EditText) findViewById(R.id.search);
        mListView = (ListView) findViewById(R.id.listView);
        suggestText = (TextView) findViewById(R.id.textView);
        suggestText.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);

        setBottom();
        initTextListener();
        progressBar.setVisibility(View.GONE);
    }


    private void initTextListener() {
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();

        mSearchParam.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (hasWindowFocus()) {
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


//suggestion or search
    private void searchForMatch(String keyword) {
        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        suggestID = new HashSet();
        progressBar.setVisibility(View.VISIBLE);

        //update the users list view
        if (keyword.length() == 0) {

            suggestText.setVisibility(View.VISIBLE);


            final Set<String> followingID = new HashSet();

//            suggest friends
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            final String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Query query = reference.child("following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {

                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            followingID.add(String.valueOf(singleSnapshot.child("user_id").getValue()));

                            DataSnapshot innerFollowingDs = singleSnapshot.child("innerFollowing");
                            for (DataSnapshot ds : innerFollowingDs.getChildren()) {
                                suggestID.add(String.valueOf(ds.getValue()));
                            }

                            DataSnapshot innerFollowersDs = singleSnapshot.child("innerFollowers");
                            for (DataSnapshot ds : innerFollowersDs.getChildren()) {
                                System.out.println("innferFollowers" + String.valueOf(ds.getValue()));

                                suggestID.add(String.valueOf(ds.getValue()));

                            }
                        }


                        System.out.println("suggest size!!: " + suggestID.size());

                        if (suggestID.size() > 0) {

                            for (String id : suggestID) {

                                System.out.println("followed user: " + followingID);

                                //    dont't suggest himself
                                //    don't suggest anyone has been followed by the current user
                                if (!id.equals(currentUID) && !followingID.contains(id)) {
                                    System.out.println("id: " + id);

                                    Query queryUser = reference.child("users")
                                            .orderByChild("user_id").equalTo(id);

                                    queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                                //                                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());

                                                mUserList.add(singleSnapshot.getValue(User.class));
                                            }

                                            if (mUserList.size() > 0) {
                                                System.out.println("111111");
                                                System.out.println("188 update");
                                                //update the users list view
                                                updateUsersList();
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                System.out.println("222222");
                                                //if no friends suggests after filtered

                                                Query _query = reference.child("users");
                                                _query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                at most suggest 6 users
                                                        int userCt = 0;
                                                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                                            //   don't suggest himself
                                                            //   don't suggest whoever already followed
                                                            if (!singleSnapshot.child("user_id").getValue().equals(currentUID)
                                                                    && !followingID.contains(singleSnapshot.child("user_id").getValue())) {
                                                                Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());
                                                                userCt += 1;
                                                                mUserList.add(singleSnapshot.getValue(User.class));
                                                            }
                                                            if (userCt >= 6) {
                                                                break;
                                                            }
                                                        }
                                                        System.out.println("215 update");
                                                        //update the users list view
                                                        updateUsersList();
                                                        progressBar.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }
                            }
                        } else {
                            System.out.println("don't have any suggestion!");


                            Query _query = reference.child("users");
                            _query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                at most suggest 6 users
                                    int userCt = 0;
                                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                                    don't suggest himself
//                                    don't suggest whoever already followed
                                        if (!singleSnapshot.child("user_id").getValue().equals(currentUID)
                                                && !followingID.contains(singleSnapshot.child("user_id").getValue())) {
                                            Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());
                                            userCt += 1;
                                            mUserList.add(singleSnapshot.getValue(User.class));
                                        }
                                        if (userCt >= 6) {
                                            break;
                                        }
                                    }

                                    System.out.println("262 update");
                                    //update the users list view
                                    updateUsersList();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    } else {
                        System.out.println("not following anyone!");


                        Query _query = reference.child("users");
                        _query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                at most suggest 6 users
                                int userCt = 0;
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                                    don't suggest himself
//                                    don't suggest whoever already followed
                                    if (!singleSnapshot.child("user_id").getValue().equals(currentUID)
                                            && !followingID.contains(singleSnapshot.child("user_id").getValue())) {
                                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());
                                        userCt += 1;
                                        mUserList.add(singleSnapshot.getValue(User.class));
                                    }
                                    if (userCt >= 6) {
                                        break;
                                    }
                                }
                                System.out.println("298 update");
                                //update the users list view
                                updateUsersList();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {

//            search people
            suggestText.setVisibility(View.GONE);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child("users")
                    .orderByChild("username").equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());

                        mUserList.add(singleSnapshot.getValue(User.class));
                    }
                    System.out.println("337 update");
                    //update the users list view
                    updateUsersList();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUsersList() {
        Log.d(TAG, "updateUsersList: updating users list");

        mAdapter = new UserAdapter(mContext, R.layout.discovery_userlist, mUserList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());


//                click the current user himself
                if (mUserList.get(position).getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    Log.d(TAG, mUserList.get(position).getUsername());
                    startActivity(intent);
                } else {
                    //navigate to profile activity
                    Intent intent = new Intent(mContext, ViewProfileActivity.class);
                    intent.putExtra("calling_activity", "discover_activity");
                    intent.putExtra("intent_user", mUserList.get(position));
                    startActivity(intent);
                }
            }
        });
    }

    //    set up bottom view
    private void setBottom() {
        Log.d(TAG, "bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(DiscoverActivity.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }
}

