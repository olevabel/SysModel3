package ut.systems.modelling;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import ut.systems.modelling.data.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


@Plugin(
        name = "Converter BPMN-PN",
        parameterLabels = {"BPMNDiagram"},
        returnLabels = {"Petri-Net"},
        returnTypes = {Petrinet.class},
        userAccessible = true,
        help = "Convert a BPMN diagram into a Petri-Net"
)
public class ConverterPlugin {

    private static HashMap<BPMNNode, Node> helperMap;

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Name Surname",
            email = "name.surname@ut.ee"
    )
    @PluginVariant(variantLabel = "Convert BPMN into PN", requiredParameterLabels = {0})
    public static Petrinet optimizeDiagram(UIPluginContext context, BPMNDiagram diagram) {
        helperMap = new HashMap<>();
        Controller controller = new Controller();
        BPMN myBPMNModel = getMyBPMNModel(diagram);
        PetriNet myPetriNet = controller.convertToPetri(myBPMNModel,false);
        return myPetriNetToPetrinet(myPetriNet);
    }

    public static BPMN getMyBPMNModel(BPMNDiagram diagram) {
        return convertToMyBPMN(diagram.getFlows(), diagram);

    }

    private static BPMN convertToMyBPMN(Collection<Flow> flows, BPMNDiagram diagram) {
        BPMN bpmn = new BPMN();
        ArrayList<SequenceFlow> sequenceFlows = new ArrayList<>();
        for (Flow flow : flows) {
            SequenceFlow sequenceFlow = new SequenceFlow();
            if (flow.getSource() instanceof Event) {
                Event event = (Event) flow.getSource();
                if (event.getEventType().equals(Event.EventType.INTERMEDIATE)) {
                    if (helperMap.containsKey(event)) {
                        sequenceFlow.setSrc(helperMap.get(event));
                    } else {
                        ut.systems.modelling.data.Event myEvent = new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.INTERMEDIATE);
                        helperMap.put(event, myEvent);
                        sequenceFlow.setSrc(myEvent);
                    }
                } else if (event.getEventType().equals(Event.EventType.START)) {
                    ut.systems.modelling.data.Event startEvent = new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.START);
                    bpmn.setStartEvent(startEvent);
                    sequenceFlow.setSrc(startEvent);
                }
            }
            if (flow.getSource() instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) flow.getSource();
                if (helperMap.containsKey(subProcess)) {
                    sequenceFlow.setSrc(helperMap.get(subProcess));
                } else {
                    Compound compound = new Compound(convertToMyBPMN(diagram.getFlows(subProcess), diagram));
                    helperMap.put(subProcess, compound);
                    sequenceFlow.setSrc(compound);
                }
            }
            if (flow.getSource() instanceof Gateway) {
                Gateway gateway = (Gateway) flow.getSource();
                if (gateway.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    ut.systems.modelling.data.Gateway myGateway;
                    if (gateway.getGraph().getOutEdges(gateway).size() > 1) {
                        if (helperMap.containsKey(gateway)) {
                            sequenceFlow.setSrc(helperMap.get(gateway));
                        } else {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORSPLIT);
                            helperMap.put(gateway, myGateway);
                            sequenceFlow.setSrc(myGateway);
                        }

                    } else {
                        if (helperMap.containsKey(gateway)) {
                            sequenceFlow.setSrc(helperMap.get(gateway));
                        } else {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORJOIN);
                            helperMap.put(gateway, myGateway);
                            sequenceFlow.setSrc(myGateway);
                        }
                    }
                } else if (gateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                    ut.systems.modelling.data.Gateway myGateway;
                    if (gateway.getGraph().getOutEdges(gateway).size() > 1) {
                        if (helperMap.containsKey(gateway)) {
                            sequenceFlow.setSrc(helperMap.get(gateway));
                        } else {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.ANDSPLIT);
                            helperMap.put(gateway, myGateway);
                            sequenceFlow.setSrc(myGateway);
                        }

                    } else {
                        if (helperMap.containsKey(gateway)) {
                            sequenceFlow.setSrc(helperMap.get(gateway));
                        } else {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.ANDJOIN);
                            helperMap.put(gateway, myGateway);
                            sequenceFlow.setSrc(myGateway);
                        }
                    }
                }

            }
            if (flow.getSource() instanceof Activity) {
                Activity activity = (Activity) flow.getSource();
                if (helperMap.containsKey(activity)) {
                    sequenceFlow.setSrc(helperMap.get(activity));
                } else {
                    Simple simple = new Simple();
                    helperMap.put(activity, simple);
                    sequenceFlow.setSrc(simple);
                }
            }
            if (flow.getTarget() instanceof Event) {
                Event event = (Event) flow.getTarget();
                if (event.getEventType().equals(Event.EventType.INTERMEDIATE)) {
                    if (!helperMap.containsKey(event)) {
                        ut.systems.modelling.data.Event myEvent = new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.INTERMEDIATE);
                        sequenceFlow.setTgt(myEvent);
                        helperMap.put(event, myEvent);
                    } else {
                        sequenceFlow.setTgt(helperMap.get(event));
                    }
                } else if (event.getEventType().equals(Event.EventType.END)) {
                    ut.systems.modelling.data.Event endEvent = new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.END);
                    bpmn.setEndEvent(endEvent);
                    sequenceFlow.setTgt(endEvent);
                }
            }

            if (flow.getTarget() instanceof Gateway) {
                Gateway gateway = (Gateway) flow.getTarget();
                if (gateway.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    if (helperMap.containsKey(gateway)) {
                        sequenceFlow.setTgt(helperMap.get(gateway));
                    } else {
                        ut.systems.modelling.data.Gateway myGateway;
                        if (gateway.getGraph().getOutEdges(gateway).size() > 1) {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORSPLIT);
                        } else {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORJOIN);
                        }
                        sequenceFlow.setTgt(myGateway);
                        helperMap.put(gateway, myGateway);
                    }
                } else if (gateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                    if (helperMap.containsKey(gateway)) {
                        sequenceFlow.setTgt(helperMap.get(gateway));
                    } else {
                        ut.systems.modelling.data.Gateway myGateway;
                        if (gateway.getGraph().getOutEdges(gateway).size() > 1) {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.ANDSPLIT);
                        } else {
                            myGateway = new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.ANDJOIN);
                        }

                        sequenceFlow.setTgt(myGateway);
                        helperMap.put(gateway, myGateway);
                    }
                }
            }

            if (flow.getTarget() instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) flow.getTarget();
                if (helperMap.containsKey(subProcess)) {
                    sequenceFlow.setTgt(helperMap.get(subProcess));
                } else {
                    Compound compound = new Compound(convertToMyBPMN(diagram.getFlows(subProcess), diagram));
                    sequenceFlow.setTgt(compound);
                    helperMap.put(subProcess, compound);
                }
            }

            if (flow.getTarget() instanceof Activity) {
                Activity activity = (Activity) flow.getTarget();
                if (helperMap.containsKey(activity)) {
                    sequenceFlow.setTgt(helperMap.get(activity));
                } else {
                    Simple simple = new Simple();
                    sequenceFlow.setTgt(simple);
                    helperMap.put(activity, simple);
                }
            }
            sequenceFlows.add(sequenceFlow);
        }
        bpmn.setFlows(sequenceFlows);
        return bpmn;
    }

    public static Petrinet myPetriNetToPetrinet(PetriNet myPetriNet) {
        PetrinetImpl pn = new PetrinetImpl("Result PN");
        for (Transition transition : myPetriNet.getTransitions()) {
            if (transition.getIncomingPlaces().isEmpty()) {
                pn.addPlace(Controller.START_PLACE);
            } else if (transition.getOutgoingPlaces().isEmpty()) {
                pn.addPlace(Controller.END_PLACE);
            } else {
                for (Place place : transition.getIncomingPlaces()) {
                    org.processmining.models.graphbased.directed.petrinet.elements.Place place1 = pn.addPlace(place.getLabel());
                    org.processmining.models.graphbased.directed.petrinet.elements.Transition transition1 = pn.addTransition(transition.getName());
                    pn.addArc(place1, transition1);
                }
                for (Place place : transition.getOutgoingPlaces()) {
                    org.processmining.models.graphbased.directed.petrinet.elements.Place place1 = pn.addPlace(place.getLabel());
                    org.processmining.models.graphbased.directed.petrinet.elements.Transition transition1 = pn.addTransition(transition.getName());
                    pn.addArc(transition1, place1);
                }
            }
        }
        return pn;
    }


}