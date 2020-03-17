package centralesupelec.engine.preferences;

public class Value {

    private String value;

    public Value(String a) {

        value = a;
    }

    public String get_value(){
        return this.value;
    }

    public Integer get_score(Boolean use_real){
        Integer score = null;
        if(use_real) {
            switch (value) {
                case "Very Good":
                    score = 4;
                    break;
                case "Good":
                    score = 3;
                    break;
                case "Bad":
                    score = 2;
                    break;
                case "Very Bad":
                    score = 1;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }
        else{
            score = Integer.parseInt(value.split("_")[1]);
        }
        return score;
    }

}
