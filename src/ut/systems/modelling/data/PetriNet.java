package ut.systems.modelling.data;

import java.util.ArrayList;

/**
 * @(#) PetriNet.java
 */

public class PetriNet {
    private ArrayList<Place> places;

    private ArrayList<Transition> transitions;

    private Controller context;

    public PetriNet() {
        this.places = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    public Place addPlace(String placeLabel) {
        return new Place(placeLabel);
    }

    public Transition addTransition(String transitionLabel) {
        return new Transition(transitionLabel);
    }

    public void addArcP2T(Place srcPlace, Transition targetTransition) {
        places.add(srcPlace);
        transitions.add(targetTransition);
    }

    public void addArcT2P(Transition srcTransition, Place targetPlace) {
        transitions.add(srcTransition);
        places.add(targetPlace);
    }

    public void removeArcT2P(Transition nextSplit, Place outgoingPlace) {

    }

    public void removeArcP2T(Place incomingPlace, Transition nextJoin) {

    }

    public void removeTransition(Transition subprocessTransition) {

    }


}
