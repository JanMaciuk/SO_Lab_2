import java.util.Comparator;

public class DistanceComparator implements Comparator<Request> {
    public int compare(Request req1, Request req2) { // Distance between the disc header and the request target block
        return Integer.compare(Main.getDistance(req1), Main.getDistance(req2));

    }

}
