/**
 * @(#) Node.java
 */
package ut.systems.modelling.data;
public class Node
{
	private SequenceFlow src;
	
	private SequenceFlow tgt;
	
	private String name;
	
	private BPMN bpmn;
	
	public boolean isKindOf(Class<?> nodeType) {
		return nodeType.isInstance(this);
	}
	
	public BPMN getBPMN( )
	{
		return null;
	}



}
