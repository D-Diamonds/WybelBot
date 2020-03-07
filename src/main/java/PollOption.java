import java.io.Serializable;

public class PollOption implements Serializable {
	private String option;
	private int votes = 0;

	public PollOption(String option) {
		this.option = option;
	}

	public int getVotes() {
		return votes;
	}

	public String getOption() {
		return option;
	}

	public void increment() {
		votes++;
	}
}
