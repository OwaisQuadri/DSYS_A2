
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Supervisor {
    /*
     * 
     * Class Supervisor created on 2021-11-06 to act as an RMI server for students
     * to take an exam or review their marks. This class is also an interface for
     * teachers and administrators to create, delete and review test and their
     * statistics.
     * 
     */

    // initialize
    // constant
    final static String[] USERS = { "admin", "teacher" };
    final static String[] PASSWORDS = { "admin", "teacher" };
    final static Scanner sc = new Scanner(System.in);
    // variables
    private static String currentTest = "";
    private static ArrayList<ArrayList<String>> currQuestions = new ArrayList<>();

    public static void main(String argv[]) {
        // make sure no arguements are passed
        if (argv.length != 0) {
            System.out.println("Usage: java -Djava.security.policy=policy.txt Supervisor");
            System.exit(0);
        }
        // prompt user for username
        System.out.print("Username: ");
        String username = sc.nextLine();
        // prompt user for password
        System.out.print("Password: ");
        String password = sc.nextLine();
        // verify login
        boolean login = false;
        // check the constant arrays of usernames and corresponding passwords for match
        for (int i = 0; i < USERS.length; i++) {
            if (username.equalsIgnoreCase(USERS[i])) {// ignore case on username only
                if (password.equals(PASSWORDS[i])) {
                    login = true;// verified
                }
            }
        }
        // if login not verified, exit
        if (!login) {
            System.out.println("Incorrect username or password, try again later");
            System.exit(0);
        }
        // welcome the verified user
        System.out.println("\nWelcome, " + username + "!");
        // show menu
        displayMenu();
    }

    /*
     * Method name: displayMenu ; accepts: no parameters ; returns: void ;
     *
     * purpose: show user possible actions and guide user towards those actions when
     * selected. Then send user to perform the selected action.
     */
    private static void displayMenu() {
        // command line menu
        System.out.println("1. Create New Test");
        System.out.println("2. Accept Test Submissions");
        System.out.println("3. Review Test Stats");
        System.out.println("4. Delete a Test (and its stats)");
        System.out.println("5. Exit");
        System.out.println("please enter a number from the above selection");
        int input = 0;
        // make sure input is proper
        try {
            input = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid entry : no integer detected, try again");
            displayMenu();
            input = 0;
        }
        // switch statement on input
        switch (input) {
        case 0:
            // do nothing
            break;
        case 1:
            // create test and store in local file under Content/
            createTest();
            // come back to menu once done
            displayMenu();
            break;
        case 2:
            // read available tests and get admin to select one
            startTest();// does not come back to menu because program is exited
            break;
        case 3:
            // show test menu with stats instead of everytrhing else
            showTestMenu("stats");
            // come back to menu once done
            displayMenu();
            break;
        case 4:
            // delete test
            showTestMenu("del");
            // come back to menu once done
            displayMenu();
            break;
        case 5:
            // exit
            System.exit(0);
            break;
        default:
            // invalid entry
            System.out.println("Invalid entry : please enter a number from the above selection\n");
            displayMenu();
            break;
        }
    }

    /*
     * Method name: createTest ; accepts: no parameters ; returns: void ;
     * 
     * purpose: interface for administrators and teachers to create and save tests
     * for later use.
     */
    private static void createTest() {
        // ask what the test should be named
        ArrayList<ArrayList<String>> questions = new ArrayList<>();
        System.out.print("\nWhat would you like to name the Test? ");
        String testName = sc.nextLine();
        // cannot have a space or _ in the name
        if (testName.contains("_")) {
            System.out.println("Please do not use \"_\" in the test name");
        }
        // add to a file
        String path = "../Content/" + testName + ".txt";
        try {
            File file = new File(path);
            if (file.createNewFile()) {
                System.out.print("Please enter a password for the test: ");
                String testpw = sc.nextLine();
                System.out.print("Would you like to add a question? (y/n) ");
                while ("y".equalsIgnoreCase(sc.nextLine())) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(sc.nextLine());
                    System.out.print("Would you like to add an answer to this question? (y/n) ");
                    while ("y".equalsIgnoreCase(sc.nextLine())) {
                        System.out.println("please add a '!' to the beginning if this is the correct answer");
                        temp.add(sc.nextLine());
                        System.out.print("Would you like to add an answer to this question? (y/n) ");
                    }
                    questions.add(temp);
                    System.out.print("Would you like to add a question? (y/n) ");
                }
                // how many retakes allowed
                System.out.print("Please enter the number of retakes for the test (blank means infinite): ");
                String retakes = sc.nextLine();
                // start and end date and time
                System.out.print("Is there a specified time frame for this test? (Y/n)");
                String startDT = "";
                String endDT = "";
                if ("y".equalsIgnoreCase(sc.nextLine())) {
                    System.out.print("what is the start date and time (yyyy-mm-dd hh:mm): ");
                    startDT = sc.nextLine();
                    System.out.print("what is the end date and time (yyyy-mm-dd hh:mm): ");
                    endDT = sc.nextLine();
                }
                // write to file
                FileWriter wr = new FileWriter(path);
                // test password
                wr.append(testpw + "\n");
                // number of retakes
                wr.append(retakes + "\n");
                // start and end date and time
                wr.append(startDT + "\n");
                wr.append(endDT + "\n");
                // loop questions
                for (ArrayList<String> QA : questions) {
                    // write question
                    wr.append(QA.get(0) + "\n");
                    // write number of answers for question
                    wr.append((QA.size() - 1) + "\n");
                    // write answers
                    for (int i = 1; i < QA.size(); i++) {
                        wr.append(QA.get(i) + "\n");
                    }
                }

                wr.close();
                System.out.println("Test Created : " + testName + "\n");
            } else {
                System.out.println("This Test already exists.\n");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }

    /*
     * Method name: startTest ; accepts: no parameters ; returns: void ;
     *
     * purpose: binds "ClassInterface" implementation "ClientHandler" to RMI socket
     * to accept and deal with clients' requests. exit program when "exit" or
     * 'Ctrl+C' are typed.
     */
    private static void startTest() {

        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }
            ClassInterface ci = (ClassInterface) new ClientHandler("Supervisor");
            Naming.rebind("//127.0.0.1/Supervisor", ci);
            // waiting for students
            System.out.println(
                    "Tests are now being accepted\n" + "Press Ctrl+C or enter \"exit\" to stop accepting submissions.");
            while (sc.nextLine().equalsIgnoreCase("exit")) {
                System.out.println("Exiting .. ");
                System.exit(0);

            }
        } catch (Exception e) {
            System.out.println("Exiting .. ");
        }

    }

    /*
     * Method name: showTestMenu ; accepts: String mode ; returns: void ;
     *
     * purpose: shows the available tests, but the action that happens to the test
     * is dependent on the input String. Actions that could be performed on the
     * selected test are: Delete and View Stats.
     */
    private static void showTestMenu(String mode) {
        // list available tests
        String list = "";
        File availFileFolder = new File("../Content");
        File[] listOfFiles = availFileFolder.listFiles();
        String prevFile = "";
        for (File file : listOfFiles) {
            // file names : testName.txt || testName_results.txt
            String currFile = file.getName().substring(0, file.getName().length() - 4).split("_")[0];
            // to prevent writing testName twice
            if (!currFile.equals(prevFile)) {
                list += "\t" + currFile + "\n";
            }
            // update prevFile
            prevFile = currFile;
        }
        System.out.println("Please enter the name of the test that you would like to use:\n" + list);
        // load selected Test
        loadTest("../Content/" + sc.nextLine() + ".txt");
        // if no crash, test is loaded
        System.out.println("Test Loaded : " + currentTest);
        // perform action on loaded test
        switch (mode) {
        case "stats":
            // print class stats
            printStats();
            break;
        case "del":
            // delete test in question
            deleteCurrent();
            break;

        default:
            break;
        }

    }

    /*
     * Method name: loadTest ; accepts: String path ; returns: void ;
     *
     * purpose: Load test and its questions from a file specified by the path
     * parameter
     */
    private static void loadTest(String path) {
        // re-init loaded questions
        currQuestions = new ArrayList<>();
        try {
            File file = new File(path);// connect to Test file
            Scanner r = new Scanner(file);// open filereader
            currentTest = file.getName().substring(0, file.getName().length() - 4);
            for (int i = 0; i < 4; i++) {
                if (r.hasNextLine()) {
                    r.nextLine();
                }
            }
            // add arraylist of questions and answers into the questions arraylist
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
        } catch (Exception e) {
            System.out.println("An error occurred: the Test does not exist");
        }
    }

    /*
     * Method name: printStats ; accepts: no parameters ; returns: void ;
     *
     * purpose: print the statistics of the submissions on the current test which is
     * selected/loaded.
     */
    private static void printStats() {
        // read current test stat file and take note of average, low and high marks
        DecimalFormat percent = new DecimalFormat("##0.00 %");
        try {
            File file = new File("../Content/" + currentTest + "_results.txt");
            Scanner r = new Scanner(file);
            double sum = 0;
            int count = 0;
            double low = 2;
            double high = -1;
            while (r.hasNextLine()) {
                double entry = Double.parseDouble(r.nextLine().split(" ")[1]);
                // sum and count for average
                sum += entry;
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
                // file was emptied
                System.out.println("This file is empty");
            } else {
                // output stats
                double avg = sum / count;
                System.out.println("Number of attempts overall: " + count);
                System.out.println("Average Score: " + percent.format(avg));
                System.out.println("Highest Score: " + percent.format(high));
                System.out.println("Lowest Score: " + percent.format(low) + "\n");

            }

            r.close();
        } catch (Exception e) {
            // file was never created
            System.out.println("There are no statistics for this test\n");
        }
    }

    /*
     * Method name: deleteCurrent ; accepts: no parameters ; returns: void ;
     *
     * purpose: Delete whichever test is currently loaded.
     */
    private static void deleteCurrent() {
        // connect to files
        File test = new File("../Content/" + currentTest + ".txt");
        File testResults = new File("../Content/" + currentTest + "_results.txt");

        // delete test if exists
        if (test.delete()) {
            System.out.println("Deleted the test : " + currentTest);
        } else {
            System.out.println("Failed to delete the test : " + currentTest);
        }
        // delete results if exists
        if (testResults.delete()) {
            System.out.println("Deleted the test results : " + currentTest);
        } else {
            System.out.println("Failed to delete the test results : " + currentTest);
        }
    }

}