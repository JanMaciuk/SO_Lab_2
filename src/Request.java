public class Request {
    protected boolean realTime;
    int deadline;
    int blockNumber;
    int arrivalTime;
    public Request(boolean realTime, int deadline, int blockNumber, int arrivalTime) {
        this.realTime = realTime;
        this.deadline = deadline;
        this.blockNumber = blockNumber;
        this.arrivalTime = arrivalTime;
    }


}
