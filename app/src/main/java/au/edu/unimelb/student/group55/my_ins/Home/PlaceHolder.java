package au.edu.unimelb.student.group55.my_ins.Home;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.edu.unimelb.student.group55.my_ins.R;

public class PlaceHolder extends Fragment {
    private static final String TAG = "PlaceHolder Activity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment, container, false);

        return view;
    }
}
