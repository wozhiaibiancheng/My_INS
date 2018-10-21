package au.edu.unimelb.student.group55.my_ins.BlueTooth.service;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.BluetoothUtil;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.Comment;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.DialogUtil;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.ToastUtil;


public class SendSocketService {

    //send text
    public static void sendMessage(final String message, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream os = null;
                try {
                    if (Comment.bluetoothSocket == null || !Comment.bluetoothSocket.isConnected()) {
                        Comment.bluetoothSocket = BluetoothUtil.connectDevice(handler);
                    }
                    os = Comment.bluetoothSocket.getOutputStream();
                    os.write(message.getBytes());
                    os.flush();
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * send file
     */
    public static void sendMessageByFile(Context context, Uri filePath, final Handler handler) {
        if (Comment.bluetoothSocket == null) {
            ToastUtil.showShort(context, "Fail bluetooth...");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Comment.bluetoothSocket == null || !Comment.bluetoothSocket.isConnected()) {
                    Comment.bluetoothSocket = BluetoothUtil.connectDevice(handler);
                }
            }
        }).start();
        Dialog dialog = new DialogUtil.DefineDialog(context, 100, 0, filePath);
        dialog.show();
        try {
            File file = new File(filePath.toString());
            if (!file.exists()) return;

            FileInputStream fis = new FileInputStream(file);

            byte[] b = new byte[1024];
            int length;
            int fileSize = 0;

            OutputStream outputStream = Comment.bluetoothSocket.getOutputStream();
            while ((length = fis.read(b)) != -1) {
                fileSize += length;
                listenr.setProgress((int) (fileSize / file.length() * 100));

                outputStream.write(b, 0, length);
            }

            fis.close();

            outputStream.flush();
            handler.sendEmptyMessage(0);
        } catch (IOException e) {
            handler.sendEmptyMessage(1);
            e.printStackTrace();
        }
    }

    static setProgessIml listenr;

    public interface setProgessIml {
        void setProgress(int size);
    }

    public void setProgressListener(setProgessIml listenr) {
        this.listenr = listenr;
    }


}
