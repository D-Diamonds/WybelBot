import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeUpdater extends Updater<ArrayList<TicTacToe>> {

	public static final String moduleName = "TicTacToe";
	public static final String moduleCommand = "!ttt";
	public static final String moduleDataPath = "src/" + BotRunner.getBotName() + "Data" + moduleName;

	public TicTacToeUpdater() {
		createDataSaver(new ArrayList<>());
	}


	// finds player's board from list of ongoing games
	private TicTacToe getPlayerBoard(User user) {
		for (TicTacToe game : getUpdatingObject()) {
			if (game.getPlayerID().equals(user.getId())) {
				return game;
			}
		}
		return null;
	}

	// removes player from the list of ongoing games
	private boolean removePlayerBoard(User user) {
		for (int i = 0; i < getUpdatingObject().size(); i++) {
			if (getUpdatingObject().get(i).getPlayerID().equals(user.getId())) {
				getUpdatingObject().remove(i);
				dataSaver.queueSaving();
				return true;
			}
		}
		return false;
	}

	private void startCmd(TicTacToe board, MessageReceivedEvent event, User author) {
		if (board == null) {
			MessageSender.sendMessage(event, "Creating game for <@" + author.getId() + ">");
			getUpdatingObject().add(board = new TicTacToe(author));
			dataSaver.queueSaving();
		}
		else
			MessageSender.sendMessage(event, "Game already created for <@" + author.getId() + ">");
		MessageSender.sendMessage(event, board.toEmbed());
	}

	private void moveCmd(TicTacToe board, MessageReceivedEvent event, User author, String move) {
		if (!board.movesLeft()) {
			MessageSender.sendMessage(event, "No moves remaining in <@" + author.getId() + ">'s " + moduleName + " game");
			removePlayerBoard(author);
		}
		else if (board.playMove(move.toLowerCase())) {
			if (board.checkWin(board.getMoveRow(), board.getMoveCol(), ":x:")) {
				BotRunner.getXpQueue().addToQueue(event, "ttt_win");
				board.setGameResult("ttt_win");
				MessageSender.sendMessage(event, board.toEmbed());
				MessageSender.sendMessage(event, "<@" + author.getId() + "> has won " + moduleName + "!");
				removePlayerBoard(author);
			}
			else if (!board.movesLeft()) {
				BotRunner.getXpQueue().addToQueue(event, "ttt_tie");
				board.setGameResult("ttt_tie");
				MessageSender.sendMessage(event, board.toEmbed());
				MessageSender.sendMessage(event, "<@" + author.getId() + ">'s " + moduleName + " game ended in a tie!");
				removePlayerBoard(author);
			}
			else {
				board.moveAI();
				if (board.checkWin(board.getMoveRow(), board.getMoveCol(), ":o:")) {
					BotRunner.getXpQueue().addToQueue(event, "ttt_loss");
					board.setGameResult("ttt_loss");
					MessageSender.sendMessage(event, board.toEmbed());
					MessageSender.sendMessage(event, BotRunner.getBotName() + " has won " + moduleName + " against <@" + author.getId() + ">");
					removePlayerBoard(author);
				}
				else if (!board.movesLeft()) {
					BotRunner.getXpQueue().addToQueue(event, "ttt_tie");
					board.setGameResult("ttt_tie");
					MessageSender.sendMessage(event, board.toEmbed());
					MessageSender.sendMessage(event, "<@" + author.getId() + ">'s " + moduleName + " game ended in a tie!");
					removePlayerBoard(author);
				}
				else {
					dataSaver.queueSaving();
					MessageSender.sendMessage(event, board.toEmbed());
				}
			}
		}
		else
			MessageSender.sendMessage(event, "<@" + author.getId() + "> Invalid syntax or move. Command syntax: \"" + moduleCommand + " move [number]\"");

	}

	private void helpCmd(MessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(moduleName + " Commands:");
		eb.setColor(new Color(80, 255, 236));
		eb.addField("**Start a game**", moduleCommand + " start", false);
		eb.addField("**Make a move**", moduleCommand + " move [#]", false);
		eb.addField("**Get game board**", moduleCommand + " get", false);
		MessageSender.sendMessage(event, eb.build());
	}

	private void removeCmd(String removeType, MessageReceivedEvent event) {
		List<Member> taggedMembers;
		if (removeType.equals("all")) {
			System.out.println("Removing all " + moduleName + " games");
			MessageSender.sendMessage(event, "All " + moduleName + " games have been removed");
			resetUpdatingObject(new ArrayList<>());
			dataSaver.queueSaving();
		}
		else {
			taggedMembers = event.getMessage().getMentionedMembers();
			for (Member member : taggedMembers) {
				if (removePlayerBoard(member.getUser())) {
					System.out.println("Removing " + member.getEffectiveName() + "'s " + moduleName + " game");
					MessageSender.sendMessage(event, "<@" + member.getIdLong() + ">'s " + moduleName + " game has been removed");
				}
				else
					MessageSender.sendMessage(event, "No board found for <@" + member.getIdLong() + ">");
			}
		}
	}

	private void saveCmd(String saveToggle, MessageReceivedEvent event) {
		if (saveToggle.equals("disable")) {
			System.out.println("Saving disabled");
			MessageSender.sendMessage(event, "Saving disabled");
			dataSaver.disableSaving();
		}
		else if (saveToggle.equals("enable")) {
			System.out.println("Saving enabled");
			MessageSender.sendMessage(event, "Saving enabled");
			enableSaving();
		}
	}


	// tictactoe message commands
	public void onMessageReceived(MessageReceivedEvent event) {
		//MessageChannel channel = event.getChannel();
		User author = event.getAuthor();
		String[] messagePhrases = event.getMessage().getContentDisplay().toLowerCase().split(" ");

		TicTacToe board = getPlayerBoard(author);

		// normal commands

		// help
		if (messagePhrases[1].equals("help"))
			helpCmd(event);
		// start
		else if (messagePhrases[1].equals("start"))
			startCmd(board, event, author);
		else if (board != null) {
			// move
			if (messagePhrases.length >= 3 && messagePhrases[1].equals("move"))
				moveCmd(board, event, author, messagePhrases[2]);
			// get
			else if (messagePhrases[1].equals("get"))
				MessageSender.sendMessage(event, board.toEmbed());
			// end
			else if (messagePhrases[1].equals("end")) {
				removePlayerBoard(author);
				MessageSender.sendMessage(event, "<@" + author.getId() + ">'s " + moduleName + " game has ended");
			}
		}
		else
			MessageSender.sendMessage(event, "No board found for <@" + author.getId() + ">. Create one with the command \"" + moduleCommand + " start\"");

		// admin commands

		if (author.getId().equals("221748640236961792")) {
			// remove all/specific tictactoe games
			if (messagePhrases.length >= 3 && messagePhrases[1].equals("remove"))
				removeCmd(messagePhrases[2], event);
			// disable/enable saving
			else if (messagePhrases.length >= 3 && messagePhrases[1].equals("save"))
				saveCmd(messagePhrases[2], event);
		}
	}
}