package centralesupelec.engine.arguments;

import centralesupelec.engine.preferences.CriterionName;
import javafx.util.Pair;

public class Comparison {

    private Pair<CriterionName,CriterionName> comparison;
    private CriterionName crit1;
    private CriterionName crit2;

    public Comparison(CriterionName crit1, CriterionName crit2){

        this.comparison = new Pair<>(crit1,crit2);
    }

    public Pair<CriterionName,CriterionName> get_comparison(){
        return this.comparison;
    }


}
