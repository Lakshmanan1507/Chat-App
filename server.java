import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class server extends Frame implements Runnable, ActionListener {
    TextField textField;
    TextArea textArea;
    Button send;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Thread chat;

    server() {
        textField = new TextField();
        textArea = new TextArea();
        send = new Button("Send");

        textArea.setEditable(false);
        send.addActionListener(this);

        // Layout setup
        setLayout(new BorderLayout());
        add(textArea, BorderLayout.CENTER);

        Panel bottomPanel = new Panel(new BorderLayout());
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(send, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Server setup
        try {
            serverSocket = new ServerSocket(12000);
            System.out.println("Sabari started, waiting for James...");
            socket = serverSocket.accept();
            System.out.println("James connected.");
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Thread start
        chat = new Thread(this);
        chat.setDaemon(true);
        chat.start();

        setTitle("Sabari");
        setSize(500, 500);
        setVisible(true);

        // Window close handling
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (socket != null) socket.close();
                    if (serverSocket != null) serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = textField.getText().trim();
        if (!msg.isEmpty()) {
            textArea.append("Sabari: " + msg + "\n");
            textField.setText("");

            try {
                dataOutputStream.writeUTF(msg);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = dataInputStream.readUTF();
                textArea.append("James: " + msg + "\n");
            } catch (IOException e) {
                textArea.append("Connection closed.\n");
                break;
            }
        }
    }

    public static void main(String[] args) {
        new server();
    }
}
