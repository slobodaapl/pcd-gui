package pcd.python;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import pcd.data.ImageDataStorage;

final class TCPServer {

    private DataOutputStream dout;
    private BufferedReader in;
    private ServerSocket serverSocket;
    private Socket soc;
    private final ProcessBuilder pb;
    private Process p;

    TCPServer(int port, ProcessBuilder pb) {
        this.pb = pb;
        connect(port);
    }

    TCPServer(ProcessBuilder pb) {
        this.pb = pb;
        connect(61387);
    }

    public void stop() {
        p.destroy();
    }

    synchronized public void connect(int port) {
        try {
            serverSocket = new ServerSocket(port);
            // serverSocket.setSoTimeout(1000 * 60 * 2);
            p = pb.start();
            soc = serverSocket.accept();
            soc.setReceiveBufferSize(12 * 300);

            dout = new DataOutputStream(soc.getOutputStream());
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

            dout.writeUTF("c");

            int reply = in.read();

        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("", e);
            JOptionPane.showMessageDialog(null, "Another instance of the software is already running! You can only have one open.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    public void send(String t) throws IOException {
        if (t == null) {
            return;
        }

        try {
            dout.writeUTF(t);
        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("", e);
            throw e;
        }
    }

    public byte[] toBytes(Object t) throws IOException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("Output cannot be created!", e);
            throw e;
        }
    }

    public String receive() throws IOException {
        String msg = "";

        try {
            msg = in.readLine();
        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("Imput steam cannot read line!", e);
            throw e;
        }

        return msg.substring(5);
    }

    public void closeConnection() throws IOException {
        try {
            dout.flush();
            dout.close();
            soc.close();
        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("Cannot close connetion!", e);
            throw e;
        }
    }

    public DataOutputStream getDout() {
        return dout;
    }

    public BufferedReader getIn() {
        return in;
    }

}
