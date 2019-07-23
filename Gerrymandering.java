import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdOut;

public class Gerrymandering implements Gerrymanderer {

    public static int[] avoidArticulation(Graph graph, int vertex, int d, int[] notOutOfBounds, boolean[] explored) {
        Biconnected articulationPoint = new Biconnected(graph); // uses a separate Biconnected class to find articulation points
        int notArticulation[] = new int[4]; // this array has 4 bins for the 4 neighbors based on their direction from vertex
        for (int i = 0; i < 4; i++) {
            if (!explored[notOutOfBounds[i]]) { // checks if this neighbor has been explored yet
                if (!articulationPoint.isArticulation(notOutOfBounds[i])) { // checks whether neighbors are articulation points
                    notArticulation[i] = notOutOfBounds[i]; //if not an articulation point, this neighbor is saved into an array of potential district members
                }
            }
        }
        return notArticulation; // returns array of in bounds vertices that are not articulation points
    }

    public static int[] outOfBounds(int vertex, int[] direction, int d) {
        int inBounds[] = new int[4]; // stores neighbors not out of bounds
        if (direction[0] >= 0) { // if there is a left neighbor
            inBounds[0] = direction[0];
        } if (direction[1] < d * d) { // if there is a right neighbor
            inBounds[1] = direction[1];
        } if (vertex % d != d - 1) { // if there is an up neighbor
            inBounds[2] = direction[2];
        } if (vertex % d != 0) { // if there is a down neighbor
            inBounds[3] = direction[3];
        }
        return inBounds; // returns array of vertex's neighbors that are not out of bounds
    }

    public static boolean isPrefParty(boolean party, int currentPoint, boolean[] voters) { // checks whether the current neighbor is of the preferred party
        if (voters[currentPoint] == party) {
            return true;
        }
        return false;
    }

    public static int chooseNextVertex(int vertex, int d, boolean[] explored){ // chooses new starting vertex after a district is completed
        for (int i = 0; i < d * d; i++) {
            if (explored[i]){
                vertex++;
            }
        }
        return vertex;
    }

    public static boolean changeParty(int majorityCount, boolean party, int d, boolean prefParty){ // changes the preferred party if we have already reached a majority
        if (majorityCount == (d + 1)/2){
            prefParty = !party;
        }
        return prefParty;
    }

    public int[][] gerrymander(Electorate e, boolean party) {
        Graph graph = e.getGraph(); // saves graph from electorate
        int d = e.getNumberOfDistricts(); // stores number of districts and voters per district into variable d
        int districts[][] = new int[d][d]; // we will fill this with voters and return it in the end
        boolean voters[] = e.getVoters(); // gets the party of each voter in the graph from electorate
        boolean explored[] = new boolean[d * d]; // array of vertices already a part of a district
        explored[0] = true; // adds first vertex to explored since we start there
        int voterCount = 0; // counts the number of preferred party voters within district
        int majorityCount = 0; // counts the number of voters from our preferred party added to a district
        int majorityDistCount = 0; // counts the number of districts dominated by our preferred party
        boolean prefParty = party; // stores our preferred party based on a given preference
        int vertex = 0; //always start at first vertex
        districts[0][0] = 0; // initializes first vertex in the first district so that we always start from here
        int distCount = 0; // initializes the district counter for districts[distCount][voterCount]
        while (distCount < d) { // creates 'd' districts
            voterCount = 0; // resets indices of voters for each district
            changeParty(majorityDistCount, party, d, prefParty); // ensures we are also adding voters from the opposing party to our districts
            while (voterCount < d) { // allows us to add 'd' voters per district
                int left = vertex - d; // sets left, right, up, and down neighbors of vertex
                int right = vertex + d;
                int up = vertex + 1;
                int down = vertex - 1;
                int direction[] = new int[4]; // allows us to easily access neighbors based on direction from vertex
                direction[0] = left;
                direction[1] = right;
                direction[2] = up;
                direction[3] = down;
                changeParty(majorityCount, party, d, prefParty); // ensures we are keeping some districts dominated by the opposing party
                int[] notOutOfBounds = outOfBounds(vertex, direction, d); // returns array of neighbors that are not out of bounds
                int[] potentialPoints = avoidArticulation(graph, vertex, d, notOutOfBounds, explored); // returns array of vertices that are not out of bounds and not articulation points
                for (int i = 0; i < 4; i++) {
                    if (potentialPoints[i] != 0) { // to avoid any empty bins
                        int currentPoint = potentialPoints[i];
                        boolean ourParty = isPrefParty(party, currentPoint, voters); // returns true if same party, false if not
                        if (ourParty) { // if the neighboring vertex we are considering belongs to our preferred party, we will add that vertex
                            districts[distCount][voterCount] = currentPoint;
                            vertex = currentPoint; // make the neighbor we are adding to the district the new vertex
                            explored[currentPoint] = true; // mark this newly added vertex as explored
                            majorityCount++; // increment this to indicate we have added a vertex of our preferred party
                            break;
                        }
                    }
                }
                if (districts[distCount][voterCount] == 0){ // checks if a voter of the preferred party was added
                    if (distCount == 0 && voterCount == 0){ // avoids counting voter 0 as a not-added voter
                        break;
                    }
                    for (int i = 0; i < 4; i++) {
                        if (potentialPoints[i] != 0){ // looks for the first potential voter we can add
                            districts[distCount][voterCount] = potentialPoints[i]; // add this opposing party point
                            vertex = potentialPoints[i]; // this newly added neighbor becomes the new vertex
                            explored[potentialPoints[i]] = true; // mark it as explored
                            break;
                        }
                    }
                }
                voterCount++; // moves to next available spot in district for next vertex
            }
            majorityDistCount++;
            distCount++;
            chooseNextVertex(vertex, d, explored); // chooses a new unexplored vertex as a starting point for a new district, once a district is filled
        }
        return districts;
    }
}
