/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.web.superserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public abstract class ClientHelper implements Runnable {

    private final Socket clientSocket;
    private OutputStream output;

    public abstract void onError(String tag, Exception e);

    public abstract void onReceived(String line);

    public abstract void onOpen();

    public abstract void onClose();

    public ClientHelper(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Socket getClient() {
        return clientSocket;
    }

    public void send(String line) {
        if (output == null) {
            return;
        }
        try {
            output.write((line).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            onError("ERROR SENDING DATA", ex);
        }
    }

    @Override
    public void run() {
        try {
            InputStream input = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            onOpen();

            while ((line = reader.readLine()) != null) {
                onReceived(line);
            }
            reader.close();
            output.close();
            input.close();
            onClose();
        } catch (IOException e) {
            onError("ERROR CLOSING CLIENT", e);
        }
    }

    public void close() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                onError("CLIENT CLOSING ERROR", ex);
            }
        }
    }

    @Override
    public String toString() {
        return clientSocket.toString();
    }
}