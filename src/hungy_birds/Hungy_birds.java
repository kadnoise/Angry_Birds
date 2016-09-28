/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hungy_birds;

import ab.demo.NaiveAgent;

/**
 *
 * @author RaulFreire
 */
public class Hungy_birds {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        NaiveAgent bot = new NaiveAgent();
        bot.currentLevel = 15;

        bot.run();
    }
}
