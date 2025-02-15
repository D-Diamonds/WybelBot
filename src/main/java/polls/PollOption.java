package polls;

import java.io.Serializable;

public class PollOption implements Serializable {
	private final static long serialVersionUID = 104L;

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
