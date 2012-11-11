package crimsonCandybars;

import hockey.api.IObject;
import hockey.api.Player;
import hockey.api.Position;
import hockey.api.Util;

public class CrimsonPlayer extends Player {
	
	static final int[] numbers = {2, 3, 5, 8, 13};
	
	public static final int DEFENDER = 1;
	public static final int ATTACKER = 2;
	
	public static final int A = 1733;
	public static final int AIM_DISTANCE_FROM_GOALPOST = 10;
	public static final int SHOOT_DISTANCE_FROM_GOAL = 1300;
	public static final int SHOOT_DISTANCE_PENALTY = 1300;
	public static final int COLLISION_VISION = 700;
	
	public static final Position OPPONENTS_GOAL = new Position(2600, 0);
	public static final Position OUR_GOAL = new Position (-2600, 0);
	public static final Position MIDDLE = new Position(0, 0);
	public static final Position SHOOT_OPTION_UP = new Position(2600, 90 - AIM_DISTANCE_FROM_GOALPOST);
	public static final Position SHOOT_OPTION_DOWN = new Position(2600, -90 + AIM_DISTANCE_FROM_GOALPOST);
	public static final Position PENALTY_TURN_POINT = new Position(500, 200);

	public int type;
	public int index;
	boolean penalty = false;
	boolean turnPointVisited = false;
	
	public CrimsonPlayer(int type, int index) {
		this.type = type;
		this.index = index;
	}
	
	public void faceOff() {
		penalty = false;
	}
	
	@Override
	public String getName() {
		switch(index) {
			case 1: return "The Jaeger";
			case 2: return "The Meister";
			case 3: return "The Cola";
			case 4: return "The Coke";
			case 5: return "Center plopp";
			default: return "";
		}
	}

	@Override
	public int getNumber() {
		return numbers[index - 1];
	}

	@Override
	public boolean isLeftHanded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void step() throws Exception {
		if (!hasPuck()) fiddleStick();
		if (penalty) {
			attack();
		} else if (type == DEFENDER) {
			stepDefender();
		} else {
			attack();
		}
	}
	
	// DEFEND METHODS
	
	public void stepDefender() {
		if(hasPuck()) {
			attackGoal();
		} else if(getPuck().isHeld()) {
			if(getPuck().getHolder().isOpponent()&&puckOnOurSide()&&isClosestDefenderToPuck()) {
				tackleDude();
			} else {
				defendGoal();
			}
		} else {
			defendGoal();
		}
	}
	
	public void defendGoal() {
		if(index%2==0) {
			goodSkate(new Position(OUR_GOAL.getX()+500, OUR_GOAL.getY()-50));
		} else {
			goodSkate(new Position(OUR_GOAL.getX()+500, OUR_GOAL.getY()+50));
		}
	}
	
	public void tackleDude() {
		skate(getPuck(), Player.MAX_SPEED);
	}
	
	// ATTACK METHODS
	
	public void attack() {
		if (hasPuck()) {
			attackGoal();
		} else if (getPuck().isHeld()) {
			if (getPuck().getHolder().isOpponent()) {
				skate(getPuck(), MAX_SPEED);
			} else {
				goodSkate(MIDDLE);
			}
		} else {
			skate(getPuck(), MAX_SPEED);
		}
	}
	
	public void attackGoal() {
		int shootRange = penalty ? SHOOT_DISTANCE_PENALTY : SHOOT_DISTANCE_FROM_GOAL;
		if (getX() > 2500) {
			goodSkate(MIDDLE);
		} else if (Util.dist(this, OPPONENTS_GOAL) > shootRange) {
			if (penalty) {
				if (Util.dist(this, PENALTY_TURN_POINT) < 200) turnPointVisited = true;
				if (turnPointVisited) {
					skate(OPPONENTS_GOAL, MAX_SPEED);
				} else {
					skate(PENALTY_TURN_POINT, MAX_SPEED);
				}
			} else {
				goodSkate(OPPONENTS_GOAL);
			}
		} else {
			setMessage("Shooting");
			shootOnGoal();
		}
	}
	
