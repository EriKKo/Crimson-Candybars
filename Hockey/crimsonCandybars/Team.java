package crimsonCandybars;

import java.awt.Color;

import hockey.api.GoalKeeper;
import hockey.api.Player;
import hockey.api.ITeam;

public class Team implements ITeam {
    // Team Short Name.  Max 4 characters.
    public String getShortName() { return "CCB"; }

    // Team Name
    public String getTeamName() { return "Crimson Candybars"; }

    // Team color; body color
    public Color getTeamColor() { return new Color(141, 55, 65); }

    // Team color; helmet color.
    public Color getSecondaryTeamColor() { return Color.BLUE; }

    // The team's LUCKY NUMBER!!
    public int getLuckyNumber() { return 0; }

    // Get the goal keeper of the team.
    public GoalKeeper getGoalKeeper() { return new BigFudge(); }

    // Get the other five players of the team.
    public Player getPlayer(int index) { //fix
    	if(index==1||index==2) {
    		return new CrimsonPlayer(CrimsonPlayer.DEFENDER, index);
    	}
		return new CrimsonPlayer(CrimsonPlayer.ATTACKER, index);
    }
}