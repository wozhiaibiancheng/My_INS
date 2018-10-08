package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
    private String IMAGE_PATH;

    private Bitmap resultImageBitmap;
    private ByteArrayOutputStream baos;

    private String currentLocation;
    private String postMessage;
    private String likeUrl;
    private String commentUrl;

    private double latitude;
    private double longitude;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand( intent, flags, startId );

        IMAGE_PATH = intent.getStringExtra( "file path" );
        resultImageBitmap = readImage( IMAGE_PATH );

        postMessage = intent.getStringExtra( "post message" );

        resultImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageData = baos.toByteArray();

        final String currentDate1 = DateFormat.getDateTimeInstance().format(new Date());

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

                likeUrl = currentDate2 + "-likes";
                commentUrl = currentDate2 + "-comments";

                //Get the image url and offload to Real-time database here
                String post_message = uid + "," + currentLocation + "," + postMessage + "," + downloadLink
                        + "," + likeUrl + "," + commentUrl;
                mDatabase.child( uid ).child( currentDate2 ).child( "post" ).setValue( post_message );

                Toast.makeText(PhotoUploadService.this,
                        "Set Upload message record successfully", Toast.LENGTH_SHORT).show();

                //After the upload task finished, finish the serivce itself
                stopSelf();
            }
        });

        return startId;

    }

    public Bitmap readImage(String imagePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        return bitmap;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}