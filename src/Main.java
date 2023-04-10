import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static ArrayList<Request> waitingListFCFS= new ArrayList<>();
    static ArrayList<Request> waitingListSSTF= new ArrayList<>();
    static ArrayList<Request> waitingListSCAN= new ArrayList<>();
    static ArrayList<Request> waitingListCSCAN= new ArrayList<>();
    static ArrayList<Request> waitingListEDF = new ArrayList<>();
    static ArrayList<Request> waitingListFDSCAN = new ArrayList<>();
    static ArrayList<Request> activeListFCFS = new ArrayList<>();
    static ArrayList<Request> activeListSSTF = new ArrayList<>();
    static ArrayList<Request> activeListSCAN = new ArrayList<>();
    static ArrayList<Request> activeListCSCAN = new ArrayList<>();
    static ArrayList<Request> activeListEDF = new ArrayList<>();
    static ArrayList<Request> activeListFDSCAN = new ArrayList<>();
    static ArrayList<Request> doneListFCFS = new ArrayList<>();
    static ArrayList<Request> doneListSSTF = new ArrayList<>();
    static ArrayList<Request> doneListSCAN = new ArrayList<>();
    static ArrayList<Request> doneListCSCAN = new ArrayList<>();
    static ArrayList<Request> doneListEDF = new ArrayList<>();
    static ArrayList<Request> doneListFDSCAN = new ArrayList<>();
    static ArrayList<SimulationResult> resultsFCFS = new ArrayList<>();
    static ArrayList<SimulationResult> resultsSSTF = new ArrayList<>();
    static ArrayList<SimulationResult> resultsSCAN = new ArrayList<>();
    static ArrayList<SimulationResult> resultsCSCAN = new ArrayList<>();
    static ArrayList<SimulationResult> resultsEDF = new ArrayList<>();
    static ArrayList<SimulationResult> resultsFDSCAN = new ArrayList<>();


    static int requestsNumber = 500;
    static int realTimeProbability = 5; // value in percent (1-100)
    static int runAmount = 10;  //How many times to run the simulation
    static int discSize = requestsNumber*5; // Concentration of requests per disc space
    static int discHeadPosition = ThreadLocalRandom.current().nextInt(0, discSize);
    public static void main(String[] args) {
        int realTimeRequestsNumber = 0;
        for (int i = 0; i < runAmount; i++) {
            RequestGenerator();
            for(Request req: waitingListFCFS) {if(req.realTime){realTimeRequestsNumber++;}}
            resultsFCFS.add(FCFS());
            resultsSSTF.add(SSTF());
            resultsSCAN.add(SCAN());
            resultsCSCAN.add(CSCAN());
            resultsEDF.add(EDF());
            resultsFDSCAN.add(FDSCAN());
        }
        System.out.println("Wszystkie dane to średnie z "+runAmount+" symulacji");
        System.out.println("Liczba przemieszczeń głowicy dysku:");
        System.out.println("FCFS:  .    .  "+averageMovements(resultsFCFS));
        System.out.println("SSTF:  .    .  "+averageMovements(resultsSSTF)+" Relatywnie: "+((averageMovements(resultsSSTF)*1000)/averageMovements(resultsFCFS))+"‰");
        System.out.println("SCAN:  .    .  "+averageMovements(resultsSCAN)+" Relatywnie: "+((averageMovements(resultsSCAN)*1000)/averageMovements(resultsFCFS))+"‰");
        System.out.println("C-SCAN:  .   . "+averageMovements(resultsCSCAN)+" Relatywnie: "+((averageMovements(resultsCSCAN)*1000)/averageMovements(resultsFCFS))+"‰");
        System.out.println("SSTF(EDF):  .  "+averageMovements(resultsEDF)+" Relatywnie: "+((averageMovements(resultsEDF)*1000)/averageMovements(resultsFCFS))+"‰");
        System.out.println("SSTF(FD-SCAN): "+ averageMovements(resultsFDSCAN)+" Relatywnie: "+((averageMovements(resultsFDSCAN)*1000)/averageMovements(resultsFCFS))+"‰");
        System.out.println("Liczba żądań czasu rzeczywistego: "+realTimeRequestsNumber);
        System.out.println("Pominięte/niezrealizowane żądania czasu rzeczywistego SSTF(EDF): "+averageSkipped(resultsEDF));
        System.out.println("Pominięte/niezrealizowane żądania czasu rzeczywistego SSTF(FD-SCAN): "+averageSkipped(resultsFDSCAN));

    }
    public static int averageMovements(ArrayList<SimulationResult> resultList) {
        int totalMovements = 0;
        for (SimulationResult result : resultList) {
            totalMovements += result.HeadMovements;
        }
        return totalMovements / resultList.size();
    }
    public static int averageSkipped(ArrayList<SimulationResult> resultList) {
        int totalSkipped = 0;
        for (SimulationResult result : resultList) {
            totalSkipped += result.skippedRealTime;
        }
        return totalSkipped / resultList.size();
    }

    public static int getDistance(Request req) { return Math.abs(req.blockNumber - discHeadPosition); }

    public static void RequestGenerator() {
        if (!waitingListFCFS.isEmpty()|| !waitingListSSTF.isEmpty() || !waitingListSCAN.isEmpty() || !activeListFCFS.isEmpty() || !activeListSSTF.isEmpty() || !activeListSCAN.isEmpty() || !doneListFCFS.isEmpty() || !doneListSSTF.isEmpty() || !doneListSCAN.isEmpty() || !waitingListEDF.isEmpty() || !waitingListFDSCAN.isEmpty() || !activeListEDF.isEmpty() || !activeListFDSCAN.isEmpty() || !doneListEDF.isEmpty() || !doneListFDSCAN.isEmpty()) {
            waitingListFCFS.clear(); waitingListSSTF.clear(); waitingListSCAN.clear(); waitingListCSCAN.clear();
            activeListFCFS.clear(); activeListSSTF.clear(); activeListSCAN.clear(); activeListCSCAN.clear();
            waitingListEDF.clear(); waitingListFDSCAN.clear(); activeListEDF.clear(); activeListFDSCAN.clear();
        } // Above lists should be empty, but better safe than sorry
        doneListFCFS.clear();
        doneListSSTF.clear();
        doneListSCAN.clear();
        doneListCSCAN.clear();
        doneListEDF.clear();
        doneListFDSCAN.clear();


        for (int i = 0; i < requestsNumber; i++) {
            int randomSeed = ThreadLocalRandom.current().nextInt(0,101);
            boolean realTime = randomSeed<realTimeProbability;                      // Chance of a process being real-time

            randomSeed = ThreadLocalRandom.current().nextInt(0,101);   // Reset the random seed after each use
            int deadline = ThreadLocalRandom.current().nextInt(discSize/2, discSize);              //~40% for medium deadline
            if (randomSeed<20) {deadline = ThreadLocalRandom.current().nextInt(10, discSize/2);}      //~20% for very short deadline
            else if (randomSeed>60) {deadline = ThreadLocalRandom.current().nextInt(discSize, discSize*2);} //~40% for very long deadline
            if(!realTime) {deadline = discSize*10;} // for non-real-time requests, deadline is "infinite", its ignored anyway

            int blockNumber = ThreadLocalRandom.current().nextInt(1, discSize);
            int arrivalTime = ThreadLocalRandom.current().nextInt(0, discSize*10);

            waitingListFCFS.add(new Request(realTime, deadline, blockNumber, arrivalTime));
            waitingListSSTF.add(new Request(realTime, deadline, blockNumber, arrivalTime));
            waitingListSCAN.add(new Request(realTime, deadline, blockNumber, arrivalTime));
            waitingListCSCAN.add(new Request(realTime, deadline, blockNumber, arrivalTime));
            waitingListEDF.add(new Request(realTime, deadline, blockNumber, arrivalTime));
            waitingListFDSCAN.add(new Request(realTime, deadline, blockNumber, arrivalTime));
        }
    }

    public static SimulationResult FCFS() {
        int headMovements = 0;
        ArrivalTimeComparator arrivalTimeComparator = new ArrivalTimeComparator();
        while (!waitingListFCFS.isEmpty() || !activeListFCFS.isEmpty()) {
            for (Request request: waitingListFCFS){
                if (request.arrivalTime <= headMovements) {activeListFCFS.add(request);}
            }
            if (activeListFCFS.isEmpty()) {
                waitingListFCFS.sort(arrivalTimeComparator);
                activeListFCFS.add(waitingListFCFS.get(0)); // Waiting time is not counted anyway, so im skipping to the point when the request would be activated.
            }
            waitingListFCFS.removeAll(activeListFCFS);
            activeListFCFS.sort(arrivalTimeComparator);
            Request processedRequest = activeListFCFS.get(0);
            while (!(processedRequest.blockNumber == discHeadPosition)) {
                discHeadPosition += Integer.compare(processedRequest.blockNumber,discHeadPosition); //move disc head in the right direction until it reaches the destination block
                headMovements++;
            }
            doneListFCFS.add(processedRequest);
            activeListFCFS.removeAll(doneListFCFS);

        }
        return new SimulationResult(headMovements);
    }

    public static SimulationResult SSTF() {
        int headMovements = 0;
        DistanceComparator distanceComparator = new DistanceComparator();
        ArrivalTimeComparator arrivalTimeComparator = new ArrivalTimeComparator();
        while(!waitingListSSTF.isEmpty() || !activeListSSTF.isEmpty()) {
            for (Request request: waitingListSSTF) {
                if (request.arrivalTime <= headMovements) {activeListSSTF.add(request);}
            }
            if (activeListSSTF.isEmpty()) {
                waitingListSSTF.sort(arrivalTimeComparator);
                activeListSSTF.add(waitingListSSTF.get(0));
            }
            waitingListSSTF.removeAll(activeListSSTF);
            activeListSSTF.sort(distanceComparator);
            Request processedRequest = activeListSSTF.get(0);
            while (!(processedRequest.blockNumber == discHeadPosition)) {
                discHeadPosition += Integer.compare(processedRequest.blockNumber,discHeadPosition);
                headMovements++;
            }
            doneListSSTF.add(processedRequest);
            activeListSSTF.removeAll(doneListSSTF);

        }
        return new SimulationResult(headMovements);
    }

    public static SimulationResult SCAN() {
        int headMovements = 0;
        discHeadPosition = 0; // Disc head starts from the beginning of the disc in SCAN
        int direction = 1; // 1=right, -1=left

        while(!waitingListSCAN.isEmpty() || !activeListSCAN.isEmpty() ) {
            for (Request request: waitingListSCAN) {
                if (request.arrivalTime <= headMovements) {activeListSCAN.add(request);}
            }
            if (activeListSCAN.isEmpty()) {
                waitingListSCAN.sort(new ArrivalTimeComparator());
                activeListSCAN.add(waitingListSCAN.get(0)); // The head waits without moving until a request appears
            }
            waitingListSCAN.removeAll(activeListSCAN);
            discHeadPosition += direction;
            headMovements++;
            for (Request request: activeListSCAN) {
                if (request.blockNumber == discHeadPosition) {
                    doneListSCAN.add(request);
                }
            }
            activeListSCAN.removeAll(doneListSCAN);
            if (discHeadPosition>=discSize) {direction = -1;}   //Start moving to the left if you reach the right end
            if (discHeadPosition<=0) {direction = 1;}           //Start moving to the right if you reach the left end
        }


        return new SimulationResult(headMovements);
    }

    public static SimulationResult CSCAN() {
        int headMovements = 0;
        discHeadPosition = 0; // Disc head starts from the beginning of the disc in CSCAN


        while(!waitingListCSCAN.isEmpty() || !activeListCSCAN.isEmpty() ) {
            for (Request request: waitingListCSCAN) {
                if (request.arrivalTime <= headMovements) {activeListCSCAN.add(request);}
            }
            if (activeListCSCAN.isEmpty()) {
                waitingListCSCAN.sort(new ArrivalTimeComparator());
                activeListCSCAN.add(waitingListCSCAN.get(0)); // The head waits without moving until a request appears
            }
            waitingListCSCAN.removeAll(activeListCSCAN);
            discHeadPosition++; //Always moving right
            headMovements++;
            for (Request request: activeListCSCAN) {
                if (request.blockNumber == discHeadPosition) {
                    doneListCSCAN.add(request);
                }
            }
            activeListCSCAN.removeAll(doneListCSCAN);
            if (discHeadPosition>=discSize) {discHeadPosition=0; headMovements++;}   //Reset position to the beginning of the drive quickly (assume time complexity of single read)

        }


        return new SimulationResult(headMovements);
    }

    public static SimulationResult EDF() {
        int headMovements = 0;
        int skippedRealTimeRequests = 0;
        boolean realTimeExist;

        while(!waitingListEDF.isEmpty() || !activeListEDF.isEmpty()) {
            for (Request request: waitingListEDF) {
                if (request.arrivalTime <= headMovements) {activeListEDF.add(request);}
            }
            if (activeListEDF.isEmpty()) {
                waitingListEDF.sort(new ArrivalTimeComparator());
                activeListEDF.add(waitingListEDF.get(0));
            }
            waitingListEDF.removeAll(activeListEDF);

            realTimeExist = false;
            for (Request request: activeListEDF) { if (request.realTime) {
                if(request.deadline<=0) {doneListEDF.add(request);
                    skippedRealTimeRequests++;}
                else {realTimeExist = true; }
            } }
            activeListEDF.removeAll(doneListEDF); // Delete all skipped requests from active
            if (activeListEDF.isEmpty()) {continue;}
            if (realTimeExist) {activeListEDF.sort(new DeadlineComparator());}
            else {activeListEDF.sort(new DistanceComparator());}
            Request processedRequest = activeListEDF.get(0);

            while (!(processedRequest.blockNumber == discHeadPosition)) {
                discHeadPosition += Integer.compare(processedRequest.blockNumber,discHeadPosition);
                headMovements++;
                for (Request request: activeListEDF) { if (request.realTime) { request.deadline--; } }
                if(processedRequest.deadline <= 0) {
                    skippedRealTimeRequests++;break;} //Will never happen unless the processed request is real-time
            }
            doneListEDF.add(processedRequest); //Request is considered done even if it was skipped due to deadline. But skipped requests are counted
            activeListEDF.removeAll(doneListEDF);

        }
        return new SimulationResult(headMovements,skippedRealTimeRequests);
    }

    public static SimulationResult FDSCAN() {
        int headMovements = 0;
        int skippedRealTimeRequests = 0;
        boolean realTimeExist;

        while(!waitingListFDSCAN.isEmpty() || !activeListFDSCAN.isEmpty()) {
            for (Request request: waitingListFDSCAN) {
                if (request.arrivalTime <= headMovements) {activeListFDSCAN.add(request);}
            }
            if (activeListFDSCAN.isEmpty()) {
                waitingListFDSCAN.sort(new ArrivalTimeComparator());
                activeListFDSCAN.add(waitingListFDSCAN.get(0));
            }
            waitingListFDSCAN.removeAll(activeListFDSCAN);

            realTimeExist = false;
            for (Request request: activeListFDSCAN) { if (request.realTime) {
                if(getDistance(request)<=request.deadline) { realTimeExist = true; } // if request is doable enter FD-SCAN mode
                else {
                    skippedRealTimeRequests++; doneListFDSCAN.add(request);}       // skip impossible requests
            } }
            activeListFDSCAN.removeAll(doneListFDSCAN); // delete requests that were skipped from active

            if (activeListFDSCAN.isEmpty()) {continue;}
            if (realTimeExist) {activeListFDSCAN.sort(new DeadlineComparator());}
            else {activeListFDSCAN.sort(new DistanceComparator());}
            Request processedRequest = activeListFDSCAN.get(0);

            while (!(processedRequest.blockNumber == discHeadPosition)) {
                discHeadPosition += Integer.compare(processedRequest.blockNumber,discHeadPosition);
                headMovements++;

                for (Request request: activeListFDSCAN) {
                    if (request.blockNumber == discHeadPosition) {doneListFDSCAN.add(request);} // if we happen to pass over a request, complete it
                    if (request.realTime) { request.deadline--; }
                }
                if(processedRequest.deadline <= 0) {skippedRealTimeRequests++;break;} //Will never happen unless the processed request is real-time
            }
            doneListFDSCAN.add(processedRequest); //Request is considered done even if it was skipped due to deadline. But skipped requests are counted
            activeListFDSCAN.removeAll(doneListFDSCAN);

        }
        return new SimulationResult(headMovements,skippedRealTimeRequests);
    }




}