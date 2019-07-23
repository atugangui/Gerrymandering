/** Interface for an algorithm to divide an Electorate into districts so as to favor one party. */
public interface Gerrymanderer {

    /**
     * Given an electorate, returns an array of districts, each of which is an array of the IDs of voters in that
     * district. Each voter must appear exactly once, all districts must be the same size, and each district must
     * be contiguous.
     */
    int[][] gerrymander(Electorate electorate, boolean party);

}
