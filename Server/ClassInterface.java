package Server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClassInterface extends Remote {
    public int takeTest(String name,String testName, String password) throws RemoteException;
    public String testList() throws RemoteException;
}