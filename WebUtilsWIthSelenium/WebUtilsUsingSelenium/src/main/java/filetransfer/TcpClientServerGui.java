package filetransfer;

import javax.swing.*;

public class TcpClientServerGui {

    public static Boolean started = false;

    TcpClientServerGui() {
        JFrame f = new JFrame("Tcp sync example");

        //submit button
        JButton startServerButton = new JButton("Start server on port 4444");
        startServerButton.setBounds(100, 100, 240, 40);
        //enter name serverLabel
        JLabel serverLabel = new JLabel();
        serverLabel.setText("Folder to save:");
        serverLabel.setBounds(10, 10, 200, 100);
        //serverFolder to enter folder
        JTextField serverFolder = new JTextField();
        serverFolder.setBounds(110, 50, 230, 30);
        serverFolder.setText(System.getProperty("user.home") + "/file-transfer/");

        //start client
        JButton startClientButton = new JButton("Start client (send data)");
        startClientButton.setBounds(100, 210, 240, 40);
        //serverFolder to enter folder
        JTextField serverIp = new JTextField();
        serverIp.setBounds(110, 180, 130, 30);
        serverIp.setText("34.76.112.159");
        //enter name serverLabel
        JLabel clientLabel = new JLabel();
        clientLabel.setText("Server Ip:");
        clientLabel.setBounds(10, 150, 200, 100);


        //empty serverLabel which will show event after button clicked
        JLabel label1 = new JLabel();
        label1.setBounds(10, 240, 300, 100);

        JTextArea statusArea = new JTextArea("status:");
        statusArea.setBounds(10, 250, 500, 350);

        //add to frame
        f.add(label1);
        f.add(serverFolder);
        f.add(serverLabel);
        f.add(startServerButton);
        f.add(clientLabel);
        f.add(startClientButton);
        f.add(serverIp);
        f.add(statusArea);
        f.setSize(600, 500);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //action listener
        startServerButton.addActionListener(event -> {
            if (!started) {
                label1.setText("Server started on 4444 port :" + serverFolder.getText());
                FileServer fs = new FileServer(4444, serverFolder.getText());
                fs.start();
                //started = true;
            }
        });

        //action listener
        startClientButton.addActionListener(event -> {
            if (!started) {
                label1.setText("Client start sending data to 4444 port");
                FileClient fc = new FileClient(serverIp.getText(), 4444,
                        System.getProperty("user.home") + "/file-transfer/");
                // started = true;
            }
        });
    }


    public static void main(String[] args) {
        System.out.println(System.getProperty("user.home") + "/file-transfer/");
        new TcpClientServerGui();
    }
}