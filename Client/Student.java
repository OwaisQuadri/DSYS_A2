
import java.rmi.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Student {

    final static int PORT_NUMBER = 1234;
    final static String[] USERS = { "owais", "student","john" };
    final static String[] PASSWORDS = { "owais", "student","john" };
    final static String ANSWER_KEY = "iosuhefiuherfiushzfgiu";
    // create scanner
    static Scanner sc = new Scanner(System.in);

    public static void main(String argv[]) {
        if (argv.length != 0) {
            System.out.println("Usage: java Student");
            System.exit(0);
        }

        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        boolean login = false;
        for (int i = 0; i < USERS.length; i++) {
            if (username.equalsIgnoreCase(USERS[i])) {
                if (password.equals(PASSWORDS[i])) {
                    login = true;
                }
            }
        }
        // verify login
        if (!login) {
            System.out.println("Incorrect username or password");
            System.out.println("Usage: java Student");
            System.exit(0);
        }

        try {
            String name = "//127.0.0.1/Supervisor";
            // configure RMI
            ClassInterface ci = (ClassInterface) Naming.lookup(name);
            // client stub ready
            // does student just want to see their own statistics or take a test
            System.out.println("Welcome " + username + "!");
            gotoMenu(ci, username);

        } catch (Exception e) {
            System.out.println("Submissions Unavailable : try again later.");
            e.printStackTrace();
            System.exit(0);
        }
        // close scanner
        sc.close();
        // close sockets

    }

    private static void gotoMenu(ClassInterface ci, String username) throws RemoteException {
        switch (displayMenu()) {
        case 1:
            // take test
            runTest(ci, username);
            gotoMenu(ci, username);
            break;
        case 2:
            // get stats
            System.out.println(ci.studentStats(username));
            gotoMenu(ci, username);
            break;
        case 3:
            // exit
            System.exit(0);
        default:
            System.out.println("Invalid entry : please enter a number from the above selection");
            gotoMenu(ci, username);
            break;
        }
    }

    private static void runTest(ClassInterface ci, String username) throws RemoteException {
        // ask student which test they want to take
        String testName, testPW;
        System.out.println("Which test would you like to take?\n" + ci.testList());
        System.out.print("Test Name: ");
        testName = sc.nextLine();
        System.out.print("Test Password: ");
        testPW = sc.nextLine();
        // ask server to take a certain test
        int TestStatus = ci.takeTest(username, testName, testPW);
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
            // test taking time
            ci.uploadResult(username, startTest(testName, ci.getQuestions()), testName);
            break;

        default:
            break;
        }
    }

    private static int displayMenu() {
        System.out.println("1. Take a Test");
        System.out.println("2. Review Test Stats");
        System.out.println("3. Exit");
        System.out.println("please enter a number from the above selection");
        int input = 0;
        try {
            input = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid entry : no integer detected, try again");
            displayMenu();
            input = 0;
        }
        return input;

    }

    public static double startTest(String testName, ArrayList<ArrayList<String>> questions) {
        // tell them the name of quiz
        System.out.println("Test " + testName + " is starting now:");
        int total = 0;
        int correct = 0;
        boolean isTest = false;
        for (ArrayList<String> q : questions) {
            System.out.println(q.get(0));// display q's
            for (int i = 1; i < q.size(); i++) {
                String send = q.get(i);
                if (send.charAt(0) == '!') {
                    send = send.substring(1);
                    isTest = true;
                }
                send = i + ". " + send;
                System.out.println(send);
            }
            if (!isTest) {
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
        DecimalFormat percent = new DecimalFormat("#0.00 %");
        System.out.println("Your score was : " + percent.format(result) + "\n");
        return result;

    }
}