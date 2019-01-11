package bc19;

import java.util.HashMap;
import java.util.ArrayList;

public class MyRobot extends BCAbstractRobot {
	public int turn;
	public int ourTeam;//red: 0 blue: 1
	public boolean[][] map;
	public boolean[][] karboniteMap;
	public boolean[][] fuelMap;
	public int numCastles = 0;
	public int castlesInitialized;
	public boolean mapIsHorizontal;
	public Position[] ourCastlePositions;
	public Position[] enemyCastlePositions;
	public HashMap<Position, int[][]> paths;


    public Action turn() {
		turn++;
		InitInfo();
  
 
    	if (me.unit == SPECS.CASTLE) {
			Castle castle = new Castle(this);
			//return castle.Execute();
		}

		
    	if (me.unit == SPECS.PILGRIM) {
			Pilgrim pilgrim = new Pilgrim(this);
			//return pilgrim.Execute();
		}
		return null;	
	}	
	void InitInfo(){
		if(turn == 1 && me.unit == SPECS.CASTLE){
			if(numCastles == 0){
				numCastles = getVisibleRobots().length;
				ourCastlePositions = new Position[numCastles];
				FindSymmetry();
				ourTeam = me.team == SPECS.RED ? 0 : 1;
			}			
			ourCastlePositions[castlesInitialized] = new Position(me.x, me.y);
			castlesInitialized++;
			if(castlesInitialized == numCastles){
				FindEnemyCastles();
			}
		}
	}
	void FindSymmetry(){
		for(int i = 0; i < map.length / 2; i++){
			for(int j = 0; j < map[i].length; j++){
				if(map[i][j] != map[(map.length - 1) - i][j]){
					mapIsHorizontal = false;
					return;
				}
			}
		}
		mapIsHorizontal = true;
	}
	void FindEnemyCastles(){
		enemyCastlePositions = new Position[numCastles];
		if (mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position(ourCastlePositions[i].x, (byte)((map[0].length - 1) - ourCastlePositions[i].y));
				log(enemyCastlePositions[i].toString());
			}
		}
		else if (!mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position((byte)((map.length - 1) - ourCastlePositions[i].x), ourCastlePositions[i].y);
				log(enemyCastlePositions[i].toString());
			}
		}
	}
	void GenerateCastlePaths(){
		for(int i = 0; i < ourCastlePositions.length; i++){
			paths.put(ourCastlePositions[i], Movement.CreateFloodPath(map, ourCastlePositions[i]));
		}
		for(int i = 0; i < ourCastlePositions.length; i++){

		}
	}
}

class Position{
	int x;
	int y;

	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}

	public String toString()
	{
		return Integer.toString(x) + " " + Integer.toString(y);
	}
}

class Castle extends BCAbstractRobot{

	MyRobot robot;

	public Castle(MyRobot robot){
		this.robot = robot;
	}
	public Action Execute(){
		return robot.buildUnit(robot.SPECS.PILGRIM,1,0);
	}

}

class Church extends BCAbstractRobot{

	MyRobot robot;

	public Church(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.buildUnit(robot.SPECS.PILGRIM,1,0);
	}
}

class Pilgrim extends BCAbstractRobot{

	MyRobot robot;
	
	public Pilgrim(MyRobot robot){
		this.robot = robot;
	}

	public Action Execute(){
		int dx = (int)(Math.random() * 3);
		int dy = (int)(Math.random() * 3);
		if(dx == 0 && dy == 0){
		dx++;
		}
		return robot.move(dx, dy);

	}

}

class Crusader extends BCAbstractRobot{
	
	MyRobot robot;

	public Crusader(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}
}

class Prophet extends BCAbstractRobot{
	
	MyRobot robot;

	public Prophet(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}
}

class Preacher extends BCAbstractRobot{
	
	MyRobot robot;
 
	public Preacher(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}
}

class Helper extends BCAbstractRobot{
	public static boolean inMap(boolean[][] map, Position pos)
	{
		if (pos.x < 0 || pos.x > (map.length - 1) || pos.y < 0 || pos.y > (map[0].length - 1))
		{
			return false;
		}
		return true;	
	}

	public static Position[] AllPassableInRange(boolean[][] map, Position pos, int[] r)
	{
		ArrayList<Position> validPositions = new ArrayList<>();
		for (int i = -r[1]; i <= r[1]; i++)
		{
			for (int j = -r[1]; j <= r[1]; j++)
			{
				int x = (pos.x + i);
				int y = (pos.y + j);
				if (!inMap(map, new Position(x, y)))
				{
					continue;
				}
				if(!map[x][y]){
					continue;
				}
				int distanceSquared = (x - pos.x) * (x - pos.x) + (y - pos.y) * (y - pos.y);
				if (distanceSquared > r[1] || distanceSquared < r[0])
				{
					continue;
				}
				
				validPositions.add(new Position(x, y));
			}
		}
		return validPositions.toArray(new Position[validPositions.size()]);
	}
	public static float DistanceSquared(Position pos1, Position pos2){
		return (pos2.x - pos1.x) * (pos2.x - pos1.x) + (pos2.y - pos1.y) * (pos2.y - pos1.y);
	}
}

class Movement extends BCAbstractRobot{

	public static int[][] CreateFloodPath(boolean[][] map, Position pos){
		int[][] output = new int[map.length][map[0].length];
		Flood(map, output, pos, 0);
		return output;
	}
	static void Flood(boolean[][] map, int[][] floodMap, Position pos, int prev){
		if(!Helper.inMap(map, pos)){
			return;
		}
		if(!map[pos.x][pos.y]){
			floodMap[pos.x][pos.y] = -1;
		}
		else if(floodMap[pos.x][pos.y] == 0){
			floodMap[pos.x][pos.y] = prev + 1;
		}
		Flood(map, floodMap, new Position(pos.x + 1, pos.y), prev + 1);
		Flood(map, floodMap, new Position(pos.x, pos.y + 1), prev + 1);
		Flood(map, floodMap, new Position(pos.x - 1, pos.y), prev + 1);
		Flood(map, floodMap, new Position(pos.x, pos.y - 1), prev + 1);
		//might consider 8 directional

	}
	int PathingDistance(int[][] path)
	{
		return path[me.x][me.y];
	}
	
	Position FloodPathing(int[][] path)
	{
		if (path == null)
		{
			return null;	
		}

		Position[] validPositions = Helper.AllPassableInRange(map, new Position(me.x, me.y), SPECS.UNITS[me.unit].ATTACK_RADIUS);
		int lowest = Integer.MAX_VALUE;
		Position lowestPos = null;

		for (int i = 0; i < validPositions.length ; i++)
		{
			if (path[validPositions[i].x][validPositions[i].y] < lowest)
			{
				lowest = path[validPositions[i].x][validPositions[i].y];
				lowestPos = validPositions[i];
			}
		}
		return lowestPos;
	}
}

class Attack extends BCAbstractRobot{

	public Action AttackClosest()
	{
		Robot[] visibleRobots = getVisibleRobots();
		int leastDistance = Integer.MAX_VALUE;
		int botIndex = -1;
		for (int i = 0; i < visibleRobots.length; i++)
		{
			if (visibleRobots[i].team != ourTeam)
			{
				if ()
			}
		}	
	}
}


//Wandering
//Moving to Something In vision
//Run Away
