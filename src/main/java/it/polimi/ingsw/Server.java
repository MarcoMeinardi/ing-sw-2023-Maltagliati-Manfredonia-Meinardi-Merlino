package it.polimi.ingsw;

import controller.lobby.LobbyController;
import network.ClientManagerInterface;
import network.GlobalClientManager;

import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Server {
	static private final Logger logger = Logger.getLogger(Server.class.getName());
	static private final Pattern ip4Pattern = Pattern.compile("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$");
	static private final Pattern ip6Pattern = Pattern.compile("^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$");
	static public void main(String[] args) throws Exception {
		System.setProperty("java.rmi.server.hostname", getIp());
		ClientManagerInterface clientManager = GlobalClientManager.getInstance();
		LobbyController lobbyController = LobbyController.getInstance();
		clientManager.waitAndClose();
	}

	static private String getIp(){
		String ip;
		Scanner scanner = new Scanner(System.in);
		while(true){
			System.out.println("Enter the server IP address:");
			ip = scanner.nextLine();
			if(ip == null){
				System.out.println("Invalid IP address");
				continue;
			}
			ip = ip.replace(" ","").replace("\n","");
			if(ip.equals("") || !(ip4Pattern.matcher(ip).matches() || ip6Pattern.matcher(ip).matches())){
				System.out.println("Invalid IP address");
				continue;
			}
			logger.info("RMI hostaname set to IP address: " + ip);
			return ip;
		}
	}
}
