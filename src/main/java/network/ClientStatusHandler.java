package network;

/**
 * Class for handling the status of the client.
 */
public class ClientStatusHandler {
    ClientStatus status = ClientStatus.Idle;
    ClientStatus lastValidStatus = ClientStatus.Idle;
    Object statusLock = new Object();
    Object lastValidStatusLock = new Object();

    /**
     * Returns the current status of the client.
     * @return the current status of the client.
     */
    public ClientStatus getStatus() {
        synchronized (statusLock){
            return status;
        }
    }

    /**
     * Sets the status of the client.
     * @param status the new status of the client.
     */
    public void setStatus(ClientStatus status) {
        synchronized (statusLock){
            this.status = status;
        }
        if(status != ClientStatus.Disconnected){
            synchronized (lastValidStatusLock){
                lastValidStatus = status;
            }
        }
    }

    /**
     * Returns the last valid status of the client.
     * @return the last valid status of the client.
     */
    public ClientStatus getLastValidStatus() {
        synchronized (lastValidStatusLock){
            return lastValidStatus;
        }
    }

    /**
     * Sets the last valid status of the client.
     * @param status the new last valid status of the client.
     */
    public void setLastValidStatus(ClientStatus status) {
        synchronized (lastValidStatusLock){
            lastValidStatus = status;
        }
    }
}
