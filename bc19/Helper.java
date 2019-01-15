package bc19;

import java.util.ArrayList;

public class Helper{
	public static boolean inMap(boolean[][] map, Position pos)
	{
		if (pos.y < 0 || pos.y > (map.length - 1) || pos.x < 0 || pos.x > (map[0].length - 1))
		{
			return false;
		}
		return true;	
	}

	public static Position[] AllOpenInRange(MyRobot robot, boolean[][] map, Position pos, int r)
	{
		ArrayList<Position> validPositions = new ArrayList<>();
		for (int i = -r; i <= r; i++)
		{
			for (int j = -r; j <= r; j++)
			{
				int y = (pos.y + i);
				int x = (pos.x + j);
				if (!inMap(map, new Position(y, x)) || robot.getVisibleRobotMap()[y][x] != 0)
				{
					continue;
				}
				int distanceSquared = (y - pos.y) * (y - pos.y) + (x - pos.x) * (x - pos.x);
				if (distanceSquared > r)
				{
					continue;
				}
				
				validPositions.add(new Position(y, x));
			}
		}
		return validPositions.toArray(new Position[validPositions.size()]);
	}

	public static float DistanceSquared(Position pos1, Position pos2){
		return (pos2.y - pos1.y) * (pos2.y - pos1.y)  + (pos2.x - pos1.x) * (pos2.x - pos1.x);
	}
	
    public static boolean FindSymmetry(boolean[][] map){
        boolean mapIsHorizontal = true;
		for(int i = 0; i < map.length / 2; i++){
			for(int j = 0; j < map[i].length; j++){
				if(map[i][j] != map[(map.length - 1) - i][j]){
                    mapIsHorizontal = false;
                    return mapIsHorizontal;
				}
			}
        }
        return mapIsHorizontal;
	}
	
    public static Position FindEnemyCastle(boolean[][] map, boolean mapIsHorizontal, Position ourCastle){
        return mapIsHorizontal ? new Position(ourCastle.y, (map[0].length - 1) - ourCastle.x) : new Position((map.length - 1) - ourCastle.y, ourCastle.x);
	}
	
	public static Robot RobotAtPosition(MyRobot robot, Position pos)
	{
		Robot[] visibleRobots = robot.getVisibleRobots();
		for (int i = 0; i < visibleRobots.length; i++)
		{
			if (pos.y == visibleRobots[i].y && pos.x == visibleRobots[i].x)
			{
				return visibleRobots[i];
			}
		}
		return null;
	}
	
	public static boolean PositionInVision(MyRobot robot, Position pos)
	{
		Position robotPos = new Position(robot.me.y, robot.me.x);
		if (DistanceSquared(robotPos, pos) <= robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS)
		{
			return true;
		}
		return false;
	}

	public static boolean IsSurroundingsOccupied(MyRobot robot, int[][] map, Position pos)
	{
		for (int i = -2; i <= 2; i++)
		{
			for (int j = -2; j <= 2; j++)
			{
				if (map[pos.y + i][pos.x + j] == 0)
				{
					return false;
				}
			}
		}
		return true;
	}
	public static Position RandomNonResourceAdjacentPositionInMoveRange(MyRobot robot, Position pos)
	{
		int[][] robots = robot.getVisibleRobotMap();
		boolean[][] fuelMap = robot.getFuelMap();
		boolean[][] karbMap = robot.getKarboniteMap();
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				Position adjacent = new Position(pos.y + i, pos.x + j);
				if(Helper.inMap(robot.map, adjacent) && robot.map[adjacent.y][adjacent.x] && Helper.DistanceSquared(new Position(robot.me.y, robot.me.x), adjacent) <= robot.SPECS.UNITS[robot.me.unit].SPEED){
					if (robot.map[adjacent.y][adjacent.x] && robots[adjacent.y][adjacent.x] == 0 && fuelMap[adjacent.y][adjacent.x] == false && karbMap[adjacent.y][adjacent.x] == false)
					{
						return new Position(adjacent.y, adjacent.x);
					}
				}
			}
		}	
	
		return null;
	}

	public static Position RandomNonResourceAdjacentPosition(MyRobot robot, Position pos)
	{
		int[][] robots = robot.getVisibleRobotMap();
		boolean[][] fuelMap = robot.getFuelMap();
		boolean[][] karbMap = robot.getKarboniteMap();
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if(Helper.inMap(robot.map, new Position(pos.y + i, pos.x + j)) && robot.map[pos.y + i][pos.x + j]){
				if (robot.map[pos.y + i][pos.x + j] && robots[pos.y + i][pos.x + j] == 0 && fuelMap[pos.y + i][pos.x + j] == false && karbMap[pos.y + i][pos.x + j] == false)
				{
					return new Position(pos.y + i, pos.x + j);
				}
			}
			}
		}
		return null;
	}
	public static boolean CanAfford(MyRobot robot, int unit){
		if(robot.karbonite > robot.SPECS.UNITS[unit].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[unit].CONSTRUCTION_FUEL){
			return true;
		}
		return false;
		
	}
}