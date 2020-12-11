package gairrymander;

import java.util.*;
import java.io.*;

public class Gerrymanderer {
    public int population;
    public int numPrecincts;
    public int numDistricts;
    public double error; // max error between actual and average district population
    public Graph<Precinct> g;
    public Precinct[] precincts;
    public Map<Arguments, Set<District>> subSolutions;

    public static void main(String[] args) throws FileNotFoundException {
        // cindyTest();
        test(36, 5);
        // test(36, 5); // can run on 36, can't on 40
        // Precinct[] precincts = fromFile(new File("gairrymander\\oregon_data.csv"));
        // Gerrymanderer gerry = new Gerrymanderer(population(precincts), precincts.length, 5, precincts);
        // System.out.println(Arrays.toString(precincts));
        // gerry.rect();
        // System.out.println("average district population " + gerry.population / gerry.numDistricts);
        // System.out.println(gerry.gerrymander(gerry.population / gerry.numDistricts, true));
    }
    
    private static void test(int numPrecincts, int numDistricts) {
        Random rand = new Random();
        Precinct[] precincts = new Precinct[numPrecincts];
        int population = 0;
        for (int i = 0; i < precincts.length; i++) {
            int precinctPop = /*100*/rand.nextInt(1000);
            precincts[i] = new Precinct(i, precinctPop, rand.nextDouble());
            population += precinctPop;
        }
        Gerrymanderer gerry = new Gerrymanderer(population, precincts.length, numDistricts, precincts);
        gerry.rect();
        // System.out.println(gerry.g);
        System.out.println("average district population " + population / numDistricts);
        Set<District> districts = gerry.gerrymander(population / numDistricts, true);
        for (District d : districts) {
            System.out.print(population(d.precincts) + " ");
            System.out.println(d.population);
        }
        System.out.println(districts.size() + " districts");
        gerry.labelPrecincts(districts);
        // System.out.println(districts);
        System.out.println(gerry.toJSON());
        // System.out.println(gerry.subSolutions);
    }

    // pre: numPrecincts >= numDistricts, precincts array initialized
    // post: Constructs rectangular graph of cols numPrecincts / numDistricts, min rows numDistricts
    // extra row for leftover
    public void rect() {
        if (population == -1) {
            population = population(precincts);
        }
        int n = numPrecincts / numDistricts;
        double lat1 = 46; double lng1 = -124;
        double lat2 = 42; double lng2 = -117;
        double lat = Math.abs(lat1 - lat2) / numDistricts;
        if (numDistricts * n < numPrecincts) {
            lat++;
        }
        double lng = Math.abs(lng1 - lng2) / n;
        for (int i = 0; i < precincts.length; i++) {
            precincts[i].x = lat1 - lat * (i / n);
            precincts[i].y = lng1 + lng * (i % n);
            List<Precinct> adj = new LinkedList<>();
            int l = i - 1;
            if (i % n != 0) adj.add(precincts[l]);
            int r = i + 1;
            if (r % n != 0 && r < precincts.length) adj.add(precincts[r]);
            int t = i - n;
            if (t >= 0) adj.add(precincts[t]);
            int b = i + n;
            if (b < precincts.length) adj.add(precincts[b]);
            g.addVertex(precincts[i], adj);
        }
    }

    public static int population(Precinct[] precincts) {
        int population = 0;
        for (Precinct p : precincts) {
            population += p.population;
        }
        return population;
    }

    public static int population(Set<Precinct> precincts) {
        int population = 0;
        for (Precinct p : precincts) {
            population += p.population;
        }
        return population;
    }

    private static void cindyTest() {
        Random rand = new Random();
        Precinct[] precincts = new Precinct[6];
        int population = 0;
        int numDistricts = 2;
        for (int i = 0; i < 6; i++) {
            int precinctPop = 100/*rand.nextInt(1000)*/;
            precincts[i] = new Precinct(i, precinctPop, rand.nextDouble());
            population += precinctPop;
        }

        Gerrymanderer gerry = new Gerrymanderer(population, precincts.length, numDistricts, precincts);
        gerry.g.addVertex(precincts[0], Arrays.asList(precincts[1], precincts[2], precincts[3]));
        gerry.g.addVertex(precincts[1], Arrays.asList(precincts[0], precincts[3]));
        gerry.g.addVertex(precincts[2], Arrays.asList(precincts[0], precincts[3], precincts[5]));
        gerry.g.addVertex(precincts[3], Arrays.asList(precincts[0], precincts[1], precincts[2], precincts[4]));
        gerry.g.addVertex(precincts[4], Arrays.asList(precincts[3], precincts[5]));
        gerry.g.addVertex(precincts[5], Arrays.asList(precincts[2], precincts[4]));
        System.out.println("average district population " + population / numDistricts);
        // System.out.println(gerry.g);
        // gerry.generateAllDistricts(population / numDistricts);
        Set<District> districts = gerry.gerrymander(population / numDistricts, true);
        // System.out.println(districts);
        gerry.labelPrecincts(districts);
        // System.out.println(gerry.g.getEdges());
        System.out.println(gerry.toJSON());
        // subSolutions = new HashMap<>();
        // System.out.println(gerry.gerrymander2(population / numDistricts, true));
        // System.out.println(gerry.pack(population / numDistricts, true));
        // System.out.println(gerry.subSolutions);
    }

