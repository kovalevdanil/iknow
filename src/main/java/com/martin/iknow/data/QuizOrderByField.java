package com.martin.iknow.data;

public enum QuizOrderByField {
    ID("id"),
    ID_DESC("-id"),
    RATING("rating"),
    RATING_DESC("-rating"),
    CREATED_AT("created_at"),
    CREATED_AT_DESC("-created_at");

    private final String field;

    QuizOrderByField(String field){
        this.field = field;
    }

    public static boolean validFieldName(String field){
        for (var f : QuizOrderByField.values())
            if (f.field.equals(field))
                return true;
        return false;
    }
}
