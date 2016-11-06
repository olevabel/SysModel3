package ut.systems.modelling.data;

/**
 * @(#) Place.java
 */

public class Place
{
	private Transition p2t;
	
	private Transition t2p;
	
	private PetriNet petriNet;

	private String label;
	
	private int type;

	public Place(String label) {
		this.label = label;
	}

	public Transition getP2t() {
		return p2t;
	}

	public Transition getT2p() {
		return t2p;
	}

	public PetriNet getPetriNet() {
		return petriNet;
	}

	public String getLabel() {
		return label;
	}

	public int getType() {
		return type;
	}
}
