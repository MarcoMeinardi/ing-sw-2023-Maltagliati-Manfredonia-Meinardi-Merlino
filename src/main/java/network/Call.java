package network;

import java.io.Serializable;
import java.util.UUID;

/**
 * Call is a record that contains the parameters, the service and the id of the remote function.
 * @param params the parameters needed by the function
 * @param service the function to call
 * @param id the id used to identify this call istance
 * @param <P> the type of the parameters
 */
public record Call<P extends Serializable>(P params, Service service, UUID id) implements Serializable{}
