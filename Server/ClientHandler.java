package Server;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler extends UnicastRemoteObject implements ClassInterface {// for multithreading client requests
    private String name;
    private Socket client;
    private ArrayList<ArrayList<String>> questions;
    private String studentName;
    static String test = "";
    static String pw, startDT, endDT;
    static int numOfTakes;
    static ArrayList<ArrayList<String>> currQuestions = new ArrayList<>();
    boolean startTest = false;
    Scanner student = new Scanner(System.in);

    // Constructor
    public ClientHandler(String s) throws RemoteException {
        super();
        name = s;
    }

    private static boolean loadTest(String name) {
        // re-init loaded questions
        currQuestions = new ArrayList<>();
        String path = "Content/" + name + ".txt";
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
                numOfTakes = Integer.parseInt(r.nextLine());
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

    public int takenTest(String name) {
        try {
            String path = "Content/" + name + "_results.txt";
            File file = new File(path);
            Scanner r = new Scanner(file);
            int count = 0;
            while (r.hasNextLine()) {
                String curStudent = r.nextLine().split(" ")[0];
                if (curStudent.equals(name)) {
                    count++;
                }
            }
            return count;

        } catch (Exception e) {
            System.out.println("takentest: test dne or not loaded");
            e.printStackTrace();
            return -1;
        }
    }

    public int takeTest(String name, String testName, String password) {
        startTest = false;
        if (loadTest(testName)) {

            // check if user can even run the test
            // is it within the time frame?

            // have they taken the test before
            int tries = takenTest(name);
            if (tries == numOfTakes) {
                System.out.println(name + " has already taken this test " + numOfTakes + " time(s).");
                return -1;
            } else {
                System.out.println(name + ": Attempt #" + (tries + 1));
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
        File availFileFolder = new File("Content");
        File[] listOfFiles = availFileFolder.listFiles();
        String prevFile = "";
        for (File file : listOfFiles) {
            String currFile = file.getName().substring(0, file.getName().length() - 4).split("_")[0];
            if (!currFile.equals(prevFile)) {
                list += currFile + " \n ";
            }
            prevFile = currFile;
        }
        return list;
    }

    public void run() {
        // init
        BufferedReader input = null;
        PrintWriter w = null;
        // create input/output stream
        try {
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            w = new PrintWriter(client.getOutputStream());
            // input, output ready
            w.println("connected");
            w.flush();
            // get that students name
            studentName = input.readLine();
            System.out.println("new student connected: " + studentName);
            // tell them the name of quiz
            w.println(test);
            w.flush();
            int total = 0;
            int correct = 0;
            boolean isTest = false;
            for (ArrayList<String> q : questions) {
                w.println(q.get(0));// display q's
                w.flush();
                for (int i = 1; i < q.size(); i++) {
                    String send = q.get(i);
                    if (send.charAt(0) == '!') {
                        send = send.substring(1);
                        isTest = true;
                    }
                    send = i + ". " + send;
                    w.println(send);
                    w.flush();
                }
                if (!isTest) {
                    correct++;
                }
                w.println("iosuhefiuherfiushzfgiu");
                w.flush();
                // accept answer
                int ansIndex = Integer.parseInt(input.readLine());
                // check for correct
                if (q.get(ansIndex).charAt(0) == '!') {
                    correct++;
                    total++;
                } else {
                    total++;
                }
            }
            // send result
            double c = (double) correct;
            double t = (double) total;
            double score = c / t;
            DecimalFormat percent = new DecimalFormat("#0.00 %");
            w.println("Your score was : " + percent.format(score));
            w.println("exit");
            w.flush();
            // save result
            try (FileWriter fw = new FileWriter("Content/" + test + "_results.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.println(studentName + " " + score);
            } catch (IOException e) {
                // exception handling left as an exercise for the reader
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // if (output !=null){
                // output.close();
                // }
                if (input != null) {
                    input.close();
                }
                if (w != null) {
                    w.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
