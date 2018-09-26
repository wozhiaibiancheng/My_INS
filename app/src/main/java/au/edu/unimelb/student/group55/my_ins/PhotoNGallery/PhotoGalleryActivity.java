package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import au.edu.unimelb.student.group55.my_ins.R;

public class PhotoGalleryActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.photo_gallery);
        imageView = (ImageView) findViewById(R.id.imageView);

        TextView shareClose = (TextView) findViewById(R.id.gallery_cancel);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        TextView nextScreen = (TextView) findViewById(R.id.gallery_next);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(this, NextActivity.class);
                //intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                //startActivity(intent);
            }
        });

        pickImage();
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), uri );
                imageView.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Toast.makeText(this, "Cannot load image from photo gallery", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
