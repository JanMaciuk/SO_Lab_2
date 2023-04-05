import java.util.Comparator;

public class DeadlineComparator implements Comparator<Request> {
    public int compare(Request req1, Request req2) {
        return Integer.compare(req1.deadline, req2.deadline);
    }
}
