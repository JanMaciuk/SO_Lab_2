import java.util.Comparator;

public class ArrivalTimeComparator implements Comparator<Request> {
    public int compare(Request req1, Request req2) { //Komparator po czasie przybycia
        return Integer.compare(req1.arrivalTime, req2.arrivalTime);
    }
}

