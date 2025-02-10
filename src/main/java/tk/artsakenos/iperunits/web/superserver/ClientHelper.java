/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.web.superserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 *
 */
public abstract class ClientHelper implements Runnable {

    private Socket clientSocket = null;
    private InputStream input;
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
            output.write((line).getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            onError("ERROR SENDING DATA", ex);
        }
    }

    @Override
    public void run() {
        try {
            input = clientSocket.getInputStream();
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