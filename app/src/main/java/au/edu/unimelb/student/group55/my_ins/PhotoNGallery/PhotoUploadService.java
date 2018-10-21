package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityPosts;
import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserAccountSetting;

public class PhotoUploadService extends Service {
    @Nullable
    private FirebaseUser user;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    private UserAccountSetting userAccountSetting;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private int imageNumber;
    private long postNum;
    private String uid;
    private byte[] imageData;
    private String downloadLink;
    private String IMAGE_PATH;
    private Bitmap resultImageBitmap;
    private ByteArrayOutputStream baos;
    private String postMessage;

    private String latitude;
    private String longitude;


    @Override
    public void onCreate() {
        super.onCreate();

        mFirebaseMethods = new FirebaseMethods( PhotoUploadService.this );
        setupFirebaseAuth();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        storage = FirebaseStorage.getInstance();
        postNum = getPostNum();

        baos = new ByteArrayOutputStream();

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        IMAGE_PATH = intent.getStringExtra("file path");
        latitude = intent.getStringExtra( "latitude" );
        longitude = intent.getStringExtra( "longitude" );
        resultImageBitmap = readImage(IMAGE_PATH);

        postMessage = intent.getStringExtra("post message");

        resultImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageData = baos.toByteArray();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        final String currentDate1 = String.valueOf(simpleDateFormat.format(calendar.getTime()));

        final StorageReference imagesRef = storage.getReference().child(uid).child(currentDate1 + ".jpg");

        UploadTask uploadTask = imagesRef.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PhotoUploadService.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(PhotoUploadService.this, "Upload image successfully", Toast.LENGTH_SHORT).show();

            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    //Once the image is uploaded successfully, show the message and get its download URL
                    Uri downloadUri = task.getResult();
                    downloadLink = downloadUri.toString();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    String currentDate2 = String.valueOf(simpleDateFormat.format(calendar.getTime()));

//                    String currentDate2 = DateFormat.getDateTimeInstance().format(new Date());
                    String photoID = myRef.child("posts").child(uid).push().getKey();

                    PhotoInformation photoInformation = new PhotoInformation();
                    photoInformation.setDateCreated(currentDate2);
                    photoInformation.setUserID(uid);
                    photoInformation.setImageUrl(downloadLink);
                    photoInformation.setLatitude( latitude );
                    photoInformation.setLongitude(longitude);

//                    photoInformation.setLatitude( "1" );
//                    photoInformation.setLongitude("2");
                    photoInformation.setPhotoID(photoID);
                    photoInformation.setPostMessage(postMessage);

                    mDatabase.child("posts").child(uid).child(photoID).setValue(photoInformation);


                    String activityPhotoID = myRef.child("activity").child("posts").child(uid).push().getKey();

                    ActivityPosts activityPosts = new ActivityPosts();
                    activityPosts.setUserID(uid);
                    activityPosts.setDateCreated(currentDate2);
                    activityPosts.setImageUrl(downloadLink);

                    mDatabase.child("activity").child("posts").child(uid).child(activityPhotoID).setValue(activityPosts);

                    postNum += 1;

                    //update post number in user_account_settings
                    FirebaseDatabase.getInstance().getReference()
                            .child("user_account_settings")
                            .child(user.getUid())
                            .child("posts")
                            .setValue(postNum);

                    Toast.makeText(PhotoUploadService.this,
                            "Set Upload message record successfully", Toast.LENGTH_SHORT).show();

                    //After the upload task finished, finish the serivce itself
                    stopSelf();

                } else {
                    Toast.makeText(PhotoUploadService.this,
                            "Failed to set Upload message", Toast.LENGTH_SHORT).show();
                    stopSelf();
                    // Handle failures
                    // ...
                }
            }
        });

        return startId;

    }


    private long getPostNum() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("user_account_settings")
                .orderByChild("user_id").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userAccountSetting = ds.getValue(UserAccountSetting.class);
                    postNum = userAccountSetting.getPosts();
                    System.out.println("getPostNum： " + postNum);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return postNum;
    }




    public Bitmap readImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        return bitmap;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageNumber = mFirebaseMethods.getImageNumber(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}