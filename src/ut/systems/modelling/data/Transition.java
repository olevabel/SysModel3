package ut.systems.modelling.data;

import java.util.ArrayList;


/**
 * @(#) Transition.java
 */


public class Transition {
    private ArrayList<Place> p2t;

    private ArrayList<Place> t2p;

    private PetriNet petriNet;

    private String name;

    public Transition(String transitionLabel) {
        this.name = transitionLabel;
    }

    public ArrayList<Place> getIncomingPlaces() {
        return p2t;
    }

    public ArrayList<Place> getOutgoingPlaces() {
        return p2t;
    }


}
