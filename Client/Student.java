
import java.rmi.*;
import java.util.Scanner;

public class Student {

    final static int PORT_NUMBER = 1234;
    final static String[] USERS = { "owais", "student" };
    final static String[] PASSWORDS = { "owais", "student" };
    final static String ANSWER_KEY = "iosuhefiuherfiushzfgiu";

    public static void main(String argv[]) {
        if (argv.length != 0) {
            System.out.println("Usage: java Student");
            System.exit(0);
        }
        // create scanner
        Scanner sc = new Scanner(System.in);
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
            System.out.println("Usage: java Client.Student");
            System.exit(0);
        }

        try {
            String name = "//127.0.0.1/Supervisor";
            // configure RMI
            ClassInterface ci = (ClassInterface) Naming.lookup(name);
            // client stub ready
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
            case -1:
                System.out.println("This user has reached the maximum amount of tries for this test");
                break;
            case 0:
                System.out.println("test does not exist");
                break;
            case 1:
                System.out.println("the test will begin shortly");
                startTest();
                // when questions are all answered, get the student summary
                getStats();
                break;

            default:
                break;
            }

        } catch (Exception e) {
            System.out.println("Submissions Unavailable : try again later.");
            e.printStackTrace();
            System.exit(0);
        }
        // close scanner
        sc.close();
        // close sockets

    }

    public static void startTest() {
        // while there are unanswered questions

        // ask for next question

        // reply with answer to next question

    }

    public static void getStats() {
        // student summary: names of all tests taken with the highest scores (vs class
        // average,low and high) (and maybe subject averages if applicable)
    }
}