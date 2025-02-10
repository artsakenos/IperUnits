/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.web.superserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public abstract class ServerHelper implements Runnable {

    private int serverPort = 8080;
    private ServerSocket serverSocket = null;
    private boolean stopped = false;
    private Thread runningThread = null;
    private ArrayList<ClientHelper> clients = new ArrayList<ClientHelper>();

    public ServerHelper(int port) {
        this.serverPort = port;
        runningThread = new Thread(this);
        runningThread.start();
    }

    public abstract void onStarted(int port);

    public abstract void onStopped(int port);

    public abstract void onError(String error, Exception e);

    public abstract void onConnected(Socket client);

    public abstract void onClientOpen(ClientHelper client);

    public abstract void onClientClose(ClientHelper client);

    public abstract void onClientReceived(ClientHelper client, String line);

    public abstract void onClientError(String tag, Exception e);

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            onStarted(this.serverPort);
        } catch (IOException e) {
            onError("ERROR OPENING SERVER. Port:" + this.serverPort + "; ", e);
        }
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                onConnected(clientSocket);
            } catch (IOException e) {
                onError("ERROR ACCEPTING CONNECTION. Port:" + this.serverPort + "; ", e);
                if (isStopped()) {
                    onStopped(this.serverPort);
                    return;
                }
            }
            ClientHelper clientInServerHelper = new ClientHelper(clientSocket) {
                @Override
                public void onReceived(String line) {
                    onClientReceived(this, line);
                }

                @Override
                public void onError(String tag, Exception e) {
                    onClientError(tag, e);
                }

                @Override
                public void onOpen() {
                    onClientOpen(this);
                }

                @Override
                public void onClose() {
                    onClientClose(this);
                    clients.remove(this);
                }
            };
            Thread thread = new Thread(clientInServerHelper);
            clients.add(clientInServerHelper);
            thread.start();
        }
        onStopped(this.serverPort);
    }

    public synchronized boolean isStopped() {
        return this.stopped;
    }

    public synchronized void close() {
        this.stopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            onError("ERROR CLOSING SERVER", e);
        }
    }

    public void sendLine(Socket client, String line) {
        try {
            OutputStream outputStream = client.getOutputStream();
            outputStream.write((line + "\r\n").getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            onError("ERROR SENDING DATA (" + line + ")", ex);
        } catch (NullPointerException ex) {
            onError("ERROR SENDING DATA (" + line + ")", ex);
        }
    }

    @Override
    public String toString() {
        return serverSocket.toString() + " (" + clients.size() + " connected)";
    }
}