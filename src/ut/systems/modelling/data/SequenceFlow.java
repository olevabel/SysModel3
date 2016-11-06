/**
 * @(#) SequenceFlow.java
 */
package ut.systems.modelling.data;

public class SequenceFlow
{
	private Node src;
	
	private Node tgt;
	
	private BPMN bpmn;
	
	public Node getSrc( )
	{
		return src;
	}
	
	public Node getTgt( )
	{
		return tgt;
	}

	public void setSrc(Node src) {
		this.src = src;
	}

	public void setTgt(Node tgt) {
		this.tgt = tgt;
	}
}
