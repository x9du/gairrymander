package gairrymander;

import java.util.*;

public class Precinct implements Comparable<Precinct> {
	public int code;
	public int population;
	public double percentD;
	public boolean isD;
	public int district;
	public double x;
	public double y;
	
	public Precinct(int code, int population, double percentD) {
		this.code = code;
		this.population = population;
		this.percentD = percentD;
		isD = percentD >= 0.5;
		district = -1; // initially unlabeled
		x = 0.0;
		y = 0.0;
	}

	public Precinct(Precinct p) {
		this.code = p.code;
		this.population = p.population;
		this.percentD = p.percentD;
		isD = percentD >= 0.5;
		district = p.district;
		x = p.x;
		y = p.y;
	}

	public Precinct(Precinct p, int district) {
		this.code = p.code;
		this.population = p.population;
		this.percentD = p.percentD;
		isD = percentD >= 0.5;
		this.district = district;
		x = p.x;
		y = p.y;
	}

	public String toJSON() {
		double w = 0.2;
		double[][] latlngs = new double[4][2];
		latlngs[0][0] = x; latlngs[0][1] = y;
		latlngs[1][0] = x; latlngs[1][1] = y - w;
		latlngs[2][0] = x - w; latlngs[2][1] = y - w;
		latlngs[3][0] = x - w; latlngs[3][1] = y;
		StringBuilder sb = new StringBuilder("{ \"latlngs\":[");
		for (int i = 0; i < latlngs.length; i++) {
			sb.append("[");
			sb.append(latlngs[i][0]);
			sb.append(", ");
			sb.append(latlngs[i][1]);
			sb.append("]");
			if (i < latlngs.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("], \"percentD\":");
		sb.append(percentD);
		sb.append(", \"isD\":");
		sb.append(isD);
		sb.append(", \"district\":");
		sb.append(district);
		sb.append(" }");
		return sb.toString();
	}
	
	public int compareTo(Precinct o) {
		return Double.compare(this.percentD, o.percentD);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Precinct)) {
			return false;
		}
		Precinct other = (Precinct) o;
		return code == other.code;
	}

	public int hashCode() {
		return Objects.hash(code);
	}

	public String toString() {
		String str = "{" + code + ": ";
		if (isD) {
			str += "D";
		} else {
			str += "R";
		}
		return str + ", " + district + ", " + population + ", " + percentD * 100 + "% D}";
	}
}
