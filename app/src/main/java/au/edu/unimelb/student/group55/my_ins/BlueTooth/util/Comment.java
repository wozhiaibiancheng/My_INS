package au.edu.unimelb.student.group55.my_ins.BlueTooth.util;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.UUID;

public class Comment {


    public static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public static BluetoothDevice bluetoothDevice;


    public static BluetoothSocket bluetoothSocket;


    public static final int SWITCH = 101;


    public static final int FOUND = 102;


    public static final int FINISHED = 103;


    public static final int BOND =104;

    public static final int CONNECT =105;


    public static final int IMAGE_CODE = 0;


    public static final int NAME_CODE = 1;

}
