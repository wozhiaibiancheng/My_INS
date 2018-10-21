package au.edu.unimelb.student.group55.my_ins.BlueTooth.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import au.edu.unimelb.student.group55.my_ins.BlueTooth.entity.DatasEntity;


public class BluetoothUtil {


    public static BluetoothAdapter mBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }



    public static IntentFilter makeFilters() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        return intentFilter;
    }


    public static boolean isOpenBluetooth() {
        if (BluetoothUtil.mBluetoothAdapter().isEnabled()) {
            startGetBound();
            startSerch();
            return true;
        } else {
            return false;
        }
    }



    public static void switchBluetooth(Activity activity) {
        if (!mBluetoothAdapter().isEnabled()) {
            boolean enable = mBluetoothAdapter().enable();
            if (!enable) {
                activity.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        } else {
            mBluetoothAdapter().disable();
        }
    }


    public static void startSerch() {

        if (mBluetoothAdapter().isDiscovering()) {
            mBluetoothAdapter().cancelDiscovery();
        }
        DatasEntity.mBluetoothDevices.clear();
        mBluetoothAdapter().startDiscovery();
    }


    public static void startGetBound() {
        DatasEntity.mPairedDevices.clear();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter().getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DatasEntity.mPairedDevices.add(device);
            }
        }
    }


    public static void connectBound() {
        if (Comment.bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Comment.bluetoothDevice.createBond();
                        } else {
                            Method method = BluetoothDevice.class.getMethod("createBond");
                            method.invoke(Comment.bluetoothDevice);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    public static void connectSocket(final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Comment.bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    try {
                        if (Comment.bluetoothSocket != null) {
                            Comment.bluetoothSocket.close();
                            Comment.bluetoothSocket = null;
                        }
                        if (Build.VERSION.SDK_INT >=22) {
                            Comment.bluetoothSocket = Comment.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(Comment.SPP_UUID);
                        } else {
                            Comment.bluetoothSocket = Comment.bluetoothDevice.createRfcommSocketToServiceRecord(Comment.SPP_UUID);
                        }

                        if (!Comment.bluetoothSocket.isConnected()) {
                            Comment.bluetoothSocket.connect();
                            handler.sendEmptyMessage(Comment.CONNECT);
                        }
                    } catch (Exception e) {
                        try {
                            Comment.bluetoothSocket = (BluetoothSocket) Comment.bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(Comment.bluetoothDevice, 1);
                            if (!Comment.bluetoothSocket.isConnected()) {
                                Comment.bluetoothSocket.connect();
                                handler.sendEmptyMessage(Comment.CONNECT);
                            }
                        } catch (Exception e1) {
                            try {
                                Comment.bluetoothSocket.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public static BluetoothSocket connectDevice(final Handler handler) {
        BluetoothSocket socket = null;
        try {
            Comment.SPP_UUID = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");
            socket = Comment.bluetoothDevice.createRfcommSocketToServiceRecord(Comment.SPP_UUID);
            socket.connect();
            handler.sendEmptyMessage(Comment.CONNECT);
        } catch (Exception e) {
            handler.sendEmptyMessage(2);
            try {
                if (socket!=null)
                socket.close();
            } catch (Exception closeException) {

            }
        }
        return socket;
}


    public static void unpairDevice() {
        Method removeBondMethod = null;
        try {
//            removeBondMethod = Comment.bluetoothDevice.getClass().getMethod("removeBond", (Class[]) null);
//            removeBondMethod.invoke(Comment.bluetoothDevice, (Object[]) null);
            removeBondMethod = Comment.bluetoothDevice.getClass().getMethod("removeBond");
            removeBondMethod.invoke(Comment.bluetoothDevice);
            Log.d("aaa", "ok");
        } catch (Exception e) {
            Log.d("aaa", "error");
            e.printStackTrace();
        }
    }
}
