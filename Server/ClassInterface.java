
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClassInterface extends Remote {
    public void function1() throws RemoteException;
}