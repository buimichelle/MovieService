import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Multithreading {
    public static void main(String[] args) throws IOException {

        Main mainCall = new Main();
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(new QueryWorker(mainCall, "insertActors"));
        executor.execute(new QueryWorker(mainCall, "insertMovies"));

        executor.shutdown();
        while (!executor.isTerminated()) {}

        executor = Executors.newFixedThreadPool(3);

        executor.execute(new QueryWorker(mainCall, "insertCast"));


        executor.shutdown();
        while (!executor.isTerminated()) {}

        mainCall.writeToFile();
    }

    static class QueryWorker implements Runnable {

        private Main mainCall;
        private String functionToRun;
        QueryWorker(Main mainObj, String function) {
            this.mainCall = mainObj;
            this.functionToRun = function;
        }

        @Override
        public void run() {
            switch (functionToRun) {
                case "insertActors":
                    try {
                        mainCall.insertActors();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "insertMovies":
                    try {
                        mainCall.insertMovies();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "insertCast":
                    try {
                        mainCall.insertCast();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    System.out.println("Invalid function name!");
            }
//                Thread.sleep(10);
        }
    }
}

