package gairrymander;

import java.util.*;

public class Gerrymanderer {
    private int population;
    private int districts;
    private double error; // max error between actual and average district population
    private Graph<Precinct> g;

    public static void main(String[] args) {      
        Random rand = new Random();
        Precinct[] precincts = new Precinct[6];
        int population = 0;
        int districts = 2;
        for (int i = 0; i < 6; i++) {
            int precinctPop = rand.nextInt(1000);
            precincts[i] = new Precinct(i, precinctPop, rand.nextDouble());
            population += precinctPop;
        }

        Gerrymanderer gerry = new Gerrymanderer(population, districts);
        gerry.g.addVertex(precincts[0], Arrays.asList(precincts[1], precincts[2], precincts[3]));
        gerry.g.addVertex(precincts[1], Arrays.asList(precincts[0], precincts[3]));
        gerry.g.addVertex(precincts[2], Arrays.asList(precincts[0], precincts[3], precincts[5]));
        gerry.g.addVertex(precincts[3], Arrays.asList(precincts[0], precincts[1], precincts[2], precincts[4]));
        gerry.g.addVertex(precincts[4], Arrays.asList(precincts[3]));
        gerry.g.addVertex(precincts[5], Arrays.asList(precincts[2]));
        System.out.println(gerry.g);
    }

    public Gerrymanderer(int population, int districts){
        this.population = population;
        this.districts = districts;
        this.error = 0.1; // probably want to change this later
        this.g = new Graph<>();
    }

    public void gerrymander() {

    }
    
}
