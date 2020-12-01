import java.util.*;

public class Precinct implements Comparable<Precinct> {
	private int population;
	private double percentD;
	private boolean isD;
	private List<Precinct> bordering;
	
	public Precinct(int population, double percentD, List<Precinct> bordering) {
		this.population = population;
		this.percentD = percentD;
		this.bordering = new LinkedList<Precinct>();
		bordering.addAll(bordering);
		this.isD = percentD >= 0.5;
	}
	
	public int compareTo(Precinct o) {
		return Double.compare(this.percentD, o.percentD);
	}

}
