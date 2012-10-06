package edu.vub.at.nfcpoker.comm;

import java.util.Date;

import edu.vub.at.nfcpoker.ConcretePokerServer.GameState;

public interface Message {

	public static abstract class TimestampedMessage implements Message {
		public long timestamp;
			
		public TimestampedMessage() {
			timestamp = new Date().getTime();
		}
		
		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@" + timestamp;
		}
	}
	
	public static final class StateChangeMessage extends TimestampedMessage {
		public GameState newState;

		public StateChangeMessage(GameState newState_) {
			newState = newState_;
		}
		
		// for kryo
		public StateChangeMessage() {}

		@Override
		public String toString() {
			return super.toString() + ": State change to " + newState;
		}
	}
}
