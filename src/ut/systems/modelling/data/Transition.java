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
        this.p2t = new ArrayList<>();
        this.t2p = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Place> getIncomingPlaces() {
        return p2t;
    }

    public ArrayList<Place> getOutgoingPlaces() {
        return t2p;
    }


    public void addOutgoingPlace(Place place) {
        getOutgoingPlaces().add(place);
    }

    public void addIncomingPlace(Place place) {
        getIncomingPlaces().add(place);
    }

    public void removeOutgoingPlace(Place place) {
        getOutgoingPlaces().remove(place);
    }

    public void removeIncomingPlace(Place place) {
        getIncomingPlaces().remove(place);
    }
}
