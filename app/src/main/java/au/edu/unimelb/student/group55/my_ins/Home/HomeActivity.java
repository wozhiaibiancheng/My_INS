package au.edu.unimelb.student.group55.my_ins.Home;

//import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

//import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import au.edu.unimelb.student.group55.my_ins.Firebase.Comment;
import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.UniversalImageLoader;


public class HomeActivity extends AppCompatActivity{
    private static final String TAG = "Home Activity";

    private Context mContext = HomeActivity.this;

    //firebase
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;

    private static final int HOME_FRAGMENT = 1;

    //widgets
    private ViewPager myViewPager;
    private FrameLayout myFrameLayout;
    private RelativeLayout myRelativeLayout;
    private BottomNavigationViewEx bottomNavigationView;

    //vars
    private ArrayList<PhotoInformation> myPhotoInformations;
    private ArrayList<PhotoInformation> myPaginatedPhotos;
    private ArrayList<String> myFollowing;
    private ListView myListView;
    private HomeFragmentAdapter myAdapter;
    private int myResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"on create");
        setContentView(R.layout.home_fragment);

        myViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        myFrameLayout = (FrameLayout) findViewById(R.id.container);
        myRelativeLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        setupFirebaseAuth();

        initImageLoader();
        setBottom();
        getFollowing();
//        setPager();

    }

//    public void onCommentThreadSelected(PhotoInformation photoInformation, String callingActivity){
//
//        CommentFragment fragment  = new CommentFragment();
//        Bundle args = new Bundle();
//        args.putParcelable("photo information", photoInformation);
//        args.putString("home activity", TAG);
//        fragment.setArguments(args);
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack("View Comments");
//        transaction.commit();
//
//    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        myRelativeLayout.setVisibility( View.GONE);
        myFrameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        myRelativeLayout.setVisibility(View.VISIBLE);
        myFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(myFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }


    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

//    add camera and home tab in nav bar
//    private void setPager(){
//        SectionAdapter sectionAdapter = new SectionAdapter(getSupportFragmentManager());
//        sectionAdapter.addFragment(new CameraFragment());
//        sectionAdapter.addFragment(new HomeFragment());
//        ViewPager viewPager = (ViewPager)findViewById(R.id.body);
//        viewPager.setAdapter(sectionAdapter);
//
//        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
//    }

    //    set up bottom view
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(HomeActivity.this,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }


    private void setupFirebaseAuth(){

        myAuth = FirebaseAuth.getInstance();

        myAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
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


    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    myFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }
                // Return list of user ID to myFollowing list
                myFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d(TAG, "onDataChange: found user: " +
                        myFollowing);
                //get the photos
                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < myFollowing.size(); i++){
            final int count = i;
            Query query = reference
                    .child("posts")
                    .child(myFollowing.get(i))
                    .orderByChild("post_id")
                    .equalTo( myFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        System.out.println("Invoked here successfully");

                        PhotoInformation photoInformation = new PhotoInformation();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photoInformation.setPostMessage(objectMap.get(getString(R.string.post_message)).toString());
                        photoInformation.setPhotoID(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photoInformation.setUserID(objectMap.get(getString(R.string.field_user_id)).toString());
                        photoInformation.setDateCreated(objectMap.get(getString(R.string.field_date_created)).toString());
                        photoInformation.setImageUrl(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child("comments").getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photoInformation.setComments(comments);
                        myPhotoInformations.add(photoInformation);
                    }
                    if(count >= myFollowing.size() - 1){
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos(){
        myPaginatedPhotos = new ArrayList<>();
        if(myPhotoInformations != null){
            try{
                Collections.sort( myPhotoInformations, new Comparator<PhotoInformation>() {
                    @Override
                    public int compare(PhotoInformation o1, PhotoInformation o2) {
                        return o2.getDateCreated().compareTo(o1.getDateCreated());
                    }
                });

                int iterations = myPhotoInformations.size();

                if(iterations > 10){
                    iterations = 10;
                }

                myResults = 10;
                for(int i = 0; i < iterations; i++){
                    myPaginatedPhotos.add( myPhotoInformations.get(i));
                }

                myAdapter = new HomeFragmentAdapter(mContext, R.layout.home_fragment_cells, myPaginatedPhotos );
                myListView.setAdapter( myAdapter );

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(myPhotoInformations.size() > myResults && myPhotoInformations.size() > 0){

                int iterations;
                if(myPhotoInformations.size() > (myResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = myPhotoInformations.size() - myResults;
                }

                //add the new photos to the paginated results
                for(int i = myResults; i < myResults + iterations; i++){
                    myPaginatedPhotos.add( myPhotoInformations.get(i));
                }
                myResults = myResults + iterations;
                myAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

}




