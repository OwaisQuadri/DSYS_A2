
import java.rmi.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Student {
    /*
     * 
     * Class Student created on 2021-11-06 to act as Client for Students so that
     * they can complete tests and see their past test results as well as where they
     * place in the class
     * 
     */
    // init constants
    final static String[] USERS = { "owais", "student", "john", "taha", "sanzir" };
    final static String[] PASSWORDS = { "owais", "student", "john", "taha", "sanzir" };
    // create scanner
    static Scanner sc = new Scanner(System.in);

    public static void main(String argv[]) {
        // ensure proper usage (no args)
        if (argv.length != 0) {
            System.out.println("Usage: java Student");
            System.exit(0);
        }
        // ask user for username
        System.out.print("Username: ");
        String username = sc.nextLine();
        // and password
        System.out.print("Password: ");
        String password = sc.nextLine();
        // credential validation
        boolean login = false;
        for (int i = 0; i < USERS.length; i++) {
            // check corresponding passwords
            if (username.equalsIgnoreCase(USERS[i])) {// only username ignores case
                if (password.equals(PASSWORDS[i])) {
                    login = true;
                }
            }
        }
        // quit if login credentials incorrect
        if (!login) {
            System.out.println("Incorrect username or password");
            System.out.println("Usage: java Student");
            System.exit(0);
        }
        // connect to supervisor server
        try {
            String name = "//127.0.0.1/Supervisor";
            // configure RMI
            ClassInterface ci = (ClassInterface) Naming.lookup(name);
            // client stub ready
            // does student just want to see their own statistics or take a test
            System.out.println("Welcome " + username + "!");
            gotoMenu(ci, username);

        } catch (Exception e) {
            // supervisor forgot to accept submissions
            System.out.println(
                    "Submissions Unavailable : try again later when a Supervisor starts to accept submissions.");
            System.exit(0);
        }
        // close scanner
        sc.close();

    }

    /*
     * Method name: displayMenu ; accepts: n/a ; returns: int ;
     *
     * purpose: Display menu for client / Student to perform action such as take
     * test and view stats. Does not perform actions itself, but returns an integer
     * that points to the action
     */
    private static int displayMenu() {
        // display User interface
        System.out.println("1. Take a Test");
        System.out.println("2. Review Test Stats");
        System.out.println("3. Exit");
        System.out.println("please enter a number from the above selection");
        int input = 0;
        // verify input
        try {
            input = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid entry : no integer detected, try again");
            displayMenu();// loop until they get it right
            input = 0;
        }
        return input;

    }

    /*
     * Method name: gotoMenu ; accepts: ClassInterface ci, String username ;
     * returns: void ;
     *
     * purpose: navigates to the different functions that the Student can perform
     * such as taking test and view stats. This is called gotoMenu because this is
     * the menu that must be gone to in the end after all actions are performed so
     * that the user doesn't need to keep signing in to view stats after taking a
     * test or vice versa.
     */
    private static void gotoMenu(ClassInterface ci, String username) throws RemoteException {
        // based on the input from display
        switch (displayMenu()) {
        case 1:
            // take test
            runTest(ci, username);
            gotoMenu(ci, username);
            break;
        case 2:
            // get student's stats
            System.out.println(ci.studentStats(username));
            gotoMenu(ci, username);
            break;
        case 3:
            // exit
            System.exit(0);
        default:
            // another number?
            System.out.println("Invalid entry : please enter a number from the above selection");
            gotoMenu(ci, username);
            break;
        }
    }

    /*
     * Method name: runTest ; accepts: ClassInterface ci, String username ; returns:
     * void ;
     *
     * purpose: The test is specified along with the answer and the client requests
     * confirmation to complete the test from the server but based on the server
     * response, the status message is printed. when status is 1, user will take
     * test and upload test at the same time
     */
    private static void runTest(ClassInterface ci, String username) throws RemoteException {
        // ask student which test they want to take
        String testName, testPW;
        // get list of available tests from server
        System.out.println("Which test would you like to take?\n" + ci.testList());
        System.out.print("Test Name: ");
        testName = sc.nextLine();// accept input
        System.out.print("Test Password: ");
        testPW = sc.nextLine();// accept test password
        // ask server to take a certain test
        int TestStatus = ci.takeTest(username, testName, testPW);
        // server response printed to student
        switch (TestStatus) {
        case -3:
            // incorrect password
            System.out.println("The test password was incorrect");
            break;
        case -2:
            // not within time frame
            System.out.println("This test is unavailable at this time\n");
            break;
        case -1:
            System.out.println("This user has reached the maximum amount of tries for this test");
            break;
        case 0:
            System.out.println("This test does not exist");
            break;
        case 1:
            System.out.println("the test will begin shortly");
            // test taking time !!
            ci.uploadResult(username, startTest(testName, ci.getQuestions()), testName);
            break;

        default:
            break;
        }
    }

    /*
     * Method name: startTest ; accepts: String testName,
     * ArrayList<ArrayList<String>> questions ; returns: double ;
     *
     * purpose: actually performing the action of taking a test. the questions are
     * asked and answered, then a result is returned based on correct and incorrect
     * answers
     */
    public static double startTest(String testName, ArrayList<ArrayList<String>> questions) {
        // tell them the name of quiz
        System.out.println("Test " + testName + " is starting now:");
        int total = 0;
        int correct = 0;
        boolean isTest = false;
        for (ArrayList<String> q : questions) {
            System.out.println(q.get(0));// display q's
            for (int i = 1; i < q.size(); i++) {// display answers
                String send = q.get(i);
                if (send.charAt(0) == '!') {// remove ! from correct answers
                    send = send.substring(1);
                    isTest = true;
                }
                send = i + ". " + send;
                System.out.println(send);// display question and answers after parsing and serializing
            }
            if (!isTest) {// if no correct answers specified (!)
                correct++;
            }
            // accept answer
            int ansIndex = Integer.parseInt(sc.nextLine());
            // check for correct
            if (q.get(ansIndex).charAt(0) == '!') {
                correct++;
                total++;
            } else {
                total++;
            }

        }
        // send result to user
        double c = (double) correct;
        double t = (double) total;
        double result = c / t;
        DecimalFormat percent = new DecimalFormat("#0.00 %");// format output to user
        System.out.println("Your score was : " + percent.format(result) + "\n");
        return result;

    }
}