import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class client extends Frame implements Runnable, ActionListener {
    TextField textField;
    TextArea textArea;
    Button send;

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Thread chat;

    client() {
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

        // Networking setup
        try {
            socket = new Socket("localhost", 12000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Thread start
        chat = new Thread(this);
        chat.start();

        setTitle("James");
        setSize(500, 500);
        setVisible(true);

        // Window close handling
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (socket != null) socket.close();
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
            textArea.append("James: " + msg + "\n");
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
                textArea.append("Sabari: " + msg + "\n");
            } catch (IOException e) {
                textArea.append("Connection closed.\n");
                break;
            }
        }
    }

    public static void main(String[] args) {
        new client();
    }
}
