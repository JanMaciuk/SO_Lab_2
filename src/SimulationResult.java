public class SimulationResult {
    int HeadMovements;
    int skippedRealTime;

    public SimulationResult(int HeadMovements) {
        this.HeadMovements = HeadMovements;
        this.skippedRealTime = -1; // not applicable for algorithms without real-time requests
    }
    public SimulationResult(int HeadMovements, int skippedRealTime) {
        this.HeadMovements = HeadMovements;
        this.skippedRealTime = skippedRealTime;// for algorithms with real-time requests
    }
}
