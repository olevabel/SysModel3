package ut.systems.modelling;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.elements.*;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import ut.systems.modelling.data.*;

import java.util.ArrayList;
import java.util.Collection;


@Plugin(
        name = "Converter BPMN-PN",
        parameterLabels = {"BPMNDiagram"},
        returnLabels = {"Petri-Net"},
        returnTypes = {Petrinet.class},
        userAccessible = true,
        help = "Convert a BPMN diagram into a Petri-Net"
)
public class ConverterPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Name Surname",
            email = "name.surname@ut.ee"
    )
    @PluginVariant(variantLabel = "Convert BPMN into PN", requiredParameterLabels = {0})
    public static Petrinet optimizeDiagram(UIPluginContext context, BPMNDiagram diagram) {

        Controller controller = new Controller();
        BPMN myBPMNModel = getMyBPMNModel(diagram);
        PetriNet myPetriNet = controller.convertToPetri(myBPMNModel);
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
                    sequenceFlow.setSrc(new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.INTERMEDIATE));
                } else if (event.getEventType().equals(Event.EventType.START)) {
                    ut.systems.modelling.data.Event startEvent = new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.START);
                    bpmn.setStartEvent(startEvent);
                    sequenceFlow.setSrc(startEvent);
                }
            }
            if (flow.getTarget() instanceof Event) {
                Event event = (Event) flow.getTarget();
                if (event.getEventType().equals(Event.EventType.INTERMEDIATE)) {
                    sequenceFlow.setTgt(new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.INTERMEDIATE));
                } else if (event.getEventType().equals(Event.EventType.END)) {
                    ut.systems.modelling.data.Event endEvent = new ut.systems.modelling.data.Event(ut.systems.modelling.data.Event.EventType.END);
                    bpmn.setEndEvent(endEvent);
                    sequenceFlow.setTgt(endEvent);
                }
            }
            if (flow.getSource() instanceof Gateway) {
                Gateway gateway = (Gateway) flow.getSource();
                if (gateway.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    if (gateway.getGraph().getOutEdges(gateway).size() > 1) {
                        sequenceFlow.setSrc(new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORSPLIT));
                    } else {
                        sequenceFlow.setSrc(new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORJOIN));
                    }
                } else if (gateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                    sequenceFlow.setSrc(new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.AND));
                }
            }
            if (flow.getTarget() instanceof Gateway) {
                Gateway gateway = (Gateway) flow.getTarget();
                if (gateway.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    if (gateway.getGraph().getOutEdges(gateway).size() > 1) {
                        sequenceFlow.setTgt(new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORSPLIT));
                    } else {
                        sequenceFlow.setTgt(new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.XORJOIN));
                    }
                } else if (gateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                    sequenceFlow.setTgt(new ut.systems.modelling.data.Gateway(ut.systems.modelling.data.Gateway.Type.AND));
                }
            }
            if (flow.getSource() instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) flow.getSource();
                sequenceFlow.setSrc(new Compound(convertToMyBPMN(diagram.getFlows(subProcess), diagram)));
            }
            if (flow.getTarget() instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) flow.getTarget();
                sequenceFlow.setTgt(new Compound(convertToMyBPMN(diagram.getFlows(subProcess), diagram)));
            }
            if (flow.getSource() instanceof Activity) {
                sequenceFlow.setSrc(new Simple());
            }
            if (flow.getTarget() instanceof Activity) {
                sequenceFlow.setTgt(new Simple());
            }
            sequenceFlows.add(sequenceFlow);
        }
        bpmn.setFlows(sequenceFlows);
        return bpmn;
    }
    public static Petrinet myPetriNetToPetrinet (PetriNet myPetriNet) {
        PetrinetImpl pn = new PetrinetImpl("Result PN");
        for(Transition transition : myPetriNet.getTransitions()) {
            if(transition.getIncomingPlaces().isEmpty()) {
                pn.addPlace(Controller.START_PLACE);
            } else if (transition.getOutgoingPlaces().isEmpty()) {
                pn.addPlace(Controller.END_PLACE);
            } else {
                for(Place place : transition.getIncomingPlaces()) {
                    org.processmining.models.graphbased.directed.petrinet.elements.Place place1 = pn.addPlace(place.getLabel());
                    org.processmining.models.graphbased.directed.petrinet.elements.Transition transition1 = pn.addTransition(transition.getName());
                    pn.addArc(place1, transition1);
                }
                for (Place place : transition.getOutgoingPlaces()) {
                    org.processmining.models.graphbased.directed.petrinet.elements.Place place1 = pn.addPlace(place.getLabel());
                    org.processmining.models.graphbased.directed.petrinet.elements.Transition transition1 = pn.addTransition(transition.getName());
                    pn.addArc(transition1,place1);
                }
            }
        }
        return pn;
    }


}