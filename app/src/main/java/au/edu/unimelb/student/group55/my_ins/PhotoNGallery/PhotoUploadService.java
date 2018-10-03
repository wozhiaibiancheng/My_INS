package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;

public class PhotoUploadService extends Service {
    @Nullable

    private FirebaseUser user;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;

    private String uid;
    private byte[] imageData;
    private Task<Uri> downloadUri;
    private String downloadLink;
    private Uri imageUri;
    private String FILE_NAME;
    private String WORKING_DIRECTORY;

    private String PHOTO_UPLOAD_SERVICE = "Photo Upload Service";
    private ByteArrayOutputStream baos;

    private String currentLocation;
    private String postMessage;

    @Override
    public void onCreate(){
        super.onCreate();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        storage = FirebaseStorage.getInstance();
        baos = new ByteArrayOutputStream();

        currentLocation = "Location not available";

        //This is the user input message attached with the image
        // will need to be passed from Apply Filter activity through Intent
        postMessage = "message not available";

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand( intent, flags, startId );

        imageUri = (Uri) intent.getParcelableExtra(PHOTO_UPLOAD_SERVICE);

        FILE_NAME = intent.getStringExtra( "filename" );
        WORKING_DIRECTORY = intent.getStringExtra( "working directory" );


        Bitmap resultImageBitmap = new ImageSaver(this).
                setFileName(FILE_NAME).
                setDirectoryName(WORKING_DIRECTORY).
                load();

//        Bitmap resultImageBitmap = (Bitmap) intent.getParcelableExtra( PHOTO_UPLOAD_SERVICE );
        resultImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageData = baos.toByteArray();

        String currentDate1 = DateFormat.getDateTimeInstance().format(new Date());

        final StorageReference imagesRef = storage.getReference().child( uid ).child(currentDate1 + ".jpg");

        UploadTask uploadTask = imagesRef.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PhotoUploadService.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Once the image is uploaded successfully, show the message and get its download URL
                downloadUri = imagesRef.getDownloadUrl();
                downloadLink = downloadUri.toString();
                Toast.makeText(PhotoUploadService.this, "Upload image successfully", Toast.LENGTH_SHORT).show();

                String currentDate2 = DateFormat.getDateTimeInstance().format(new Date());


                //Get the image url and offload to Real-time database here
                String post_message = currentLocation + "," + postMessage + "," + downloadLink;
                mDatabase.child( uid ).child( currentDate2 ).setValue( post_message );
                Toast.makeText(PhotoUploadService.this,
                        "Set Upload message record successfully", Toast.LENGTH_SHORT).show();

                //After the upload task finished, finish the serivce itself
                stopSelf();
            }
        });

        return startId;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