    public Gerrymanderer(int population, int numPrecincts, int numDistricts, Precinct[] precincts){
        this.population = population;
        this.numPrecincts = numPrecincts;
        this.numDistricts = numDistricts;
        this.error = 0.1 * population / numDistricts; // probably want to change this later
        this.g = new Graph<>();
        this.precincts = precincts;
        this.subSolutions = new HashMap<>();
    }

    public Set<District> pack(int precinctPop, boolean dem) {
        g.sortAdj(dem);
        return pack(g.getRoot(), new HashSet<>(Arrays.asList(precincts)), new District(), precinctPop, dem);
    }

    public Set<District> pack(Precinct root, Set<Precinct> unvisited, District district, int precinctPop, boolean dem) {
        if (precinctPop < 0 - error) {
            return null;
        } else if (precinctPop >= 0 - error/*<= 0*/ || precinctPop <= error) { // If this district full, generate next district
            if (unvisited.size() == 0) {
                Set<District> districts = new HashSet<>();
                districts.add(new District(district));
                return districts;
            }
            Set<District> districts = gerrymander(root, unvisited, new District(), population / numDistricts, dem);
            if (districts != null) {
                districts.add(district);
            }
            return districts;
        }
        unvisited.remove(root);
        district.add(root);
        precinctPop -= root.population;
        Set<Precinct> unvisitedCopy = new HashSet<>(unvisited);
        Set<District> districts = null;
        Iterator<Precinct> i = g.getAdj(root).listIterator();
        boolean noAdjUnvisited = true;
        while (i.hasNext()) {
            Precinct p = i.next();
            if (unvisitedCopy.contains(p)) {
                noAdjUnvisited = false;
                districts = pack(p, unvisitedCopy, new District(district), precinctPop, dem);
                if (districts != null) {
                    return districts;
                }
            }
        }
        if (unvisited.size() == 0) {
            districts = gerrymander(new Precinct(root), unvisited, district, precinctPop - root.population, dem);
        } else if (noAdjUnvisited && precinctPop - root.population >= 0 - error && precinctPop - root.population <= error) {
            Iterator<Precinct> it = unvisited.iterator();
            districts = gerrymander(it.next(), unvisited, new District(), population / numDistricts, dem);
            if (districts != null) {
                districts.add(new District(district));
            }
        }
        return districts;
    }

    public Set<District> gerrymander(int precinctPop, boolean dem) {
        return gerrymander(g.getRoot(), new HashSet<>(Arrays.asList(precincts)), new District(), precinctPop, dem);
    }

