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
import au.edu.unimelb.student.group55.my_ins.Home.HomeFragmentAdapter;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;

import static android.view.View.GONE;

public class FeedActivityLikes extends AppCompatActivity {
    private static final String TAG = "FeedActivityLikes";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Context context;

    private ProgressBar progressBar;

    private ArrayList<String> myFollowing;
    private ArrayList<ActivityLikes> activityLikesList;
    private ArrayList<ActivityLikes> toDisplayLikesList;
    private ListView myListView;

    private TextView cancel;
    private FeedLikesAdapter myAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_feed_likes );
        Log.d("INFO","onCreate started!");
        myFollowing = new ArrayList<>();
        activityLikesList = new ArrayList<ActivityLikes>();
        toDisplayLikesList = new ArrayList<ActivityLikes>();

        myListView = (ListView) findViewById(R.id.listView);
        cancel = (TextView) findViewById( R.id.likes_feed_cancel );

        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(GONE);

        context = FeedActivityLikes.this;
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
                getUserLikesEvents(myFollowing);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getUserLikesEvents(final ArrayList<String> myFollowingList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < myFollowingList.size(); i++) {
            final int count = i;
            Query query = reference
                    .child( "activity" )
                    .child( "likes" )
                    .child( myFollowingList.get( i ) );
            query.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        System.out.println( "Invoked here successfully" );

                        ActivityLikes activityLikes = new ActivityLikes(  );
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        activityLikes.setDateLiked( objectMap.get( "dateLiked" ).toString() );
                        activityLikes.setLikerID( objectMap.get( "likerID" ).toString() );
                        activityLikes.setPosterID( objectMap.get( "posterID" ).toString() );
                        activityLikes.setPostID( objectMap.get( "postID" ).toString() );

                        activityLikesList.add( activityLikes );

                    }
                    if (count >= myFollowingList.size() - 1) {
                        // display following events here
                        displayUserLikesEvent();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            } );
        }
    }

    private void displayUserLikesEvent(){
        toDisplayLikesList = new ArrayList<>();
        if(activityLikesList != null){
            try{
                Collections.sort( activityLikesList, new Comparator<ActivityLikes>() {
                    @Override
                    public int compare(ActivityLikes a1, ActivityLikes a2) {
                        return a2.getDateLiked().compareTo(a1.getDateLiked());
                    }
                });

                int iterations = activityLikesList.size();

                if(iterations > 10){
                    iterations = 10;
                }

                for(int i = 0; i < iterations; i++){
                    toDisplayLikesList.add( activityLikesList.get(i));
                }

                // This need to be changed as well
                myAdapter = new FeedLikesAdapter(this, R.layout.activity_feed_likes_cells, toDisplayLikesList );
                myListView.setAdapter( myAdapter );

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
        BottomNavTool.enableNav(FeedActivityLikes.this,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }



    // Always check the login state of the user
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
                    // If we detect no user login information, navigate to the Login screen
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
