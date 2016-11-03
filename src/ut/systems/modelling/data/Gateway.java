package ut.systems.modelling.data;

/**
 * @(#) Gateway.java
 */

public class Gateway extends Node {

    public enum Type {
        XORSPLIT,
        XORJOIN
    }
    private Type type;

    public Type getType() {
        return type;
    }
}
