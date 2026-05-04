package br.com.dio.model;

public class Space {

    private Integer actual;
    private final int expected;
    private final boolean fixed;
    private final int row;
    private final int col;

    public Space(final int expected, final boolean fixed, final int row, final int col) {
        this.expected = expected;
        this.fixed = fixed;
        this.row = row;
        this.col = col;
        if (fixed){
            actual = expected;
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Integer getActual() {
        return actual;
    }

    public void setActual(final Integer actual) {
        if (fixed) return;
        this.actual = actual;
    }

    public void clearSpace(){
        setActual(null);
    }

    public int getExpected() {
        return expected;
    }

    public boolean isFixed() {
        return fixed;
    }
}