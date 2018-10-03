package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.InputStream;

import au.edu.unimelb.student.group55.my_ins.R;

public class ApplyFilters extends AppCompatActivity {

    ImageView imageView;
    Bitmap selectedImage;
    private String FILE_NAME = "2.jpg";
    public static final String WORKING_DIRECTORY = "MyINS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.photo_gallery );

        File dataDirectory = new File(this.getExternalFilesDir(null), WORKING_DIRECTORY);
        if( ! dataDirectory.exists() ) {
            dataDirectory.mkdirs();
        }

        imageView = (ImageView) findViewById(R.id.imageView);

        TextView shareClose = (TextView) findViewById(R.id.gallery_cancel);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView nextScreen = (TextView) findViewById(R.id.gallery_next);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                saveResultImage( FILE_NAME, WORKING_DIRECTORY, selectedImage );

                Intent photoUploadService = new Intent( ApplyFilters.this, PhotoUploadService.class );
                photoUploadService.putExtra( "filename", FILE_NAME );
                photoUploadService.putExtra( "working directory", WORKING_DIRECTORY );
                
                startService( photoUploadService );

                finish();

            }
        });

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    public void saveResultImage(String filename, String directoryName, Bitmap bitmap){
        new ImageSaver(this).
                setFileName(filename).
                setDirectoryName(directoryName).
                save(bitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    InputStream imageStream = getContentResolver().openInputStream(resultUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap( selectedImage );
                }
                catch(Exception e){

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
