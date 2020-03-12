package centralesupelec.engine.arguments;

import centralesupelec.engine.preferences.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Arguments {

    private Item item;
    private Boolean type;
    private ArrayList<CriterionValue> argument_score = new ArrayList<>();
    private Preferences agent_pref;
    private Integer threshold;

    public Arguments(Item item, Boolean type, Preferences agent_pref){

        this.item = item;
        this.type = type;
        this.agent_pref = agent_pref;
        this.threshold = 2;

        generate_arguments();

    }

    public void generate_arguments(){
        /** Class to generate arguments for all items.
         *
         */
        if (type){ // If we want to give argument in favor of this item, get criterion above threshold
            for(CriterionValue criterionValue:agent_pref.get_criterion_values_list()){
                if(criterionValue.get_value().get_score()>threshold & criterionValue.get_item().equals(item)){
                    //System.out.println(criterionValue.get_criterion_name().getName() + " " + criterionValue.get_value().get_value() + " " + criterionValue.get_value().get_score());
                    argument_score.add(criterionValue);
                }
            }
        }
        else{ // If we want to give argument in defavor of this item, get criterion under threshold
            for(CriterionValue criterionValue:agent_pref.get_criterion_values_list()){
                if(criterionValue.get_value().get_score()<=threshold & criterionValue.get_item().equals(item)){
                    argument_score.add(criterionValue);
                }
            }
        }
    }

    public void print_argument_item(){
        for(CriterionValue criterionValue:argument_score){
            System.out.println("Argument we can use for "+item.get_name()+" : "+criterionValue.get_criterion_name().getName()+" : "+agent_pref.get_criterion_rank(criterionValue)+" "+ criterionValue.get_value().get_value());
        }
    }

    public CriterionValue find_argument(CriterionName crit) {
        /**
         * Method to find an argument to counter the previous argument proposed
         */

        CriterionValue crit_argument = null;
        for (CriterionValue best_crit : argument_score) {
            if (agent_pref.is_prefered_criterion(best_crit.get_criterion_name(), crit)) {
                // If one of our criterion is better
                argument_score.remove(best_crit); // We can no longer use this argument
                return best_crit;
            } else {
                return null;
            }
        }
        return crit_argument;
    }

    public CriterionValue get_best_argument(){
        return argument_score.get(0);
    }

    public ArrayList<CriterionValue> getArgument_score(){
        return argument_score;
    }

}
