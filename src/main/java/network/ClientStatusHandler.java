package network;

public class ClientStatusHandler {
    ClientStatus status = ClientStatus.Idle;
    ClientStatus lastValidStatus = ClientStatus.Idle;
    Object statusLock = new Object();
    Object lastValidStatusLock = new Object();

    public ClientStatus getStatus() {
        synchronized (statusLock){
            return status;
        }
    }

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

    public ClientStatus getLastValidStatus() {
        synchronized (lastValidStatusLock){
            return lastValidStatus;
        }
    }

    public void setLastValidStatus(ClientStatus status) {
        synchronized (lastValidStatusLock){
            lastValidStatus = status;
        }
    }
}
