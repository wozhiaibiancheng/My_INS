package au.edu.unimelb.student.group55.my_ins.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ClientBlueTooth implements Runnable{
    final String TAG = "ClientThread";
    BluetoothAdapter adapter;
    BluetoothDevice device;
    Handler handler;

    BluetoothSocket socket;
    OutputStream outputStream;
    InputStream in;



    public ClientBlueTooth(){
        this.adapter = adapter;
        this.device = device;
        this.handler = handler;
    }
    public ClientBlueTooth(BluetoothAdapter adapter, BluetoothDevice device,
                           Handler handler) {
        this.adapter = adapter;
        this.device = device;
        this.handler = handler;
        UUIDMethod();

    }
    public void UUIDMethod(){
        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(ParaValues.UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = tmp;
    }
    @Override
    public void run() {

        Log.e(TAG, "ClientBlueTooth:client thread run()");
        if (adapter.isDiscovering())
            adapter.cancelDiscovery();
        try {
            socket.connect();
            outputStream = socket.getOutputStream();
            in = socket.getInputStream();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "-----------do client read run()");
                    byte[] buffer = new byte[1024];
                    int len;
                    String content;
                    try {
                        while ((len=in.read(buffer)) >=0) {
                            content=new String(buffer, 0, len);
                            Message message = new Message();
                            message.what = ParaValues.Message_Client_New;
                            message.obj = content;
                            handler.sendMessage(message);
//                            Log.e(TAG, "------------- client read data in while ,send msg ui" + content);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
           Log.e(TAG, "IOEexception");
        }
    }


    public void write(String data){
//        data = data+"\r\n";
        try {
            outputStream.write(data.getBytes("utf-8"));
//            Log.e(TAG, "---------- write data ok "+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        }
