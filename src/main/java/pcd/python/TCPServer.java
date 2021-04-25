package pcd.python;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
//import pcd.data.ImageDataStorage;
import pcd.utils.Constant;

final class TCPServer {
    private static final Logger LOGGER = LogManager.getLogger(TCPServer.class);
    private DataOutputStream dout;
    private BufferedReader in;
    private final ProcessBuilder pb;
    private Process p;

    TCPServer(int port, ProcessBuilder pb) {
        this.pb = pb;
        connect(port);
    }

    public void stop() {
        if (p != null) {
            p.destroy();
        }
    }

    synchronized public void connect(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            //serverSocket.setSoTimeout(1000 * 60 * 2);

            if (pb != null) {
                p = pb.start();
            }

            Socket soc = serverSocket.accept();
            soc.setReceiveBufferSize(8192 * 2);

            dout = new DataOutputStream(soc.getOutputStream());
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

            dout.writeUTF("c");

        } catch (IOException e) {
            LOGGER.error("", e);
            JOptionPane.showMessageDialog(null, "Another instance of the software is already running! You can only have one open.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    public void send(String t) throws IOException {
        if (t == null) {
            return;
        }

        try {
            if (Constant.DEBUG_MSG) {
                System.out.println("Sending:\n" + t);
            }
            dout.writeUTF(t);
        } catch (IOException e) {
            LOGGER.error("", e);
            throw e;
        }
    }

    public String receive() throws IOException {
        String msg;

        try {
            msg = in.readLine();
        } catch (IOException e) {
           LOGGER.error("Imput steam cannot read line!", e);
            throw e;
        }

        if (Constant.DEBUG_MSG) {
            System.out.println("Received:\n" + msg);
        }

        return msg;
    }

    public DataOutputStream getDout() {
        return dout;
    }

    public BufferedReader getIn() {
        return in;
    }

}
