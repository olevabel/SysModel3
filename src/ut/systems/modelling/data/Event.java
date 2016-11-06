/**
 * @(#) Event.java
 */
package ut.systems.modelling.data;

public class Event extends Node {

    public enum EventType {
        START, INTERMEDIATE, END;
    }
    private EventType type;

    public EventType getType() {
        return type;
    }

    public Event(EventType type) {
        this.type = type;
    }
}
