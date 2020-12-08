package gairrymander;

import java.util.*;

public class District {
    public int population;
    public int popD;
    public Set<Precinct> precincts;

    public District() {
        this(0, 0, new HashSet<>());
    }

    public District(int population, int popD, Set<Precinct> precincts) {
        this.population = population;
        this.popD = popD;
        this.precincts = precincts;
    }

    public District(District d) {
        this.population = d.population;
        this.popD = d.popD;
        this.precincts = new HashSet<>();
        this.precincts.addAll(d.precincts);
    }

    public void add(Precinct p) {
        precincts.add(p);
        population += p.population;
        popD += p.population * p.percentD;
    }

    public void remove(Precinct p) {
        precincts.remove(p);
        population -= p.population;
        popD -= p.population * p.percentD;
    }

    public boolean isD() {
        return popD >= population / 2 + 1;
    }

    public double winPercent() {
        if (isD()) {
            return 1.0 * popD / population;
        }
        return 1.0 * (population - popD) / population;
    }

    public String toString() {
        String str = "{";
        if (isD()) {
			str += "D";
		} else {
			str += "R";
		}
        return str + " " + winPercent() + "% " + precincts.toString() + "}";
    }
}
