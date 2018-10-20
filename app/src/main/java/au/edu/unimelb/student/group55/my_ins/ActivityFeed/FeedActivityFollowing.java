package au.edu.unimelb.student.group55.my_ins.ActivityFeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityFollowing;
import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityLikes;
import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;

import static android.view.View.GONE;

public class FeedActivityFollowing extends AppCompatActivity {

    private static final String TAG = "FeedActivityFollowing";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Context context;

    private ProgressBar progressBar;

    private ArrayList<String> myFollowing;
    private ArrayList<ActivityFollowing> activityFollowingsList;
    private ArrayList<ActivityFollowing> toDisplayFollowingList;
    private ListView myListView;

    private TextView cancel;

    private FeedFollowingAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_following );
        Log.d("INFO","onCreate started!");
        myFollowing = new ArrayList<>();
        activityFollowingsList = new ArrayList<ActivityFollowing>();

        myListView = (ListView) findViewById(R.id.listView);
        cancel = (TextView) findViewById( R.id.following_feed_cancel );

        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(GONE);

        context = FeedActivityFollowing.this;
        FirebaseAuth();

        setBottom();
        getFollowing();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getFollowing(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    myFollowing.add(singleSnapshot.child("user_id").getValue().toString());
                }
                // Return list of user ID to myFollowing list
                myFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                //get all the user activities
                getUserFollowingEvents(myFollowing);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserFollowingEvents(final ArrayList<String> myFollowingList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < myFollowingList.size(); i++) {
            final int count = i;
            Query query = reference
                    .child( "activity" )
                    .child( "following" )
                    .child( myFollowingList.get( i ) );
            query.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        System.out.println( "Invoked here successfully" );

                        ActivityFollowing activityFollowing = new ActivityFollowing(  );
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        activityFollowing.setDateToFollow( objectMap.get( "dateToFollow" ).toString() );
                        activityFollowing.setFollowerID( objectMap.get( "followerID" ).toString() );
                        activityFollowing.setFollowID( objectMap.get( "followID" ).toString() );

                        activityFollowingsList.add( activityFollowing );

                    }
                    if (count >= myFollowingList.size() - 1) {
                        // display following events here
                        displayUserFollowingEvent();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            } );
        }
    }

    private void displayUserFollowingEvent(){
        toDisplayFollowingList = new ArrayList<>();
        if(activityFollowingsList != null){
            try{
                Collections.sort( activityFollowingsList, new Comparator<ActivityFollowing>() {
                    @Override
                    public int compare(ActivityFollowing a1, ActivityFollowing a2) {
                        return a2.getDateToFollow().compareTo(a1.getDateToFollow());
                    }
                });
                Log.e(TAG, "The activity Following List is: " + activityFollowingsList );

                int iterations = activityFollowingsList.size();

                if(iterations > 3){
                    iterations = 3;
                }
                Log.e(TAG, "The iteration number is: " + iterations );

                for(int i = 0; i < iterations; i++){
                    toDisplayFollowingList.add( activityFollowingsList.get(i));
                }

//                // This need to be changed as well

                Log.e(TAG, "The following list is: " + toDisplayFollowingList );

                myAdapter = new FeedFollowingAdapter( FeedActivityFollowing.this, R.layout.activity_feed_following_cells, toDisplayFollowingList );
                myListView.setAdapter( myAdapter );
//                myAdapter = new HomeFragmentAdapter(getActivity(), R.layout.home_fragment_cells, toDisplayFollowingList );
//                myListView.setAdapter( myAdapter );

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    //    set up bottom view
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(FeedActivityFollowing.this,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }




    /**
     * Setup the firebase auth object
     */
    private void FirebaseAuth() {

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}
