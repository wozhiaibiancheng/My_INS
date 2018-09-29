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

    @Override
    public void onCreate(){
        super.onCreate();

        String date = DateFormat.getDateTimeInstance().format(new Date());

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef = storage.getReference().child( uid ).child(date + ".jpg");

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageData = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PhotoUploadService.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUri = imagesRef.getDownloadUrl();
                Toast.makeText(PhotoUploadService.this, "Upload image successfully", Toast.LENGTH_SHORT).show();
            }
        });

        String location = "Location not available";
        String message = "message not available";
        //uid + date + location + message + url +

        String post_message = location + "," + message + "," + downloadUri;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child( uid ).child( date ).setValue( post_message );
    }

    @Override
    public IBinder onBind(Intent intent) {
        imageData = intent.getByteArrayExtra("resultImage");
        return null;
    }
}
