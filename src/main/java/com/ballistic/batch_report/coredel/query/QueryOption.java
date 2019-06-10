package com.ballistic.batch_report.coredel.query;

public enum QueryOption {

    EQUAL(0, "="), LESS_THEN(1, "<"), GREATER_THEN(2, ">"),
    LESS_THEN_EQUAL(3, "<="), GREATER_THEN_EQUAL(4, ">="), NOT_EQUAL(5, "!="),
    IN(6, "In"), NOT_IN(7, "nIn");

    Integer option;
    String optionValue;

    QueryOption(int option, String optionValue) {
        this.option = option;
        this.optionValue = optionValue;
    }

    public Integer getOption() { return option; }
    public String getOptionValue() { return optionValue; }

    public static String getMatchOptionValue(Integer option) {
        String optionValue = null;
        switch (option) {
            case 0:
                optionValue = EQUAL.getOptionValue();
                break;
            case 1:
                optionValue = LESS_THEN.getOptionValue();
                break;
            case 2:
                optionValue = GREATER_THEN.getOptionValue();
                break;
            case 3:
                optionValue = LESS_THEN_EQUAL.getOptionValue();
                break;
            case 4:
                optionValue = GREATER_THEN_EQUAL.getOptionValue();
                break;
            case 5:
                optionValue = NOT_EQUAL.getOptionValue();
                break;
            case 6:
                optionValue = IN.getOptionValue();
                break;
            case 7:
                optionValue = NOT_IN.getOptionValue();
                break;
            default: // default will equal
                optionValue = EQUAL.getOptionValue();
                break;
        }
        return optionValue;
    }

}
