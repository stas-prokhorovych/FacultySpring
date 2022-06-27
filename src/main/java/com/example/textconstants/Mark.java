package com.example.textconstants;

public enum Mark {
    A("A", "Excellent"),
    B("B", "Very good"),
    C("C", "Good"),
    D("D", "Satisfactory"),
    E("E", "Sufficient"),
    FX("FX", "Fail"),
    F("F", "Fail");

    private String code;
    private String explanation;

    Mark(String code, String explanation) {
        this.code = code;
        this.explanation = explanation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
