package ut.systems.modelling.data;

/**
 * @(#) Gateway.java
 */

public class Gateway extends Node {

    public enum Type {
        XORSPLIT,
        XORJOIN,
        ANDJOIN,
        ANDSPLIT
    }
    private Type type;

    public Gateway(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
