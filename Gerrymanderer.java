package gairrymander;

import java.util.*;

public class Gerrymanderer {
    private int population;
    private int numPrecincts;
    private int numDistricts;
    private double error; // max error between actual and average district population
    private Graph<Precinct> g;

    public static void main(String[] args) {      
        Random rand = new Random();
        Precinct[] precincts = new Precinct[6];
        int population = 0;
        int numDistricts = 2;
        for (int i = 0; i < 6; i++) {
            int precinctPop = rand.nextInt(1000);
            precincts[i] = new Precinct(i, precinctPop, rand.nextDouble());
            population += precinctPop;
        }

        Gerrymanderer gerry = new Gerrymanderer(population, precincts.length, numDistricts);
        gerry.g.addVertex(precincts[0], Arrays.asList(precincts[1], precincts[2], precincts[3]));
        gerry.g.addVertex(precincts[1], Arrays.asList(precincts[0], precincts[3]));
        gerry.g.addVertex(precincts[2], Arrays.asList(precincts[0], precincts[3], precincts[5]));
        gerry.g.addVertex(precincts[3], Arrays.asList(precincts[0], precincts[1], precincts[2], precincts[4]));
        gerry.g.addVertex(precincts[4], Arrays.asList(precincts[3]));
        gerry.g.addVertex(precincts[5], Arrays.asList(precincts[2]));
        System.out.println("average district population " + population / numDistricts);
        // System.out.println(gerry.g);
        // gerry.generateAllDistricts(population / numDistricts);
    }

    public Gerrymanderer(int population, int numPrecincts, int numDistricts){
        this.population = population;
        this.numPrecincts = numPrecincts;
        this.numDistricts = numDistricts;
        this.error = 0.1; // probably want to change this later
        this.g = new Graph<>();
    }

    public void gerrymander() {

    }

    // Commented out for the commit because it's unfinished
    /*public void generateAllDistricts(int precinctPop) {
        generateAllDistricts(g.getRoot(), 0, -1, 0, precinctPop);
    }

    private void generateAllDistricts(Precinct root, int visited, int prevDistrict, int nextDistrict, int precinctPop) {
        if (visited == numPrecincts) {
            System.out.println(g);
        } else {
            visited++;
            // System.out.print(root.code + " ");
            if (precinctPop <= 0) {

            }
            if (prevDistrict != -1) {
                root.district = prevDistrict;
            } else {
                // if (precinctPop - root.population < 0 - error) {
                //     nextDistrict++;
                // }
                root.district = nextDistrict;
            }
            
            Iterator<Precinct> i = g.getAdj(root).listIterator();
            while (i.hasNext()) {
                Precinct p = i.next();
                if (p.district == -1)
                    generateAllDistricts(p, visited, prevDistrict, nextDistrict, precinctPop);
            }
        }
    }*/
}
