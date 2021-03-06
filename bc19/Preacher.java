package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Preacher extends MovingRobot implements Machine {

	MyRobot robot;
	boolean initialized;
	Robot parent;
	Position parentLocation;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	Position defensePosition;
	boolean manualFort;
	int fortCount;
	Position changeCheck;

	public Preacher(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		robot.log("Preacher : " + robot.location);
		if (robot.me.turn == 1) {
			InitializeVariables();
			parent = StructureBornFrom(robot);
			parentLocation = new Position(parent.y, parent.x);
			if (parent.unit == robot.SPECS.CHURCH) {
				initialized = true;
			}
		}
		if (!initialized) {
			CastleInit();
		}
		if (UpdateBattleStatus(robot, enemyCastleLocations, changeCheck) != changeCheck) {
			changeCheck = UpdateBattleStatus(robot, enemyCastleLocations, changeCheck);
		}
		Position invader = ListenForDefense(robot);

		if (Helper.Have(robot, 0, 50)) {
			if (Helper.EnemiesAround(robot)) {
				return AttackEnemies();
			} else if (invader != null) {
				if (defensePosition == null) {
					ArrayList<Position> defensePositions = GetValidDefense(robot, routesToEnemies, parentLocation,
							invader);
					defensePosition = Helper.ClosestPosition(robot, defensePositions);
				}
				if (!defensePosition.equals(robot.location)) {
					return MoveCloser(robot, defensePosition, false);
				}
			}
		}

		if (Helper.Have(robot, 0, 50) && robot.currentHealth < robot.previousHealth && EnemiesOfTypeInVision(robot,
				new int[] { robot.SPECS.CRUSADER, robot.SPECS.PREACHER, robot.SPECS.PROPHET }).size() == 0) {
			Position otherSide = Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, robot.location);
			return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, otherSide, false), otherSide, false,
					new ArrayList<Robot>());
		}
		if (initialized && Helper.Have(robot, 0, 325)) {
			robot.log("Fortified " + !Fortified(robot, robot.location));
			if (changeCheck == null && !Fortified(robot, robot.location) && !manualFort) {
				fortCount++;
				if (fortCount > 10 && Helper.DistanceSquared(parentLocation, robot.location) <= 100) {
					manualFort = true;
				}
				ArrayList<Position> valid = GetValidFortifiedPositions(robot, parentLocation);
				if (valid.size() > 0) {
					Position closest = Helper.ClosestPosition(robot, valid);
					float[][] shortPath = CreateLayeredFloodPath(robot, closest, robot.location);
					return FloodPathing(robot, shortPath, closest, false, new ArrayList<Robot>());
				} else {

					Position goal = null;
					if (robot.mapIsHorizontal) {
						if (robot.me.y > robot.map.length / 2) {
							goal = new Position(0, robot.me.x);
						} else {
							goal = new Position(robot.map.length - 1, robot.me.x);
						}
					} else {
						if (robot.me.x > robot.map.length / 2) {
							goal = new Position(robot.me.y, 0);
						} else {
							goal = new Position(robot.me.y, robot.map.length - 1);
						}
					}
					return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, goal, false), goal, false,
							new ArrayList<Robot>());
				}
			} else if (changeCheck != null) {
				boolean rushTime = true;
				CastleDown(robot, enemyCastleLocations, routesToEnemies);

				Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies, enemyCastleLocations);
				if (closestEnemyCastle != null && Helper.DistanceSquared(robot.location, closestEnemyCastle) <= 196) {
					rushTime = false;
				}
				return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true),
						closestEnemyCastle, rushTime, new ArrayList<Robot>());
			}

		}

		return null;
	}

	void CastleInit() {
		initialized = ReadCombatSignals(robot, castleLocations);
		if (initialized) {
			enemyCastleLocations = Helper.FindEnemyCastles(robot, robot.mapIsHorizontal, castleLocations);
			for (int i = 0; i < enemyCastleLocations.size(); i++) {
				GetOrCreateMap(robot, routesToEnemies, enemyCastleLocations.get(i), false);
			}
		}
	}

	void InitializeVariables() {
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
	}

	public Action AttackEnemies() {
		int most = Integer.MIN_VALUE;
		Position attackTile = null;
		for (int i = -robot.tileVisionRange; i <= robot.tileVisionRange; i++) {
			for (int j = -robot.tileVisionRange; j <= robot.tileVisionRange; j++) {
				Position checkTile = new Position(robot.me.y + i, robot.me.x + j);
				if (Helper.inMap(robot.map, checkTile)
						&& Helper.DistanceSquared(checkTile, robot.location) <= robot.visionRange) {
					int mostEnemies = NumAdjacentEnemies(checkTile);
					if (mostEnemies > most) {
						most = mostEnemies;
						attackTile = checkTile;
					}
				} else {
					continue;
				}
			}
		}
		return robot.attack(attackTile.x - robot.me.x, attackTile.y - robot.me.y);
	}

	public int NumAdjacentEnemies(Position pos) {
		int numEnemies = 0;
		Robot[] robots = robot.getVisibleRobots();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = 0; k < robots.length; k++) {
					if (pos.y + i == robots[k].y && pos.x + j == robots[k].x) {
						if (robots[k].team == robot.ourTeam && robots[k].unit == robot.SPECS.CASTLE) {
							numEnemies -= 8;
						} else if (robots[k].team == robot.ourTeam) {
							numEnemies--;
						} else {
							numEnemies++;
						}
					} else {
						continue;
					}
				}
			}
		}
		return numEnemies;
	}

}