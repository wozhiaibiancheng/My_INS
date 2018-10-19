package au.edu.unimelb.student.group55.my_ins.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ServerBlueTooth  implements Runnable{
    final String TAG = "ServerThread";

    BluetoothAdapter bluetoothAdapter;
    BluetoothServerSocket serverSocket =null;
    BluetoothSocket socket = null;
    Handler uiHandler;
    Handler writeHandler;

    OutputStream out;
    InputStream in;
    BufferedReader reader;

    boolean acceptFlag = true;

    public ServerBlueTooth(BluetoothAdapter bluetoothAdapter, Handler handler) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.uiHandler = handler;
        BluetoothServerSocket tmp = null;
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(ParaValues.NAME, UUID.fromString(ParaValues.UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverSocket = tmp;
//        Log.e(TAG, "do new()");
    }

    @Override
    public void run() {
//        Log.e(TAG, " do run()");
        try {
            while (acceptFlag) {
                socket = serverSocket.accept();

                if (socket != null) {
//                    Log.e(TAG, "socket not null, get a client");

                    out = socket.getOutputStream();
                    in = socket.getInputStream();


                    BluetoothDevice remoteDevice = socket.getRemoteDevice();
                    Message message = new Message();
                    message.what = ParaValues.MSG_REV_A_CLIENT;
                    message.obj = remoteDevice;
                    uiHandler.sendMessage(message);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "do server read run()");

                            byte[] buffer = new byte[1024];
                            int len;
                            String content;
                            try {
                                while ((len = in.read(buffer)) != -1) {
                                    content = new String(buffer, 0, len);
                                    Message message = new Message();
                                    message.what = ParaValues.Message_Client_New;
                                    message.obj = content;
                                    uiHandler.sendMessage(message);
                                    Log.e(TAG, "server read data in while ,send msg ui" + content);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void write(String data){
        try {
            out.write(data.getBytes("utf-8"));
            Log.e(TAG, "Server write: write data "+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            acceptFlag = false;
            serverSocket.close();
//            Log.e(TAG, "-------------- do cancel ,flag is "+acceptFlag);

        } catch (IOException e) {
            e.printStackTrace();
//            Log.e(TAG, "----------------- cancel " + TAG + " error");
        }
    }

}
