package gairrymander;

import java.util.*;

public class Gerrymanderer {
    private int population;
    private int numPrecincts;
    private int numDistricts;
    private double error; // max error between actual and average district population
    private Graph<Precinct> g;
    static Precinct[] precincts;

    public static void main(String[] args) {      
        Random rand = new Random();
        precincts = new Precinct[6];
        int population = 0;
        int numDistricts = 2;
        for (int i = 0; i < 6; i++) {
            int precinctPop = 100/*rand.nextInt(1000)*/;
            precincts[i] = new Precinct(i, precinctPop, rand.nextDouble());
            population += precinctPop;
        }

        Gerrymanderer gerry = new Gerrymanderer(population, precincts.length, numDistricts);
        gerry.g.addVertex(precincts[0], Arrays.asList(precincts[1], precincts[2], precincts[3]));
        gerry.g.addVertex(precincts[1], Arrays.asList(precincts[0], precincts[3]));
        gerry.g.addVertex(precincts[2], Arrays.asList(precincts[0], precincts[3], precincts[5]));
        gerry.g.addVertex(precincts[3], Arrays.asList(precincts[0], precincts[1], precincts[2], precincts[4]));
        gerry.g.addVertex(precincts[4], Arrays.asList(precincts[3], precincts[5]));
        gerry.g.addVertex(precincts[5], Arrays.asList(precincts[2], precincts[4]));
        System.out.println("average district population " + population / numDistricts);
        // System.out.println(gerry.g);
        gerry.generateAllDistricts(population / numDistricts);
    }

    public Gerrymanderer(int population, int numPrecincts, int numDistricts){
        this.population = population;
        this.numPrecincts = numPrecincts;
        this.numDistricts = numDistricts;
        this.error = 0.1 * population / numDistricts; // probably want to change this later
        this.g = new Graph<>();
    }

    public void gerrymander() {

    }

    
    public void generateAllDistricts(int precinctPop) {
        generateAllDistricts(g.getRoot(), new HashSet<>(Arrays.asList(precincts)), 0, precinctPop);
    }

    private void generateAllDistricts(Precinct root, Set<Precinct> unvisited, int nextDistrict, int precinctPop) {
        System.out.println(root.code + " ");
        if (precinctPop < 0 - error) {
            System.out.println(":(");
            return;
        } else if (precinctPop <= 0 || precinctPop <= error) { // If this district full, generate next district
            System.out.println(":)");
            generateAllDistricts(root, unvisited, nextDistrict + 1, population / numDistricts);
        } else {
            System.out.println(":|");
            unvisited.remove(root);
            root.district = nextDistrict;
            Iterator<Precinct> i = g.getAdj(root).listIterator();
            while (i.hasNext()) {
                Precinct p = i.next();
                if (p.district == -1)
                    generateAllDistricts(p, unvisited, nextDistrict, precinctPop - root.population);
            }
            if (unvisited.size() == 0 && precinctPop - root.population <= error) {
                System.out.println(Arrays.toString(result()));
                System.out.println(g);
                System.out.println();
            }
            unvisited.add(root);
            root.district = -1;
        }
    }

    public double[] result() {
        return result(g.getRoot(), new boolean[numPrecincts], new double[numDistricts][2], new double[numDistricts]);
    }

    private double[] result(Precinct root, boolean visited[], double[][] districts, double[] result) {
        visited[root.code] = true;
        if (root.district != -1) {
            districts[root.district][0] += root.population * root.percentD;
            districts[root.district][1] += root.population;
            result[root.district] = districts[root.district][0] / districts[root.district][1];
        }

        Iterator<Precinct> i = g.map.get(root).listIterator();
        while (i.hasNext()) {
            Precinct p = i.next();
            if (!visited[p.code])
                result(p, visited, districts, result);
        }
        return result;
    }
}
