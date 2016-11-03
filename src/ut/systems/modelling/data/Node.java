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


	public SequenceFlow getSrc() {
		return src;
	}

	public SequenceFlow getTgt() {
		return tgt;
	}

	public String getName() {
		return name;
	}

	public BPMN getBpmn() {
		return bpmn;
	}
}
