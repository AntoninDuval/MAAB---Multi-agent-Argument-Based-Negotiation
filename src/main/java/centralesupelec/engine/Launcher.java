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

        Item[] list_inital_items = {new Item("Diesel Engine", "A super cool engine"),
                                        new Item("Electric Engine", "A super cool electric engine"),
                new Item("Fuel Engine", "A super cool fuel engine"),
                new Item("Hybrid Engine", "A super cool hybrid engine"),
                new Item("Secret Engine", "An engine no ones know what it does")};

        AgentController ac = mc.createNewAgent("MANAGER", ManagerAgent.class.getName(), new Object[]{list_inital_items});
        ac.start();

        // Set the initals state for the two new engineer
        String[] state = {"WAIT","PROPOSE"};

        for (int i = 0; i <= 1; i++) {
            AgentController ace = mc.createNewAgent("ENGINEER" + i, Engineer.class.getName(), new Object[]{state[i]});
            ace.start();
        }

    }
}
