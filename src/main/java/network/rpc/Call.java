package network.rpc;

import java.io.Serializable;
import java.util.UUID;

public record Call<P extends Serializable>(P params, Service service, UUID id) implements Serializable{}
