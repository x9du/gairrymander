package gairrymander;

import java.util.*;
import java.io.*;

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
    
    // pre: each line in the csv file contains three ints separated by commas. 
    // 		these ints represent, in order:
    //		the precinct code, the number of dem votes, the number of gop votes
    // post: returns an array of precincts representing the data in the file
    public static Precinct[] fromFile(File file) throws FileNotFoundException {
    	List<String> linesInFile = new ArrayList<String>();
    	scannerToList(linesInFile, new Scanner(file));
    	Precinct[] result = new Precinct[linesInFile.size()];
    	for (int i = 0; i < result.length; i++) {
    		String temp = linesInFile.get(i).replace(',', ' ').trim();
    		result[i] = fromFileIndividual(new Scanner(temp));
    	}
    	return result;
    }
    
    // adds each line in scanner to a list of strings
    private static void scannerToList(List<String> list, Scanner scanner) {
    	list.add(scanner.nextLine().substring(3));
    	while (scanner.hasNextLine()) {
			list.add(scanner.nextLine());
		}
    }
    
    // pre: scanner contains three int tokens representing, in order:
    // 		the precinct code, the number of dem votes, the number of gop votes
    // post: returns a precinct with these values
    private static Precinct fromFileIndividual(Scanner scanner) {
    	int code = scanner.nextInt();
    	int demVotes = scanner.nextInt();
    	double demVotesDouble = demVotes;
    	int pop = demVotes + scanner.nextInt();
    	double popDouble = pop;
    	return new Precinct(code, pop, demVotesDouble / popDouble);
    }
}
