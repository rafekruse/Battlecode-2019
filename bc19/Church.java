package bc19;

public class Church extends StationairyRobot implements Machine {

	MyRobot robot;
	int depotNum;

	public Church(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		// robot.log("Church");
		if (robot.me.turn == 1) {
			SetDepotNumer();
		}
		Action output = null;

		int resources = ResourcesAround(robot, 3);
		int pilgrims = PilgrimsAround(robot, 3);
		if (resources > pilgrims) {
			Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
			if (buildHere != null && Helper.Have(robot, 110, 400)) {
				robot.signal(depotNum, 3);
				return robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
			}
		}

		output = EvaluateEnemyRatio(robot);

		return output;
	}

	public void SetDepotNumer() {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].signal <= 31 && robots[i].signal > 0) {
				depotNum = robots[i].signal;
			}
		}
	}
}