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

        for(int i=0; i<fileCount; i++) {
            int bytesRecieved = 0;
            byte data[] = new byte[BUFFER];
            String fileName = in.readUTF();
            fileOut = new FileOutputStream(new File("/home/pihura_olia/Downloads/" +fileName));
            long fileLength = in.readLong();
            for(int j=0; j<fileLength / BUFFER; j++) {
                int totalCount = 0;

                while(totalCount < BUFFER) {
                    int count = in.read(data, totalCount, BUFFER - totalCount);
                    totalCount += count;
                }

                fileOut.write(data, 0, totalCount);
                fileOut.flush();

                bytesRecieved += totalCount;
            }
            // read the remaining bytes
            int count = in.read(data, 0, (int) (fileLength % BUFFER));
            bytesRecieved+=count;
            fileOut.write(data, 0, count);
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