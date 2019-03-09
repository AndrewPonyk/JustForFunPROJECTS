package filetransfer;


import com.google.common.primitives.Longs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileClient {

    private Socket s;

    public static int BUFFER = 100000;

    public FileClient(String host, int port, String file) {
        try {
            System.out.println("Hello".getBytes().length);
            s = new Socket(host, port);
            //sendFile(file);
            sendAllFilesFromDirectory("/home/andrii/Downloads/file-transfer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String file) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];

        while (fis.read(buffer) > 0) {
            dos.write(buffer);
        }

        fis.close();
        dos.close();
    }

    public void sendAllFilesFromDirectory(String dir) throws IOException {

        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        File[] files = new File(dir).listFiles();
        byte data[] = new byte[BUFFER];

        FileInputStream fileInput;
        out.writeInt(files.length);


        //test
//        out.write("Hello".getBytes());
//        out.writeLong(9999);
//        out.writeLong(1);
        //endtest
        for (int i =0;i<files.length;i++){
            System.out.println("Sending"+files[i].getAbsolutePath());
            fileInput = new FileInputStream(files[i]);
            out.writeUTF(files[i].getName());
            out.writeLong(files[i].length());

            int count;
            while((count = fileInput.read(data, 0, BUFFER)) != -1) {

                out.write(data, 0, count);

             //   bytesSent += count;

            }
            fileInput.close();
        }
        out.flush();
    }

    public static void main(String[] args) {
        FileClient fc = new FileClient("34.76.112.159", 4444, "/home/andrii/file-transfer.txt");

    }

}