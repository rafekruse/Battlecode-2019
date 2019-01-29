package bc19;

import java.util.ArrayList;

public class Church extends StationairyRobot implements Machine {

	MyRobot robot;
	int[] spawnOrder;
	int positionInSpawnOrder = 0;

	public Church(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		// robot.log("Church : " + robot.location);

		if (robot.me.turn == 1) {
			spawnOrder = new int[] { robot.SPECS.PROPHET, robot.SPECS.CRUSADER, robot.SPECS.CRUSADER,
					robot.SPECS.CRUSADER, robot.SPECS.PREACHER };
		}
		Action output = null;
		int signal = -1;

		positionInSpawnOrder = positionInSpawnOrder == spawnOrder.length ? 0 : positionInSpawnOrder;
		if (Helper.Have(robot, 80 + robot.SPECS.UNITS[spawnOrder[positionInSpawnOrder]].CONSTRUCTION_KARBONITE, 500)) {
			Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
			if (buildHere != null) {
				output = robot.buildUnit(spawnOrder[positionInSpawnOrder], buildHere.x - robot.me.x,
						buildHere.y - robot.me.y);
				positionInSpawnOrder++;
			}
		}

		int resources = ResourcesAround(robot, 10);
		int pilgrims = UnitAround(robot, robot.location, 10, robot.SPECS.PILGRIM);
		if (!Helper.EnemiesAround(robot) && resources > pilgrims) {
			Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
			if (buildHere != null && Helper.Have(robot, 20, 100)) {
				output = robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
			}
		} else if (!Helper.EnemiesAround(robot) && resources == pilgrims) {
			if (robot.me.turn % 225 == 0) {
				Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
				if (buildHere != null && Helper.Have(robot, 20, 100)) {
					output = robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
				}
			}
		}

		if (Helper.EnemiesAround(robot)) {
			Action canBuildDefense = EvaluateEnemyRatio(robot);
			if (canBuildDefense != null) {
				signal = CreateAttackSignal(Helper.closestEnemy(robot, Helper.EnemiesWithin(robot, robot.visionRange)),
						8);
				output = canBuildDefense;
			} 
		}

		if (signal > -1) {
			robot.signal(signal, 3);
		}
		return output;
	}

}