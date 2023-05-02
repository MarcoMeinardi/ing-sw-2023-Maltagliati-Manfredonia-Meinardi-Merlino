package controller.lobby;

import controller.game.GameController;
import network.Call;
import network.Result;
import network.WrongServiceException;
import network.parameters.WrongParametersException;
import network.rpc.server.Client;
import network.rpc.server.ClientManager;
import network.rpc.server.ClientNotFoundException;
import network.rpc.server.ClientStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class NotEnoughPlayersException extends Exception{ }
