
import java.rmi.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Student {

    final static int PORT_NUMBER = 1234;
    final static String[] USERS = { "owais" , "student"};
    final static String[] PASSWORDS = { "owais", "student"};

    public static void main(String argv[]) {
        if (argv.length != 0) {
            System.out.println("Usage: java Client.Student");
            System.exit(0);
        }
        // create scanner
        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("\nPassword: ");
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
            System.out.println("Usage: java Client.Student username password");
            System.exit(0);
        }
        // open socket
        Socket clientSocket = null;
        // create in/out strm
        BufferedReader in = null;
        PrintWriter out = null;
        String machineName="127.0.0.1";
        try {
            String name="//"+machineName+"Supervisor";
            //configure RMI
            ClassInterface ci= (ClassInterface)Naming.lookup(name);
            // open socket
            clientSocket = new Socket("localhost", PORT_NUMBER);// name/ip address, port number
            // init input
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // init output
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("Test Unavailable : try again later.");
            System.exit(0);
        }
        // clientSocket, input, output ready
        
        String line = null;

        try {
            System.out.println("Server status: " + in.readLine());
            //tell server who this is
            out.println(username);
            out.flush();
            System.out.print("Test Name: ");
            
            String testName = in.readLine();
            if (!sc.nextLine().equals(testName)){
                System.out.println("Sorry, that test is unavailable");
                System.exit(0);
            }
            System.out.println("\n" + testName + "\n");
            //take test
            while (true) {
                line = in.readLine();
                if ("iosuhefiuherfiushzfgiu".equals(line)) {
                    String ans = sc.nextLine();
                    out.println(ans);
                } else if("exit".equals(line)){
                    System.exit(0);
                }else{
                    System.out.println(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // close scanner
        sc.close();
        // close sockets

    }
}