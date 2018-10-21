package au.edu.unimelb.student.group55.my_ins.Home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.Firebase.Comment;

public class CommentFragment extends Fragment {

    private static final String TAG = "CommentFragment";

    public CommentFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;

    //widgets
    private ImageView myBackArrow, myCheckMark;
    private EditText myComment;
    private ListView myListView;

    //vars
    private PhotoInformation photoInformation;
    private ArrayList<Comment> myComments;
    private Context myContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.comment_fragment, container, false);
        myBackArrow = (ImageView) view.findViewById(R.id.left_icon);
        myCheckMark = (ImageView) view.findViewById(R.id.ivPostComment);
        myComment = (EditText) view.findViewById(R.id.comment);
        myListView = (ListView) view.findViewById(R.id.listView);
        myComments = new ArrayList<>();
        myContext = getActivity();

        myBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });


//        try{
        photoInformation = getPhotoFromBundle();
        setupFirebaseAuth();
//        }catch (NullPointerException e){ }

        return view;
    }

    private void setupWidgets(){

        CommentFragmentAdapter adapter = new CommentFragmentAdapter( myContext,
                R.layout.comment_layout, myComments );
        myListView.setAdapter(adapter);

        myCheckMark.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!myComment.getText().toString().equals("")){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment( myComment.getText().toString());
                    myComment.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(getActivity(), "Please input something for your comment before sending", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myBackArrow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), HomeActivity.class);
                    startActivity(intent);


                }catch (Exception e){
                    getActivity().getSupportFragmentManager().popBackStack();

                }

            }
        });
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void addNewComment(String newComment){
        String commentID = myRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id( FirebaseAuth.getInstance().getCurrentUser().getUid());

        //insert into user_photos node
        myRef.child("posts")
                .child( photoInformation.getUserID())
                .child( photoInformation.getPhotoID())
                .child("comments")
                .child(commentID)
                .setValue(comment);

    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private String getCallingActivityFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            System.out.println("11111111111");
            return bundle.getString("photo");
        }else{
            return null;
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private PhotoInformation getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable("photo");
        }else{
            return null;
        }
    }

           /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        myAuth = FirebaseAuth.getInstance();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference();

        myAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

//        if(photoInformation.getComments().size() == 0){
        myComments.clear();

        photoInformation.setComments( myComments );
        setupWidgets();
//        }

//
        myRef.child( "posts")
                .child( photoInformation.getUserID() )
                .child( photoInformation.getPhotoID())
                .child( "comments")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: child added.");
                        System.out.println("photoid: " + photoInformation.getPhotoID());
                        Query query = myRef
                                .child( "posts")
                                .child( photoInformation.getUserID() )
                                .orderByChild( "photoID")
                                .equalTo( photoInformation.getPhotoID());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                                    PhotoInformation photoInformation = new PhotoInformation();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photoInformation.setPostMessage(objectMap.get("postMessage").toString());
                                    photoInformation.setPhotoID(objectMap.get("photoID").toString());
                                    photoInformation.setUserID(objectMap.get("userID").toString());
                                    photoInformation.setDateCreated(objectMap.get("dateCreated").toString());
                                    photoInformation.setImageUrl(objectMap.get( "imageUrl").toString());


                                    myComments.clear();


                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child( "comments").getChildren()){
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        myComments.add(comment);
                                    }

                                    photoInformation.setComments( myComments );

                                    CommentFragment.this.photoInformation = photoInformation;

                                    setupWidgets();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query cancelled.");
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        myAuth.addAuthStateListener( myAuthListener );
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myAuthListener != null) {
            myAuth.removeAuthStateListener( myAuthListener );
        }
    }

}





















