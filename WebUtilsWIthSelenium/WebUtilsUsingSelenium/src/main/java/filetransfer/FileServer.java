package filetransfer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static filetransfer.FileClient.BUFFER;

public class FileServer extends Thread {

    private ServerSocket ss;

    public FileServer(int port) {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket clientSock = ss.accept();
                saveFile(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(Socket clientSock) throws IOException {
        DataInputStream in = new DataInputStream(clientSock.getInputStream());
        FileOutputStream fileOut;
        int bytesRecieved = 0;
        int fileCount = in.readInt();
        bytesRecieved+=4;

        // test
//        byte[] helloByte = new byte[100];
//        int shouldBeHello = in.read(helloByte, 0, 5);
//        System.out.println(new String(helloByte));
//        long l = in.readLong();//should be 9999
//        System.out.println(l);
        //endtest



        for(int i=0; i<fileCount; i++) {

            byte data[] = new byte[BUFFER];
            String fileName = in.readUTF();
            bytesRecieved += fileName.getBytes().length;
            fileOut = new FileOutputStream(new File("/home/pihura_olia/" +fileName));
            long fileLength = in.readLong();
            bytesRecieved+= 8;
            int fileReceived = 0;
            for(int j=0; j<fileLength / BUFFER; j++) {


                //while(totalCount < BUFFER) {
                    int count = in.read(data, 0, BUFFER);
                    fileReceived += count;
                //}

                fileOut.write(data, 0, BUFFER);


                bytesRecieved += count;
            }
            fileOut.flush();
            // read the remaining bytes
            int count = in.read(data, 0, (int)(fileLength%BUFFER));

            fileOut.write(data,0, (int)(fileLength%BUFFER));
            bytesRecieved+=count;
            fileOut.flush();
            fileOut.close();

            System.out.println("File " + fileName + " recieved successfully : size:" + bytesRecieved);

        }
    }

    public static void main(String[] args) {
        FileServer fs = new FileServer(4444);
        fs.start();
    }

}