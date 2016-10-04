

public interface ParserInterface 
    {
    // Methods must throw RemoteException.
    public void sendCommand() throws RemoteException;
    public void receiveAlert(String meter_name, String meter_location) throws RemoteException;
    public void receiveReading(String meter_name, String meter_location, double reading_value) throws RemoteException;
    public boolean unregisterMeter(String meter_name, String meter_host) throws RemoteException;
    public boolean registerMeter(String meter_name, String meter_location) throws RemoteException;
    public void listAllRegisteredMeter() throws RemoteException;
    public void setTariff() throws RemoteException;
    public double returnTariff() throws RemoteException;
    public String accessThing() throws RemoteException;
    public void retrieveData() throws RemoteException;
    public void meterBackOnline(String meter_name) throws RemoteException;
    }