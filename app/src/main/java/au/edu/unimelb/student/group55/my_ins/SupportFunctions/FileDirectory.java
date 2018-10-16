package au.edu.unimelb.student.group55.my_ins.SupportFunctions;

import android.os.Environment;

public class FileDirectory {

    public String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIRECTORY + "/Pictures";
    public String CAMERA = ROOT_DIRECTORY + "/Camera";
}
