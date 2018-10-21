package au.edu.unimelb.student.group55.my_ins.BlueTooth.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import au.edu.unimelb.student.group55.my_ins.BlueTooth.entity.DatasEntity;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.Comment;


public class BTBroadcastReceiver extends BroadcastReceiver {
    private BluetoothDevice device;
    private Handler mHandler;
    private Message message;


    public BTBroadcastReceiver(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                message=new Message();
                message.what = Comment.SWITCH;
                message.obj=blueState;
                mHandler.sendMessage(message);
                break;
            case BluetoothDevice.ACTION_FOUND:
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && !DatasEntity.mBluetoothDevices.contains(device)) {
                    message=new Message();
                    message.what = Comment.FOUND;
                    message.obj=device;
                    mHandler.sendMessage(message);
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    mHandler.sendEmptyMessage(Comment.FINISHED);
                break;

            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                message=new Message();
                message.what = Comment.BOND;
                message.obj=device.getBondState();
                mHandler.sendMessage(message);
                break;
        }

    }

}