    public Set<District> gerrymander(Precinct root, Set<Precinct> unvisited, District district, int precinctPop, boolean dem) {
        // System.out.print(root.code + " ");
        if (precinctPop < 0 - error) {
            // System.out.println(":(");
            return null;
        } else if (precinctPop <= 0 || precinctPop <= error) { // If this district full, generate next district
            // System.out.println(":)");
            // Arguments args = new Arguments(new HashSet<>(unvisited));
            // if (subSolutions.containsKey(args)) {
            //     return subSolutions.get(args);
            // }
            if (unvisited.size() == 0) {
                Set<District> districts = new HashSet<>();
                districts.add(new District(district));
                // System.out.println(districts);
                return districts;
            }
            Set<District> districts = gerrymander(root, unvisited, new District(), population / numDistricts, dem);
            if (districts != null) {
                // subSolutions.put(args, new HashSet<>(districts));
                districts.add(district);
            }// else {
            //     subSolutions.put(args, null);
            // }
            // System.out.println(root.code + " " + districts);
            return districts;
        } else {
            // System.out.println(":|");
            unvisited.remove(root);
            district.add(root);
            Set<District> districts = null;
            Iterator<Precinct> i = g.getAdj(root).listIterator();
            boolean noAdjUnvisited = true;
            while (i.hasNext()) {
                Precinct p = i.next();
                if (unvisited.contains(p)) {
                    noAdjUnvisited = false;
                    Set<District> temp = gerrymander(p, unvisited, new District(district), precinctPop - root.population, dem);
                    // System.out.println(root.code + " " + temp);
                    districts = optimal(districts, temp, dem);
                }
            }
            if (unvisited.size() == 0) {
                districts = gerrymander(new Precinct(root), unvisited, district, precinctPop - root.population, dem);
            } else if (noAdjUnvisited && precinctPop - root.population >= 0 - error && precinctPop - root.population <= error) {
                // Arguments args = new Arguments(new HashSet<>(unvisited));
                Iterator<Precinct> it = unvisited.iterator();
                districts = gerrymander(it.next(), unvisited, new District(), population / numDistricts, dem);
                if (districts != null) {
                    // subSolutions.put(args, new HashSet<>(districts));
                    districts.add(new District(district));
                }// else {
                //     subSolutions.put(args, null);
                // }
            }
            unvisited.add(root);
            district.remove(root);
            // System.out.println(root.code + " " + districts);
            return districts;
        }
    }

    // Attempted refactor of gerrymander method, still buggy
    public Set<District> gerrymander2(int precinctPop, boolean dem) {
        return gerrymander2(g.getRoot(), new HashSet<>(Arrays.asList(precincts)), new District(), precinctPop, dem);
    }

    // Attempted refactor of gerrymander method, still buggy
    public Set<District> gerrymander2(Precinct root, Set<Precinct> unvisited, District district, int precinctPop, boolean dem) {
        Arguments args = new Arguments(unvisited);
        if (subSolutions.containsKey(args)) {
            return subSolutions.get(args);
        }
        System.out.print(root.code + " ");
        if (precinctPop < 0 - error) {
            System.out.println(":(");
            return null;
        }
        System.out.println(":|");
        unvisited.remove(root);
        district.add(root);
        precinctPop = precinctPop - root.population;
        args = new Arguments(new HashSet<>(unvisited));
        Set<District> districts = null;
        if (precinctPop >= 0 - error && precinctPop <= error) { // If this district full, generate next district
            System.out.println(":)");
            if (unvisited.size() == 0) {
                districts = new HashSet<>();
                districts.add(new District(district));
                System.out.println(districts);
                return districts;
            }
            Iterator<Precinct> it = unvisited.iterator();
            districts = gerrymander2(it.next(), unvisited, new District(), population / numDistricts, dem);
            if (districts != null) {
                districts.add(new District(district));
            }
        } else {
            Iterator<Precinct> i = g.getAdj(root).listIterator();
            while (i.hasNext()) {
                Precinct p = i.next();
                if (unvisited.contains(p)) {
                    Set<District> temp = gerrymander2(p, unvisited, new District(district), precinctPop, dem);
                    // System.out.println(root.code + " " + temp);
                    districts = optimal(districts, temp, dem);
                }
            }
        }
        if (districts != null) {
            subSolutions.put(args, new HashSet<>(districts));
        } else {
            subSolutions.put(args, null);
        }
        unvisited.add(root);
        district.remove(root);
        System.out.println(root.code + " " + districts);
        return districts;
    }

    // Return set of districts with most districts won for party, if wins equal then highest sum of %
    // party vote for all the districts the party won. If one null, returns the other. If both null,
    // returns null.
    private Set<District> optimal(Set<District> districts1, Set<District> districts2, boolean dem) {
        if (districts1 == null) {
            return districts2;
        } else if (districts2 == null) {
            return districts1;
        }
        double[] wins1 = wins(districts1, dem);
        double[] wins2 = wins(districts2, dem);
        if (wins1[0] > wins2[0] || (wins1[0] == wins2[0] && wins1[1] >= wins2[1])) {
            return districts1;
        }
        return districts2;
    }

    public void labelPrecincts(Set<District> districts) {
        int n = 0;
        Map<Precinct, Integer> labeledPrecincts = new HashMap<>();
        for (District d : districts) {
            if (n >= numDistricts) break;
            if (Math.abs(d.population - population(d.precincts)) > error) {
                continue;
            }
            for (Precinct p : d.precincts) {
                labeledPrecincts.put(p, n);
            }
            n++;
        }

        for (int i = 0; i < precincts.length; i++) {
            if (labeledPrecincts.containsKey(precincts[i])) {
                precincts[i].district = labeledPrecincts.get(precincts[i]);
            } else {// If precinct unlabeled, assign to district of any adjacent precinct
                // loop through adjacent precincts, assign district. If still -1, random value
                Iterator<Precinct> it = g.getAdj(precincts[i]).listIterator();
                while (it.hasNext()) {
                    Precinct p = it.next();
                    if (p.district != -1) {
                        precincts[i].district = p.district;
                        break;
                    }
                }
                if (precincts[i].district == -1) {
                    precincts[i].district = (int) (Math.random() * numDistricts);
                }
            }
        }
    }

