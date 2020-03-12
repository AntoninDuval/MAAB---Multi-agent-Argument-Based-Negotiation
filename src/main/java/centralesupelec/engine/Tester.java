package centralesupelec.engine;

import centralesupelec.engine.preferences.*;
import jade.wrapper.ControllerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tester {

    public static void main(String[] args) throws ControllerException {

        CriterionName PRODUCTION_COST = new CriterionName("PRODUCTION_COST");
        CriterionName ENVIRONMENT_IMPACT = new CriterionName("ENVIRONMENT_IMPACT");
        CriterionName CONSUMPTION = new CriterionName("CONSUMPTION");
        CriterionName DURABILITY = new CriterionName("DURABILITY");

        Value VERY_GOOD = new Value("Very Good");
        Value GOOD = new Value("Good");
        Value BAD = new Value("Bad");
        Value VERY_BAD = new Value("Very Bad");

        Preferences agent_pref = new Preferences();

        agent_pref.set_criterion_name_list(new ArrayList<>(Arrays.asList(PRODUCTION_COST,ENVIRONMENT_IMPACT,CONSUMPTION,DURABILITY)));
        System.out.println(agent_pref.get_criterion_name_list());

        Item diesel_engine = new Item("Diesel Engine", "A super cool engine");

        agent_pref.add_criterion_value(new CriterionValue(diesel_engine,PRODUCTION_COST,VERY_GOOD));
        agent_pref.add_criterion_value(new CriterionValue(diesel_engine,ENVIRONMENT_IMPACT,VERY_BAD));
        agent_pref.add_criterion_value(new CriterionValue(diesel_engine,CONSUMPTION,GOOD));
        agent_pref.add_criterion_value(new CriterionValue(diesel_engine,DURABILITY,BAD));


        System.out.println("Index of Production cost : " + agent_pref.get_criterion_name_list().indexOf(PRODUCTION_COST));
        System.out.println("Index of DURABILITY cost : " + agent_pref.get_criterion_name_list().indexOf(DURABILITY));

        Item electric_engine = new Item("Electric Engine", "A super cool electric engine");

        agent_pref.add_criterion_value(new CriterionValue(electric_engine,PRODUCTION_COST,BAD));
        agent_pref.add_criterion_value(new CriterionValue(electric_engine,DURABILITY,VERY_BAD));
        agent_pref.add_criterion_value(new CriterionValue(electric_engine,ENVIRONMENT_IMPACT,GOOD));
        agent_pref.add_criterion_value(new CriterionValue(electric_engine,CONSUMPTION,BAD));

        System.out.println(electric_engine.get_value(agent_pref, PRODUCTION_COST).get_value());

        System.out.println("Which is the best between " + electric_engine.get_name() + " and " +diesel_engine.get_name());
        System.out.println("Score for electric engine : " + agent_pref.get_score(electric_engine));
        System.out.println("Score for diesel engine : " + agent_pref.get_score(diesel_engine));

        System.out.println(agent_pref.is_prefered_item(electric_engine,diesel_engine));
        System.out.println(agent_pref.most_prefered_item(Arrays.asList(electric_engine,diesel_engine)).get_name());


    }
}
