package ut.systems.modelling.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @(#) Controller.java
 */

public class Controller {
    private static final String START_PLACE = "start";
    private static final String END_PLACE = "end";
    private Boundary boundary;

    private PetriNet petriNet;

    private HashMap<Node, Transition> storageMap;

    private Set<Transition> xorSplit;

    private Set<Transition> xorJoin;

    private HashMap<Transition, BPMN> subprocesses;

    private BPMN bpmn;

    public Controller(Boundary boundary, PetriNet petriNet, HashMap<Node, Transition> storageMap, Set<Transition> xorSplit, Set<Transition> xorJoin, HashMap<Transition, BPMN> subprocesses, BPMN bpmn) {
        this.boundary = boundary;
        this.petriNet = petriNet;
        this.storageMap = storageMap;
        this.xorSplit = xorSplit;
        this.xorJoin = xorJoin;
        this.subprocesses = subprocesses;
        this.bpmn = bpmn;
    }

    public PetriNet convertToPetri(BPMN inputBPMN) {
        PetriNet petriNet = init(inputBPMN);

        ArrayList<SequenceFlow> flows = bpmn.getFlows();

        for (int i = 0; i < flows.size(); i++) {
            Place place = petriNet.addPlace("");
            SequenceFlow flow = flows.get(i);
            Node src = flow.getSrc();
            Node tgt = flow.getTgt();
            addTransition(petriNet, src);
            Transition srcTransition = storageMap.get(src);
            petriNet.addArcT2P(srcTransition, place);
            addTransition(petriNet, tgt);
            Transition tgtTransition = storageMap.get(tgt);
            petriNet.addArcP2T(place, tgtTransition);
            Iterator xorSplitIterator = xorSplit.iterator();
            while (xorSplitIterator.hasNext()) {
                Transition nextSplit = (Transition) xorSplitIterator.next();
                Transition invisibleTransition = petriNet.addTransition("");
                petriNet.addArcP2T(nextSplit.getIncomingPlaces().get(0), invisibleTransition);
                petriNet.addArcT2P(invisibleTransition, nextSplit.getOutgoingPlaces().get(0));
                petriNet.removeArcT2P(nextSplit, nextSplit.getOutgoingPlaces().get(0));
            }
            Iterator xorJoinIterator = xorJoin.iterator();
            while (xorJoinIterator.hasNext()) {
                Transition nextJoin = (Transition) xorJoinIterator.next();
                Transition invisibleTransition = petriNet.addTransition("");
                petriNet.addArcP2T(nextJoin.getIncomingPlaces().get(0), invisibleTransition);
                petriNet.addArcT2P(invisibleTransition, nextJoin.getOutgoingPlaces().get(0));
                petriNet.removeArcP2T(nextJoin.getOutgoingPlaces().get(0), nextJoin);
            }

            for (Transition transition : subprocesses.keySet()) {
                BPMN subprocessBPMN = subprocesses.get(transition);
                PetriNet subPetriNet = convertToPetri(subprocessBPMN);
                Place inPlace = transition.getIncomingPlaces().get(0);
                Place outPlace = transition.getOutgoingPlaces().get(0);
                petriNet.removeTransition(transition);
                attachPetriNets(inPlace, outPlace, subPetriNet, petriNet);
            }
        }

        return petriNet;
    }

    private void addTransition(PetriNet petriNet, Node node) {
        if (!storageMap.containsKey(node)) {
            Transition transition;
            if (node.isKindOf(Compound.class)) {
                transition = petriNet.addTransition(node.getName());
                subprocesses.put(transition, node.getBpmn());
            } else if (node.isKindOf(Gateway.class)) {
                transition = petriNet.addTransition("");
                Gateway gateway = (Gateway) node;
                if (gateway.getType() == Gateway.Type.XORSPLIT) {
                    xorSplit.add(transition);
                } else if (gateway.getType() == Gateway.Type.XORJOIN) {
                    xorJoin.add(transition);
                }
            } else {
                transition = petriNet.addTransition(node.getName());
            }
            storageMap.put(node, transition);
        }
    }

    private PetriNet init(BPMN inputBPMN) {
        PetriNet petriNet = new PetriNet();

        //add start place
        Place startPlace = petriNet.addPlace(START_PLACE);
        Transition startTransition = petriNet.addTransition("");
        petriNet.addArcP2T(startPlace, startTransition);

        //add end place
        Place endPlace = petriNet.addPlace(END_PLACE);
        Transition endTransition = petriNet.addTransition("");
        petriNet.addArcT2P(endTransition, endPlace);

        //add start event
        Event startEvent = inputBPMN.getStartEvent();
        storageMap.put(startEvent, startTransition);

        //add end event
        Event endEvent = inputBPMN.getEndEvent();
        storageMap.put(endEvent, endTransition);
        return petriNet;
    }

    public void attachPetriNets(Place inPlace, Place outPlace, PetriNet subPetriNet, PetriNet outputPN) {
        for (Transition transition : subPetriNet.getTransitions()) {
            for (Place place : transition.getIncomingPlaces()) {
                if (place.getLabel().equals(START_PLACE)) {
                    subPetriNet.removeArcP2T(place, transition);
                    place = inPlace;
                    outputPN.addArcP2T(place, transition);
                    break;
                }
            }
            for (Place place : transition.getOutgoingPlaces()) {
                if(place.getLabel().equals(END_PLACE)) {
                    subPetriNet.removeArcT2P(transition, place);
                    place = outPlace;
                    outputPN.addArcT2P(transition, place);
                    break;
                }
            }
        }
    }


}