    public String toJSON() {
        StringBuilder sb = new StringBuilder("{ \"nodes\":{ ");
        for (Precinct p : g.map.keySet()) {
            sb.append("\"p");
            sb.append(p.code);
            sb.append("\":");
            sb.append(p.toJSON());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(" }, \"edges\":[");
        for (Set<Precinct> pair : g.getEdges()) {
            sb.append("{ ");
            String str = "from";
            for (Precinct p : pair) {
                sb.append("\"");
                sb.append(str);
                sb.append("\":\"p");
                sb.append(p.code);
                sb.append("\"");
                if (str.length() == 4) {
                    sb.append(", ");
                }
                str = "to";
            }
            sb.append(" }, ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("], \"result\":\"");
        sb.append(districtResults());
        sb.append("\" }");
        return sb.toString();
    }

    private String districtResults() {
        Map<Integer, District> map = new HashMap<>();
        for (Precinct p : precincts) {
            int n = p.district;
            if (!map.containsKey(n)) {
                map.put(n, new District());
            }
            map.get(n).add(p);
        }
        String str = "";
        for (int i : map.keySet()) {
            District d = map.get(i);
            str += i + " ";
            double percentD = 1.0 * d.popD / d.population;
            // System.out.println(percentD);
            if (percentD > 0.5) {
                str += (int) (percentD * 100) + "% D, ";
            } else if (percentD < 0.5) {
                str += (int) ((1 - percentD) * 100) + "% R, ";
            } else {
                str += 50 + "% Tie, ";
            }
        }
        return str;
    }

    private double[] wins(Set<District> districts, boolean dem) {
        double[] wins = new double[2];
        for (District d : districts) {
            if (d.isD() == dem) {
                wins[0]++;
                wins[1] += d.winPercent();
            }
        }
        return wins;
    }
    
    public void generateAllDistricts(int precinctPop) {
        generateAllDistricts(g.getRoot(), new HashSet<>(Arrays.asList(precincts)), 0, precinctPop);
    }

    private void generateAllDistricts(Precinct root, Set<Precinct> unvisited, int nextDistrict, int precinctPop) {
        System.out.print(root.code + " ");
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
    //      these ints represent, in order:
    //      the precinct code, the number of dem votes, the number of gop votes
    // post: returns an array of precincts representing the data in the file
    public static Precinct[] fromFile(File file) throws FileNotFoundException {
        List<String> linesInFile = new ArrayList<String>();
        scannerToList(linesInFile, new Scanner(file));
        Precinct[] result = new Precinct[linesInFile.size()];
        for (int i = 0; i < result.length; i++) {
            String temp = linesInFile.get(i).replace(',', ' ');//.trim();
            System.out.println(temp.trim());
            result[i] = fromFileIndividual(new Scanner(temp.trim()));
        }
        return result;
    }
    
    // adds each line in scanner to a list of strings
    private static void scannerToList(List<String> list, Scanner scanner) {
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }
    }
    
    // pre: scanner contains three int tokens representing, in order:
    //      the precinct code, the number of dem votes, the number of gop votes
    // post: returns a precinct with these values
    private static Precinct fromFileIndividual(Scanner scanner) {
        int code = scanner.nextInt();
        int demVotes = scanner.nextInt();
        double demVotesDouble = demVotes;
        int pop = demVotes + scanner.nextInt();
        double popDouble = pop;
        return new Precinct(code, pop, demVotesDouble / popDouble);
    }

    private class Arguments implements Comparable<Arguments> {
        public final Set<Precinct> unvisited;

        public Arguments(Set<Precinct> unvisited) {
            this.unvisited = unvisited;
        }

        public int compareTo(Arguments other) {
            return unvisited.size() - other.unvisited.size();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o instanceof Arguments)) {
                return false;
            }
            Arguments other = (Arguments) o;
            return unvisited.equals(other.unvisited);
        }

        public int hashCode() {
            return Objects.hash(unvisited);
        }

        public String toString() {
            return unvisited.toString();
        }
    }
}
