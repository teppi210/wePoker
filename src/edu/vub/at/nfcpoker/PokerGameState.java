/*
 * wePoker: Play poker with your friends, wherever you are!
 * Copyright (C) 2012, The AmbientTalk team.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package edu.vub.at.nfcpoker;

public enum PokerGameState {
	STOPPED, WAITING_FOR_PLAYERS, PREFLOP, FLOP, TURN, RIVER, END_OF_ROUND;
	
	@Override
	public String toString() {
		switch (this) {
		case STOPPED:
			return "STOPPED";
		case WAITING_FOR_PLAYERS:
			return "Waiting for other players";
		case PREFLOP:
			return "Pre-flop";
		case FLOP:
			return "Flop";
		case TURN:
			return "Turn";
		case RIVER:
			return "River";
		case END_OF_ROUND:
			return "Round ended";
		default:
			return "";
		}
	}
}
