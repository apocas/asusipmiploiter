/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asusipmiploiter;

import ch.ethz.ssh2.InteractiveCallback;
import java.io.IOException;

/**
 *
 * @author pedrodias petermdias@gmail.com
 *
 */
class InteractiveLogic implements InteractiveCallback {

    private String user = "";

    public InteractiveLogic(String user) {
        this.user = user;
    }

    public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt,
            boolean[] echo) throws IOException {
        String[] result = new String[numPrompts];

        for (int i = 0; i < numPrompts; i++) {
            result[i] = this.user;
        }

        return result;
    }
}
