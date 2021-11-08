
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClassInterface extends Remote {
    /*
     * 
     * Class ClassInterface created on 2021-11-06 to act as an interface so that
     * clients can use functions through the interface on the ClientHandler's part
     * of the server. Here we must define the functions that must be included on
     * both server and client sides
     * 
     */
    public ArrayList<ArrayList<String>> getQuestions() throws RemoteException;

    public String studentStats(String student) throws RemoteException;

    public int takeTest(String name, String testName, String password) throws RemoteException;

    public String testList() throws RemoteException;

    public void uploadResult(String student, double result, String testName) throws RemoteException;

}