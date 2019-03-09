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
        int fileCount = in.readInt();
        int bytesRecieved = 0;
        for(int i=0; i<fileCount; i++) {

            byte data[] = new byte[BUFFER];
            String fileName = in.readUTF();
            bytesRecieved += fileName.getBytes().length;
            fileOut = new FileOutputStream(new File("/home/pihura_olia/Downloads/" +fileName));
            long fileLength = in.readLong();
            bytesRecieved+= 8;
            int fileReceived = 0;
            for(int j=0; j<fileLength / BUFFER; j++) {


                //while(totalCount < BUFFER) {
                    int count = in.read(data, BUFFER * j, BUFFER);
                    fileReceived += count;
                //}

                fileOut.write(data, BUFFER*j, BUFFER);


                bytesRecieved += count;
            }
            fileOut.flush();
            // read the remaining bytes
            int count = in.read(data, bytesRecieved+(int) (fileLength/BUFFER*BUFFER), (int)(fileLength%BUFFER));

            fileOut.write(data,fileReceived+ (int) (fileLength/BUFFER*BUFFER), (int)(fileLength%BUFFER));
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