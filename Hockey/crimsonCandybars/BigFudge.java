package crimsonCandybars;

import hockey.api.GoalKeeper;
import hockey.api.IObject;
import hockey.api.Position;
import hockey.api.Util;

public class BigFudge extends GoalKeeper {
	// Middle of our own goalcage, on the goal line
	protected static final Position GOAL_POSITION = new Position(-2650, 0);
	public static final Position OPPONENTS_GOAL = new Position(2600, 0);
	private static final Position THE_BIG_FUDGE_SPOT = new Position(-2500, 0);
	private static final Position ZLATAN_SPOT_UP = new Position(-2600 + 100, 400);
	private static final Position ZLATAN_SPOT_DOWN = new Position(-2600 + 100, -400);
	
	private static final int DIST_FROM_GOAL = 100;
	private static final int SKATE_SPEED = 100;
	private static final int RAGE_DISTANCE_OPPONENT = 1500;
	private static final int RAGE_DISTANCE_NEUTRAL = 750;

	// Number of the goalie.
	public int getNumber() { return 1; }

	// Name of the goalie.
	public String getName() { return "Big Fudge"; }

	// Left handed goalie
	public boolean isLeftHanded() { return true; }

	// Initiate
	public void init() { }

	// Face off
	public void faceOff() { }

	// Called when the goalie is about to receive a penalty shot
	public void penaltyShot() { }

	// Intelligence of goalie.
	public void step() {
		//!getPuck().isHeld() && Util.dist(getPuck(), GOAL_POSITION) < RAGE_DISTANCE_NEUTRAL || 
		if (hasPuck()) {
			setMessage("To the Zlatan spot!");
			Position spot = getY() > 0 ? ZLATAN_SPOT_UP : ZLATAN_SPOT_DOWN;
			if (Util.dist(spot, spot) < 100) {
				goodShoot(OPPONENTS_GOAL);
			} else {
				skate(spot, MAX_SPEED);
			}
		} else if (getPuck().isHeld() && getPuck().getHolder().isOpponent() && Util.dist(getPuck(), GOAL_POSITION) < RAGE_DISTANCE_OPPONENT) {
			setMessage("RAGE");
			skate(getPuck(), MAX_SPEED);
		} else if (Util.dist(this, THE_BIG_FUDGE_SPOT) > 200) {
			setMessage("FALL BACK");
			skate(THE_BIG_FUDGE_SPOT, MAX_SPEED);
		} else {
			setMessage("Line thingy");
			double px = getPuck().getX();
			double py = getPuck().getY();
			double dx = Math.abs(px - GOAL_POSITION.getX());
			double dy = py - GOAL_POSITION.getY();
			double gx = DIST_FROM_GOAL / dx;
			gx = Math.min(gx, 1);
			double gy = dy*gx;
			skate(GOAL_POSITION.getX() + DIST_FROM_GOAL, (int)gy, SKATE_SPEED);
			turn(getPuck(), MAX_TURN_SPEED);
		}
	}
	
	public void goodShoot(IObject goal) {
		turn(goal, MAX_TURN_SPEED);
		shoot(goal, MAX_SHOT_SPEED);
	}
}
