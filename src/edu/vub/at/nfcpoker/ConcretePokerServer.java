package edu.vub.at.nfcpoker;

import java.io.IOException;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import android.app.Activity;
import android.util.Log;
import edu.vub.at.commlib.CommLib;
import edu.vub.at.commlib.CommLibConnectionInfo;
import edu.vub.at.nfcpoker.comm.Message;
import edu.vub.at.nfcpoker.comm.PokerServer;
import edu.vub.at.nfcpoker.comm.Message.StateChangeMessage;

public class ConcretePokerServer extends PokerServer  {
	
	Runnable exporterR = new Runnable() {	
		@Override
		public void run() {
			Log.d("PokerServer", "Starting export");
			String address = "192.168.1.106";
			String port = "" + CommLib.SERVER_PORT;
			CommLibConnectionInfo clci = new CommLibConnectionInfo(PokerServer.class.getCanonicalName(), new String[] {address, port});
			try {
				CommLib.export(clci);
			} catch (IOException e) {
				Log.e("PokerServer", "Exporter thread crashed", e);
			}
		}
	};
	
	Runnable serverR = new Runnable() {
		public void run() {
			try {
				Log.d("PokerServer", "Starting serverR");
				Server s = new Server();
				Kryo k = s.getKryo();
				k.setRegistrationRequired(false);
				s.bind(CommLib.SERVER_PORT);
				s.start();
				s.addListener(new Listener() {
					@Override
					public void connected(Connection c) {
						super.connected(c);
						Log.d("PokerServer", "Client connected: " + c.getRemoteAddressTCP());
						gameLoop.addClient(c);
					}
				});
			} catch (IOException e) {
				Log.e("PokerServer", "Server thread crashed", e);
			}
		};
	};
	
	int nextClientID = 0;
	public enum GameState {
		WAITING_FOR_PLAYERS, PREFLOP, FLOP, TURN, RIVER, END_OF_ROUND;
	};
	


	public ConcretePokerServer(Activity gui) {
	}
	
	public void start() {		
		new Thread(serverR).start();
		new Thread(exporterR).start();
	}
	
	private GameLoop gameLoop = new GameLoop();
	
	class GameLoop implements Runnable {
		
		public GameLoop() {
			gameState = GameState.WAITING_FOR_PLAYERS;
		}

		public TreeMap<Integer, Connection> newClients = new TreeMap<Integer, Connection>();
		public TreeMap<Integer, Connection> clientsInGame = new TreeMap<Integer, Connection>();
		public GameState gameState;
			
		public void run() {
			while (true) {
				synchronized(this) {
					gameLoop.clientsInGame.putAll(gameLoop.newClients);
					gameLoop.newClients.clear();
				}
				
				Deck deck = new Deck();
				Card[] commonCards = 
						new Card[] { deck.drawFromDeck(), deck.drawFromDeck(), deck.drawFromDeck(), deck.drawFromDeck(), deck.drawFromDeck() };
				
				// hole cards
				newState(GameState.PREFLOP);
				TreeMap<Integer, Card[]> holeCards = new TreeMap<Integer, Card[]>();
				for (Integer clientNum : clientsInGame.navigableKeySet())
					holeCards.put(clientNum, new Card[] { deck.drawFromDeck(), deck.drawFromDeck() });
				
				// flop cards
				newState(GameState.FLOP);

				// turn cards
				newState(GameState.TURN);

				// river cards
				newState(GameState.RIVER);

				// results
				newState(GameState.END_OF_ROUND);

				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					Log.wtf("PokerServer", "Thread.sleep was interrupted", e);
				}
			}
		}

		private void newState(GameState newState) {
			gameState = newState;
			broadcast(new StateChangeMessage(newState));
		}
		
		private void broadcast(Message m) {
			for (Connection c : clientsInGame.values())
				c.sendTCP(m);
		}

		public void addClient(Connection c) {
			Log.d("PokerServer", "Adding client " + c.getRemoteAddressTCP());
			synchronized(this) {
				newClients.put(nextClientID++, c);
			}
			if (newClients.size() >= 2 && gameState == GameState.WAITING_FOR_PLAYERS) {
				Log.d("PokerServer", "Two or more clients connected, game can start");
				new Thread(this).start();
			}
		}
	};

}
