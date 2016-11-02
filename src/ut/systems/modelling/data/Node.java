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
	
	public boolean isKindOf(Class<Node> nodeType ) {
		if(this instanceof nodeType) {
			return true;
		}

	}
	
	public boolean isKindOf( Gateway gateway )
	{
		return false;
	}
	
	public BPMN getBPMN( )
	{
		return null;
	}



}
