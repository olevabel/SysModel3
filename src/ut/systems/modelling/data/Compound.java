package ut.systems.modelling.data;



/**
 * @(#) Compound.java
 */

public class Compound extends Node {
	private BPMN child;

	public Compound(BPMN child) {
		this.child = child;
	}

	public BPMN getChild() {
		return child;
	}
}
