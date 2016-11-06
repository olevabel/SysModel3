package ut.systems.modelling.data;


import java.util.ArrayList;

/**
 * @(#) BPMN.java
 */

public class BPMN
{
	private Controller context;
	private Event startEvent;
	private Event endEvent;
	private Compound parent;
	
	private ArrayList<SequenceFlow> flows;
	
	private Node nodes;

	public void setStartEvent(Event startEvent) {
		this.startEvent = startEvent;
	}

	public void setEndEvent(Event endEvent) {
		this.endEvent = endEvent;
	}

	public void setParent(Compound parent) {
		this.parent = parent;
	}

	public void setFlows(ArrayList<SequenceFlow> flows) {
		this.flows = flows;
	}

	public Event getStartEvent( )
	{
		return startEvent;
	}
	
	public ArrayList<SequenceFlow> getFlows( )
	{
		return flows;
	}
	
	public Event getEndEvent( )
	{
		return endEvent;
	}
	
	
}
