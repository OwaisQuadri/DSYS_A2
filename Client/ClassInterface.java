
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClassInterface extends Remote {
    public int takeTest(String name, String testName, String password) throws RemoteException;

    public String testList() throws RemoteException;

    public ArrayList<ArrayList<String>> getQuestions() throws RemoteException;

    public void uploadResult(String student,double result, String testName) throws RemoteException;

    public String studentStats(String student) throws RemoteException;
    
}