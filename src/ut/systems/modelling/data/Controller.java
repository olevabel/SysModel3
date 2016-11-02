package ut.systems.modelling.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @(#) Controller.java
 */

public class Controller {
    private Boundary boundary;

    private PetriNet petriNet;

    private HashMap<Node, Transition> storageMap = new HashMap<>();

    private Set<Transition> xorSplit;

    private Set<Transition> xorJoin;

    private HashMap<Transition, BPMN> subprocesses;

    private BPMN bpmn;

    public PetriNet convertToPetri(BPMN inputBPMN) {
        PetriNet petriNet = init(inputBPMN);

        ArrayList<SequenceFlow> flows = bpmn.getFlows();

        for (int i = 0; i < flows.size() ; i++) {
            petriNet.addPlace("");
            SequenceFlow flow = flows.get(i);
            Node src = flow.getSrc();
            Node tgt = flow.getTgt();
            if(!storageMap.containsKey(src)) {
                if (src.isKindOfCompund(src))
            }
        }

        return petriNet;
    }

    private PetriNet init(BPMN inputBPMN) {
        this.bpmn = inputBPMN;
        PetriNet petriNet = new PetriNet();

        //add start place
        Place startPlace = petriNet.addPlace("start");
        Transition startTransition = petriNet.addTransition("");
        petriNet.addArcP2T(startPlace, startTransition);

        //add end place
        Place endPlace = petriNet.addPlace("end");
        Transition endTransition = petriNet.addTransition("");
        petriNet.addArcT2P(endTransition, endPlace);

        //add start event
        Event startEvent = bpmn.getStartEvent();
        storageMap.put(startEvent,startTransition);

        //add end event
        Event endEvent = bpmn.getEndEvent();
        storageMap.put(endEvent,endTransition);
        return petriNet;
    }

    public void attachPetriNets(Place inPlace, Place outPlace, PetriNet subPetriNet, PetriNet outputPN) {

    }


}
