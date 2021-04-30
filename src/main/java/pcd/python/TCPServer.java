package pcd.python;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The TCP server used to connect to Python via socket communication.
 * @author Tibor Sloboda
 */
final class TCPServer {
    private static final Logger LOGGER = LogManager.getLogger(TCPServer.class);
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle");
    private DataOutputStream dout;
    private BufferedReader in;
    private final ProcessBuilder pb;
    private Process p;
    private boolean connected = false;

    TCPServer(int port, ProcessBuilder pb) {
        this.pb = pb;
        connect(port);
    }

    /**
     * Stops the python process
     */
    public void stop() {
        if (p != null) {
            p.destroy();
        }
    }

    /**
     * Opens TCP server connection and listens for reply, then validates connection by replying.
     * @param port the port used for connecting
     */
    public void connect(int port) {
        try {
            Runnable checkAlive = () -> {
                while(p.isAlive() && !connected){} //Waiting
                
                if(!p.isAlive()){
                    JOptionPane.showMessageDialog(null, bundle.getString("TCPServer.pythonFail"));
                    System.exit(-1);
                }
            };
                    
            ServerSocket serverSocket = new ServerSocket(port);

            if (pb != null) {
                p = pb.start();
            }

            Thread checkAliveThread = new Thread(checkAlive);
            checkAliveThread.setDaemon(true);
            checkAliveThread.start();
            Socket soc = serverSocket.accept();
            soc.setReceiveBufferSize(8192 * 2);
            connected = true;

            dout = new DataOutputStream(soc.getOutputStream());
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

            dout.writeUTF("c");

        } catch (IOException e) {
            LOGGER.error("", e);
            JOptionPane.showMessageDialog(null, "Another instance of the software is already running! You can only have one open.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    /**
     * Sends a string to Python.
     * @param t the {@link String} to send
     * @throws IOException occurs if a socket exception happens
     */
    public void send(String t) throws IOException {
        if (t == null) {
            return;
        }

        try {
            LOGGER.info("Sending:\n" + t);
            dout.writeUTF(t);
        } catch (IOException e) {
            LOGGER.error("Socket exception occured", e);
            throw e;
        }
    }

    public String receive() throws IOException {
        String msg;

        try {
            msg = in.readLine();
        } catch (IOException e) {
           LOGGER.error("Socket input stream cannot read line! Maybe you forgot to send newline character on the end?", e);
            throw e;
        }

        LOGGER.info("Received:\n" + msg);

        return msg;
    }

    public DataOutputStream getDout() {
        return dout;
    }

    public BufferedReader getIn() {
        return in;
    }

}
