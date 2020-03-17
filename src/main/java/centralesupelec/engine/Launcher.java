package centralesupelec.engine;


import centralesupelec.engine.agents.*;
import centralesupelec.engine.preferences.*;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class implements the main class of the practical work 3 project: <code>Engine</code>.
 *
 * @author E. Hermellin
 * @version 1.0
 */
public class Launcher implements Serializable {

    /**
     * The serial id of the class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The main method.
     * @param args the arguments of the program.
     */
    public static void main(String[] args) throws ControllerException {
        Runtime rt = Runtime.instance();
        rt.setCloseVM(true);
        Profile pMain = new ProfileImpl("localhost", 5666, null);
        AgentContainer mc = rt.createMainContainer(pMain);

        Boolean use_real = Boolean.TRUE; // CHANGE THIS VALUE TO USE REAL PREFERENCES OR FAKE ONE FOR TESTING

        ArrayList<Item> list_inital_items = new ArrayList<>();
        if (use_real){

            list_inital_items.add(new Item("Diesel Engine", "A super cool engine"));
            list_inital_items.add(new Item("Electric Engine", "A super cool electric engine"));
            list_inital_items.add(new Item("Hybrid Engine", "A super cool hybrid engine"));
            list_inital_items.add(new Item("Fuel Engine", "A super cool fuel engine"));
            list_inital_items.add(new Item("Secret Engine", "An engine no ones know what it does"));
        }
        else{
            list_inital_items = Item.generate_random_items(50);
        }

        int nb_crit = 15;
        int nb_value = 20; // Use pair number



        AgentController ac = mc.createNewAgent("MANAGER", ManagerAgent.class.getName(), new Object[]{list_inital_items});
        ac.start();
        // Set the initals state for the two new engineer
        String[] state = {"WAIT","PROPOSE"};
        for (int i = 0; i <= 1; i++) {
                AgentController ace = mc.createNewAgent("ENGINEER" + i,
                        Engineer.class.getName(),
                        new Object[]{state[i],
                                use_real,
                                nb_crit,
                                nb_value});
                ace.start();
            }
        }
}