	public void shootOnGoal() {
		int goalieY = getPlayer(6).getY();
		int ourY = getY();
//		if (goalieY >= 0) {
//			goodShoot(SHOOT_OPTION_UP);
//		} else {
//			goodShoot(SHOOT_OPTION_DOWN);
//		}
		if (getY() < 0) {
			goodShoot(SHOOT_OPTION_DOWN);
		} else {
			goodShoot(SHOOT_OPTION_UP);
		}
	}
	
	// UTILITY METHODS
	
	public int getClosestToPuck(int startIndex, int endIndex) {
		int closestPlayer = -1;
		double closestDistance = 10000000;
		for(int i = startIndex; i <= endIndex; i++) {
			double dist = Util.dist(getPlayer(i), getPuck());
			if(dist<closestDistance) {
				closestDistance = dist;
				closestPlayer=i;
			}
		}
		return closestPlayer;
	}
	
	public boolean isClosestDefenderToPuck() {
		return getClosestToPuck(1, 2) == index;
	}
	
	public boolean isClosestAttackerToPuck() {
		return getClosestToPuck(3, 5) == index;
	}
	
	int playersInCircle(Position circleMid, int r) {
		int res = 0;
		if (circleMid.getX() - r < -3000 || circleMid.getX() + r > 3000 ||
				circleMid.getY() - r < -1500 || circleMid.getY() + r > 1500) res = 100;
		for (int i = 0; i < 12; i++) {
			if (i == index || i == 6) continue;
			if (Util.dist(circleMid, getPlayer(i)) <= r) res++;
		}
		return res;
	}
	
	public int getClosestAllyToRight() {
		int closestPlayer =-1;
		double closestDistance = 6000; //lol
		for(int i = 1; i<=5;i++) {
			if(getPlayer(i).getX()<=getX())
				continue;
			double dist = Util.dist(getPlayer(i), this);
			if(dist<closestDistance) {
				closestDistance = dist;
				closestPlayer=i;
			}
		}
		return closestPlayer;
	}
	
	public boolean puckOnOurSide() {
		return (getPuck().getX()<0&&getPuck().getY()<0);
	}
	
	public void goodSkate(IObject goal) {
		int dir = (int)Util.datan2(goal, this);
		Position c1 = getCircle(getX(), getY(), dir, COLLISION_VISION-35);
		Position c2 = getCircle(getX(), getY(), dir + 90, COLLISION_VISION+35);
		Position c3 = getCircle(getX(), getY(), dir - 90, COLLISION_VISION+35);
		int cnt1 = playersInCircle(c1, COLLISION_VISION);
		int cnt2 = playersInCircle(c2, COLLISION_VISION);
		int cnt3 = playersInCircle(c3, COLLISION_VISION);
		int m = Math.min(cnt1, Math.min(cnt2, cnt3));
		IObject decidedGoal;
		if (cnt1 <= m + 1) {
			decidedGoal = goal;
			skate(goal, MAX_SPEED);
			setMessage("Skating forward");
		} else if (cnt2 == m) {
			decidedGoal = c2;
			skate(c2, MAX_SPEED);
			setMessage("Turning left");
		} else {
			decidedGoal = c3;
			skate(c3, MAX_SPEED);
			setMessage("Turning right");
		}
		if (Math.abs(Util.datan2(decidedGoal, this) - getHeading()) < 1) {
			skate(decidedGoal, MAX_SPEED);
		} else {
			turn(decidedGoal, MAX_TURN_SPEED);
		}
	}
	
	public Position getCircle(int x, int y, int dir, int r) {
		return new Position((int)(x + r*Util.cosd(dir)),(int) (y + r*Util.sind(dir)));
	}
	
	public void fiddleStick() {
		moveStick((int)Util.datan2(getPuck(), this), (int)Util.dist(this, getPuck()));
	}
	
	public void goodShoot(IObject goal) {
		shoot(goal, MAX_SHOT_SPEED);
	}
	
	public void penaltyShot() { 
		penalty = true;
		turnPointVisited = false;
	}
}