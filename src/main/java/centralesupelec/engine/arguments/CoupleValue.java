package centralesupelec.engine.arguments;

import centralesupelec.engine.preferences.CriterionName;
import centralesupelec.engine.preferences.CriterionValue;
import centralesupelec.engine.preferences.Value;
import javafx.util.Pair;

import java.util.AbstractMap;
import java.util.Map;

public class CoupleValue {

    private Pair<CriterionName, Value> couplevalue;

    public CoupleValue(CriterionValue criterionValue){
        this.couplevalue = new Pair<>(criterionValue.get_criterion_name(),criterionValue.get_value());

    }

    public Pair<CriterionName, Value> get_couplevalue(){
        return this.couplevalue;
    }
}
