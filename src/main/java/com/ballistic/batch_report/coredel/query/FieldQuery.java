package com.ballistic.batch_report.coredel.query;

/**
 * Note :- Group by Will Add Later
 * */
public class FieldQuery {

    private String field; // field name
    private Integer operation; // (1-6)(=,!=,<,>,<=,>=,!)
    private Object value; // x-y-z

    public FieldQuery() { }

    public FieldQuery(String field, Integer operation, Object value) {
        this.field = field;
        this.operation = operation;
        this.value = value;
    }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public Integer getOperation() { return operation; }
    public void setOperation(Integer operation) { this.operation = operation; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldQuery that = (FieldQuery) o;

        if (!field.equals(that.field)) return false;
        if (!operation.equals(that.operation)) return false;
        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + operation.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
