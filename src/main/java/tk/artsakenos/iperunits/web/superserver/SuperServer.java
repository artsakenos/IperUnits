/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.web.superserver;

import java.net.Socket;

/**
 *
 * @author Andrea
 */
public abstract class SuperServer {

    //--------------------------------------------------------------------------
    public abstract void onSSStart();

    public abstract void onSSStop();

    public abstract void onSSServerStarted(ServerHelper server, int port);

    public abstract void onSSServerStopped(ServerHelper server, int port);

    public abstract void onSSServerConnected(ServerHelper server, Socket client);

    public abstract void onSSServerError(ServerHelper server, String tag, Exception e);

    public abstract void onSSServerClientOpen(ServerHelper server, ClientHelper client);

    public abstract void onSSServerClientClose(ServerHelper server, ClientHelper client);

    public abstract void onSSServerClientReceived(ServerHelper server, ClientHelper client, String line);

    public abstract void onSSServerClientError(ServerHelper server, String tag, Exception e);

    //--------------------------------------------------------------------------
    public void start(int port) {
        ServerHelper server = new ServerHelper(port) {
            @Override
            public void onStarted(int port) {
                onSSServerStarted(this, port);
            }

            @Override
            public void onStopped(int port) {
                onSSServerStopped(this, port);
            }

            @Override
            public void onError(String tag, Exception e) {
                onSSServerError(this, tag, e);
            }

            @Override
            public void onConnected(Socket client) {
                onSSServerConnected(this, client);
            }

            @Override
            public void onClientOpen(ClientHelper client) {
                onSSServerClientOpen(this, client);
            }

            @Override
            public void onClientClose(ClientHelper client) {
                onSSServerClientClose(this, client);
            }

            @Override
            public void onClientReceived(ClientHelper client, String line) {
                onSSServerClientReceived(this, client, line);
            }

            @Override
            public void onClientError(String tag, Exception e) {
                onSSServerClientError(this, tag, e);
            }
        };
    }
    //--------------------------------------------------------------------------
}
