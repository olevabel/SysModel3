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
            if (!storageMap.containsKey(src)) {
                Transition srcTransition = null;
                if (src.isKindOf(Compound.class)) {
                    srcTransition = petriNet.addTransition(src.getName());
                    subprocesses.put(srcTransition, src.getBpmn());
                } else if (src.isKindOf(Gateway.class)) {
                    srcTransition = petriNet.addTransition("");
                    Gateway gateway = (Gateway) src;
                    if (gateway.getType() == Gateway.Type.XORSPLIT) {
                        xorSplit.add(srcTransition);
                    } else if (gateway.getType() == Gateway.Type.XORJOIN) {
                        xorJoin.add(srcTransition);
                    }
                } else {
                    petriNet.addTransition(src.getName());
                }
                storageMap.put(src, srcTransition);
            }
            Transition srcTransition = storageMap.get(src);
            petriNet.addArcT2P(srcTransition,place);
        }

        return petriNet;
    }

    private PetriNet init(BPMN inputBPMN) {
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
        storageMap.put(startEvent, startTransition);

        //add end event
        Event endEvent = bpmn.getEndEvent();
        storageMap.put(endEvent, endTransition);
        return petriNet;
    }

    public void attachPetriNets(Place inPlace, Place outPlace, PetriNet subPetriNet, PetriNet outputPN) {

    }


}
