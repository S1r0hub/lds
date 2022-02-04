package s1r0hub.examples;

import java.util.HashMap;

import lds.measures.Measure;
import lds.resource.R;

/**
 * A class that holds a pair of entities.
 * @author S1r0hub
 */
public class EntityPair {
	
	private R e1, e2;
	private HashMap<Measure, Double> r = new HashMap<>();
	
	public EntityPair(R resource1, R resource2) {
		e1 = resource1;
		e2 = resource2;
	}
	
	public R first() { return e1; }
	public R second() { return e2; }
	
	/**
	 * Get relatedness for specific measure.
	 * @param m The {@link Measure} to return value for
	 * @return <code>Null</code> if no value was added for this measure, otherwise the value.
	 */
	public double getRelatedness(Measure m) { return r.get(m); }
	public void setRelatedness(Measure measure, double val) { r.put(measure, val); }
}
