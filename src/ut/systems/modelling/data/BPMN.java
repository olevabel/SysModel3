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
