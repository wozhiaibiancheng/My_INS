package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;

public class ApplyFilters extends AppCompatActivity {

    ImageView imageView;
    Bitmap selectedImage;
    private String FILE_NAME = "/test.jpg";
    public static final String WORKING_DIRECTORY = "MyINS/test.jpg";
    private String imagePath;

    public String cropPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.photo_gallery );

        imagePath = applicationFolder();

//        Toast.makeText(ApplyFilters.this, dataDirectory, Toast.LENGTH_SHORT).show();

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

                // Upload the image in the background to avoid stop front-end UI
                Intent photoUploadService = new Intent( ApplyFilters.this, PhotoUploadService.class );
                photoUploadService.putExtra( "file path", imagePath );
                startService( photoUploadService );
                finish();

            }
        });

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }


    public String applicationFolder(){

        //change the result image name to ensure image is stored properly
        String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "test.jpg";
        return temp;
    }

    public void startImageFilter(String sourcePath, String destinationPath){
//        String workingDirectory = path + filename;
        EditImageActivity.start(this, sourcePath, destinationPath, 100, true);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get the result image path from image crop procedure

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    cropPath = resultUri.getPath();

                    startImageFilter( cropPath, imagePath );
                    Toast.makeText(ApplyFilters.this, imagePath, Toast.LENGTH_SHORT).show();

                }
                catch(Exception e){

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


        // After having finished the filters & brightness contrast setting
        // return the image data to image view

        if (requestCode == 100) { // same code you used while starting
            String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
//
//
//            InputStream imageStream = getContentResolver().openInputStream(resultUri);
//            selectedImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap( bitmap );
        }

    }



}
