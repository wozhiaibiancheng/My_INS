package au.edu.unimelb.student.group55.my_ins.BlueTooth.entity;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DatasEntity implements Serializable {


    public static List<BluetoothDevice> mPairedDevices = new ArrayList<>();

    public static List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();


}
