import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeUpdater extends Updater<ArrayList<TicTacToe>> {

	private ArrayList<TicTacToe> tttGames;

	private static final String moduleName = "TicTacToe";
	private static final String moduleCommand = "!ttt";
	private static final String moduleDataPath = "src/" + BotRunner.getBotName() + "Data" + moduleName;

	public TicTacToeUpdater() {
		createDataSaver();
	}

	public static String getModuleCommand() {
		return moduleCommand;
	}

	public static String getModuleName() {
		return moduleName;
	}

	// finds player's board from list of ongoing games
	private TicTacToe getPlayerBoard(User user) {
		for (TicTacToe game : tttGames) {
			if (game.getPlayerID().equals(user.getId())) {
				return game;
			}
		}
		return null;
	}

	// removes player from the list of ongoing games
	private boolean removePlayerBoard(User user) {
		for (int i = 0; i < tttGames.size(); i++) {
			if (tttGames.get(i).getPlayerID().equals(user.getId())) {
				tttGames.remove(i);
				dataSaver.queueSaving();
				return true;
			}
		}
		return false;
	}

	// tictactoe message commands
	public void onMessageReceived(MessageReceivedEvent event) {
		//MessageChannel channel = event.getChannel();
		User author = event.getAuthor();
		String[] messagePhrases = event.getMessage().getContentDisplay().toLowerCase().split(" ");

		TicTacToe board = getPlayerBoard(author);

		// normal commands

		// help
		if (messagePhrases.length >= 2 && messagePhrases[1].equals("help")) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(moduleName + " Commands:");
			eb.setColor(new Color(80, 255, 236));
			eb.addField("**Start a game**", moduleCommand + " start", false);
			eb.addField("**Make a move**", moduleCommand + " move [#]", false);
			eb.addField("**Get game board**", moduleCommand + " get", false);
			MessageSender.sendMessage(event, eb.build());
		}
		// start
		else if (messagePhrases.length >= 2 && messagePhrases[1].equals("start")) {
			if (board == null) {
				MessageSender.sendMessage(event, "Creating game for <@" + author.getId() + ">");
				tttGames.add(board = new TicTacToe(author));
				dataSaver.queueSaving();
			}
			else
				MessageSender.sendMessage(event, "Game already created for <@" + author.getId() + ">");
			MessageSender.sendMessage(event, board.toEmbed());
		}
		else if (board != null) {
			// move
			if (messagePhrases.length >= 3 && messagePhrases[1].equals("move")) {
				if (!board.movesLeft()) {
					MessageSender.sendMessage(event, "No moves remaining in <@" + author.getId() + ">'s " + moduleName + " game");
					removePlayerBoard(author);
				}
				else if (board.playMove(messagePhrases[2].toLowerCase())) {
					if (board.checkWin(board.getMoveRow(), board.getMoveCol(), ":x:")) {
						BotRunner.getXpQueue().addToQueue(author, "ttt_win");
						board.setGameResult("ttt_win");
						MessageSender.sendMessage(event, board.toEmbed());
						MessageSender.sendMessage(event, "<@" + author.getId() + "> has won " + moduleName + "!");
						removePlayerBoard(author);
					}
					else if (!board.movesLeft()) {
						BotRunner.getXpQueue().addToQueue(author, "ttt_tie");
						board.setGameResult("ttt_tie");
						MessageSender.sendMessage(event, board.toEmbed());
						MessageSender.sendMessage(event, "<@" + author.getId() + ">'s " + moduleName + " game ended in a tie!");
						removePlayerBoard(author);
					}
					else {
						board.moveAI();
						if (board.checkWin(board.getMoveRow(), board.getMoveCol(), ":o:")) {
							BotRunner.getXpQueue().addToQueue(author, "ttt_loss");
							board.setGameResult("ttt_loss");
							MessageSender.sendMessage(event, board.toEmbed());
							MessageSender.sendMessage(event, BotRunner.getBotName() + " has won " + moduleName + " against <@" + author.getId() + ">");
							removePlayerBoard(author);
						}
						else if (!board.movesLeft()) {
							BotRunner.getXpQueue().addToQueue(author, "ttt_tie");
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
			// get
			else if (messagePhrases.length >= 2 && messagePhrases[1].equals("get")) {
				MessageSender.sendMessage(event, board.toEmbed());
			}
			// end
			else if (messagePhrases.length >= 2 && messagePhrases[1].equals("end")) {
				removePlayerBoard(author);
				MessageSender.sendMessage(event, "<@" + author.getId() + ">'s " + moduleName + " game has ended");
			}
		}
		else
			MessageSender.sendMessage(event, "No board found for <@" + author.getId() + ">. Create one with the command \"" + moduleCommand + " start\"");

		// admin commands

		if (author.getId().equals("221748640236961792")) {
			// remove all/specific tictactoe games
			if (messagePhrases.length >= 3 && messagePhrases[1].equals("remove")) {
				List<Member> taggedMembers;
				if (messagePhrases[2].equals("all")) {
					System.out.println("Removing all " + moduleName + " games");
					MessageSender.sendMessage(event, "All " + moduleName + " games have been removed");
					tttGames = new ArrayList<>();
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
			// disable/enable saving
			else if (messagePhrases.length >= 3 && messagePhrases[1].equals("save")) {
				if (messagePhrases[2].equals("disable")) {
					System.out.println("Saving disabled");
					MessageSender.sendMessage(event, "Saving disabled");
					dataSaver.disableSaving();
				}
				else if (messagePhrases[2].equals("enable")) {
					System.out.println("Saving enabled");
					MessageSender.sendMessage(event, "Saving enabled");
					enableSaving();
				}
			}
		}
	}

	// creates DataSaver
	public void createDataSaver() {
		tttGames = new ArrayList<>();
		dataSaver = new DataSaver<>(moduleName, moduleDataPath, tttGames);
		tttGames = dataSaver.onStart();
		if (tttGames == null)
			tttGames = new ArrayList<>();
		System.out.println("Loaded " + tttGames.size() + " " + moduleName +  " games");
	}

	// enables dataSaver saving
	public void enableSaving() {
		dataSaver.enableSaving();
		System.out.println("Saved " + tttGames.size() + " " + moduleName + " games");
	}
}