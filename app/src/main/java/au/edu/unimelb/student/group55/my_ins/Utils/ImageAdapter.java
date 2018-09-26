package au.edu.unimelb.student.group55.my_ins.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import java.util.ArrayList;
import au.edu.unimelb.student.group55.my_ins.R;

public class ImageAdapter extends ArrayAdapter<String>{

    private Context context;
    private LayoutInflater inflater;
    private int layoutResource;
    private String prefix;
    private ArrayList<String> imageURLs;



    public ImageAdapter(@NonNull Context context,  int layoutResource, String prefix, ArrayList<String> imageURLs) {
        super(context, layoutResource,imageURLs);
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        this.prefix = prefix;
        this.imageURLs = imageURLs;;
    }

    private static class ViewHolder{
        SquareImageView img;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(layoutResource,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.img = (SquareImageView)convertView.findViewById(R.id.grid_img);
            viewHolder.progressBar = (ProgressBar)convertView.findViewById(R.id.grid_progressbar);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

//        final ProgressBar progressBar = viewHolder.progressBar;
        String imgURL = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(prefix + imgURL, viewHolder.img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(viewHolder.progressBar != null){
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(viewHolder.progressBar != null){
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                System.out.println("img load completed");
                if(viewHolder.progressBar != null){
                    viewHolder.progressBar.setVisibility(View.GONE);
                    System.out.println("progressBar GONE!");
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(viewHolder.progressBar != null){
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

        });
        return convertView;
    }
}
