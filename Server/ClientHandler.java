
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
    /*
     * 
     * Class ClientHandler created on 2021-11-06 to act as an implementation for
     * "ClassInterface" for students connect to and get their requests for taking
     * tests and viewing results
     * 
     */
    // init vairables
    private static String test = "";
    private static String pw, startDT, endDT;
    private static int numOfTakes;
    private static ArrayList<ArrayList<String>> currQuestions = new ArrayList<>();
    boolean startTest = false;

    // Constructor
    public ClientHandler(String s) throws RemoteException {
        super();
    }

    /*
     * Method name: getQuestions ; accepts: n/a ; returns:
     * ArrayList<ArrayList<String>> ;
     *
     * purpose: provides questions to client system for test taking.
     */
    public ArrayList<ArrayList<String>> getQuestions() {
        return currQuestions;
    }

    /*
     * Method name: getTimesTaken ; accepts: String testName, String name ; returns:
     * int ;
     *
     * purpose: returns the number of times that a certain student has taken a
     * certain test.
     */
    public int getTimesTaken(String testName, String name) {
        try {
            // read current test's results file
            String path = "../Content/" + testName + "_results.txt";
            File file = new File(path);
            Scanner r = new Scanner(file);
            int count = 0;
            // parse student names
            while (r.hasNextLine()) {
                String curStudent = r.nextLine().split(" ")[0];
                if (curStudent.equals(name)) {// if student name shows up, add to count
                    count++;
                }
            }
            // close filereader
            r.close();
            return count;

        } catch (Exception e) {
            // if test doesnt exist or test result file doesnt exist
            System.out.println("takentest: nobody has taken this test before");
            return 0;
        }
    }

    /*
     * Method name: loadTest ; accepts: String name ; returns: boolean ;
     *
     * purpose: helper function for taketest, loads selected test onto the client
     * request handler.
     */
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
            // close filereader
            r.close();
            return true;
        } catch (Exception e) {
            // test dne
            System.out.println("loadtest: This test does not exist.\n try again");
            return false;
        }
    }

    /*
     * Method name: studentStats ; accepts: String student ; returns: String ;
     *
     * purpose: return a certain student's statistics compared to the rest of the
     * class, also shows due date of available tests and availability dates for
     * unavailable tests.
     */
    public String studentStats(String student) {
        // read test stat files and take note of students grades, average, low and high
        // marks
        // init output format
        DecimalFormat percent = new DecimalFormat("##0.00 %");
        // init result string
        String result = "";
        // go through each test
        for (String currTest : testList().split("\n")) {
            if (currTest.equals("")) {
                // there will be an empty line at the end that we can discard
            } else {
                // add testname to output
                result += "\nTest: " + currTest + "\n";
                try {
                    // read both test result and test template
                    File file = new File("../Content/" + currTest + "_results.txt");
                    File file2 = new File("../Content/" + currTest + ".txt");
                    Scanner r = new Scanner(file);
                    Scanner r2 = new Scanner(file2);
                    // first two lines are meaningless in this application
                    r2.nextLine();
                    r2.nextLine();
                    // get the start and end of availability
                    startDT = r2.nextLine();
                    endDT = r2.nextLine();
                    // close reader for template
                    r2.close();
                    // init counters, high and low values as well as the student score
                    double sum = 0;
                    int count = 0;
                    double low = 2;
                    double high = -1;
                    double studentScore = -1;
                    // read file
                    while (r.hasNextLine()) {
                        String line = r.nextLine();
                        String currStud = line.split(" ")[0];// current entry student
                        double entry = Double.parseDouble(line.split(" ")[1]);// current entry
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
                        // if empty file
                        result += "There are no statistics for this test\n\n";
                    } else {
                        double avg = sum / count;
                        // show students highest own score if exists else show that he/she needs to take
                        // test
                        result += student + "'s Score: "
                                + (studentScore != -1 ? percent.format(studentScore) : "test not taken yet") + "\n";
                        // show availability
                        result += "Available on: " + startDT + "\n";
                        result += "Due on: " + endDT + "\n\n";
                        //show class stats
                        result += "Average Class Score: " + percent.format(avg) + "\n";
                        result += "Highest Class Score: " + percent.format(high) + "\n";
                        result += "Lowest Class Score: " + percent.format(low) + "\n\n";

                    }
                    r.close();
                } catch (Exception e) {
                    result += "There are no statistics for this test yet\n";
                    try {
                        // check when due if file exists
                        File file2 = new File("../Content/" + currTest + ".txt");
                        Scanner r2 = new Scanner(file2);
                        r2.nextLine();
                        r2.nextLine();
                        result += "Available on: " + r2.nextLine() + "\n\n";
                        r2.close();
                    } catch (Exception ex) {
                        result += "\n";
                    }
                }
            }
        }
        return result;

    }

    /*
     * Method name: takeTest ; accepts: String name, String testName, String
     * password ; returns: int ;
     *
     * purpose: returns the status number when test is loaded and verified (test
     * password and time availability).
     */
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

    /*
     * Method name: testList ; accepts: n/a ; returns: String ;
     *
     * purpose: List the available tests on the network.
     */
    public String testList() {
        //init list string
        String list = "";
        //go through files in folder
        File availFileFolder = new File("../Content");
        File[] listOfFiles = availFileFolder.listFiles();
        String prevFile = "";
        for (File file : listOfFiles) {
            // file names : testName.txt || testName_results.txt
            String currFile = file.getName().substring(0, file.getName().length() - 4).split("_")[0];
            // to prevent writing testName twice
            if (!currFile.equals(prevFile)) {
                list += currFile + "\n";
            }
            //update prevfile
            prevFile = currFile;
        }
        return list;
    }

    /*
     * Method name: uploadResult ; accepts: String student, double result, String
     * testName ; returns: void ;
     *
     * purpose: upload student's supplied test result to file for given test.
     */
    public void uploadResult(String student, double result, String testName) {
        // save result to its corresponding file
        try (FileWriter fw = new FileWriter("../Content/" + testName + "_results.txt", true);//append=true
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(student + " " + result);
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            
        }
    }
}