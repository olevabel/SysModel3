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
}
