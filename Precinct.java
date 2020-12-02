package gairrymander;

import java.util.*;

public class Precinct implements Comparable<Precinct> {
	public int code;
	public int population;
	public double percentD;
	public boolean isD;
	public int district;
	
	public Precinct(int code, int population, double percentD) {
		this.code = code;
		this.population = population;
		this.percentD = percentD;
		this.isD = percentD >= 0.5;
		district = -1; // initially unlabeled
	}
	
	public int compareTo(Precinct o) {
		return Double.compare(this.percentD, o.percentD);
	}

	public String toString() {
		String str = "{" + code + ": ";
		if (isD) {
			str += "D";
		} else {
			str += "R";
		}
		return str + ", " + population + ", " + percentD * 100 + "% D}";
	}
}
