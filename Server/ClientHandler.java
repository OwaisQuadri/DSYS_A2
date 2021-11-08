
import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ClientHandler extends UnicastRemoteObject implements ClassInterface {
    private String studentName;
    static String test = "";
    static String pw, startDT, endDT;
    static int numOfTakes;
    static ArrayList<ArrayList<String>> currQuestions = new ArrayList<>();
    boolean startTest = false;

    // Constructor
    public ClientHandler(String s) throws RemoteException {
        super();
    }

    private static boolean loadTest(String name) {
        // re-init loaded questions
        currQuestions = new ArrayList<>();
        String path = "../Content/" + name + ".txt";
        try {
            File file = new File(path);
            Scanner r = new Scanner(file);
            test = file.getName().substring(0, file.getName().length() - 4);
            // password
            pw = r.nextLine();
            // number of takes
            String tempString;
            if ((tempString = r.nextLine()).equals("")) {
                numOfTakes = -1;
            } else {
                numOfTakes = Integer.parseInt(tempString);
            }

            // start datetime
            startDT = r.nextLine();
            // end datetime
            endDT = r.nextLine();
            while (r.hasNextLine()) {
                // read each question
                ArrayList<String> temp = new ArrayList<>();
                temp.add(r.nextLine());
                int numOfA = Integer.parseInt(r.nextLine());
                for (int i = 0; i < numOfA; i++) {
                    temp.add(r.nextLine());
                }
                currQuestions.add(temp);
            }
            r.close();
            return true;
        } catch (Exception e) {
            System.out.println("loadtest: This test does not exist.\n try again");
            e.printStackTrace();
            return false;
        }
    }

    public int getTimesTaken(String testName, String name) {
        try {
            String path = "../Content/" + testName + "_results.txt";
            File file = new File(path);
            Scanner r = new Scanner(file);
            int count = 0;
            while (r.hasNextLine()) {
                String curStudent = r.nextLine().split(" ")[0];
                if (curStudent.equals(name)) {
                    count++;
                }
            }
            r.close();
            return count;

        } catch (Exception e) {
            System.out.println("takentest: nobody has taken this test before");
            e.printStackTrace();
            return 0;
        }
    }

    public int takeTest(String name, String testName, String password) {
        startTest = false;
        if (loadTest(testName)) {

            // check if user can even run the test
            boolean validDT;
            // is it within the time frame?
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date start = dateFormat.parse(startDT);
                Date end = dateFormat.parse(endDT);
                Date now = new Date();
                validDT = (now.before(end)) && (now.after(start));
            } catch (Exception e) {
                // no limit
                validDT = true;
            }
            //
            // have they taken the test before and do they have the right password
            boolean validPW = password.equals(pw);
            if (validPW) {
                if (validDT) {
                    int tries = getTimesTaken(testName, name);
                    if (tries == numOfTakes) {
                        System.out.println(name + " has already taken this test " + numOfTakes + " time(s).");
                        return -1;
                    } else {
                        System.out.println("Test " + testName + ": " + name + " Attempt " + (tries + 1) + "/"
                                + (numOfTakes > 0 ? numOfTakes : "infinity"));
                    }
                } else {
                    // not within timeframe
                    return -2;
                }
            } else {
                return -3;
            }
            // run the test
            startTest = true;
            return 1;

        } else {
            // return an error
            System.out.println("taketest: test dne");
            return 0;
        }

    }

    public String testList() {
        String list = "";
        File availFileFolder = new File("../Content");
        File[] listOfFiles = availFileFolder.listFiles();
        String prevFile = "";
        for (File file : listOfFiles) {
            String currFile = file.getName().substring(0, file.getName().length() - 4).split("_")[0];
            if (!currFile.equals(prevFile)) {
                list += "\t"+currFile + "\n";
            }
            prevFile = currFile;
        }
        return list;
    }

    public void uploadResult(String student, double result, String testName) {
        // save result
        try (FileWriter fw = new FileWriter("../Content/" + testName + "_results.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(student + " " + result);
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            // exception handling left as an exercise for the reader
            e.printStackTrace();
        }
    }

    public String studentStats(String student) {
        // read test stat files and take note of students grades, average, low and high
        // marks
        DecimalFormat percent = new DecimalFormat("##0.00 %");
        String result = "";
        for (String currTest : testList().split("\n")) {
            if (currTest.equals("")) {

            } else {
                result += "\nTest: " + currTest + "\n";
                try {
                    File file = new File("../Content/" + currTest + "_results.txt");
                    Scanner r = new Scanner(file);
                    double sum = 0;
                    int count = 0;
                    double low = 2;
                    double high = -1;
                    double studentScore = -1;
                    while (r.hasNextLine()) {
                        String line = r.nextLine();
                        String currStud = line.split(" ")[0];
                        double entry = Double.parseDouble(line.split(" ")[1]);
                        // sum and count for average
                        sum += entry;
                        // set as studentscore if currstud=student and if entry>studentScore
                        if ((currStud.equals(student)) && (entry > studentScore)) {
                            studentScore = entry;
                        }
                        count++;
                        // check for new low
                        if (entry < low) {
                            low = entry;
                        }
                        // check for new high
                        if (entry > high) {
                            high = entry;
                        }
                    }
                    if (low == 2) {
                        result += "There are no statistics for this test\n\n";
                    } else {
                        double avg = sum / count;
                        result += student + "'s Score: "
                                + (studentScore != -1 ? percent.format(studentScore) : "test not taken yet") + "\n\n";
                        result += "Average Class Score: " + percent.format(avg) + "\n";
                        result += "Highest Class Score: " + percent.format(high) + "\n";
                        result += "Lowest Class Score: " + percent.format(low) + "\n\n";

                    }
                    r.close();
                } catch (Exception e) {
                    result += "There is no statistics for this test\n\n";
                    e.printStackTrace();
                }
            }
        }
        return result;

    }

    public ArrayList<ArrayList<String>> getQuestions() {
        return currQuestions;
    }
}
