package com.blackcatstudios.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AStar {

	public static double lastTime = System.currentTimeMillis();
	
	private static Comparator<Node> nodeSorter = new Comparator<Node>() {
		@Override
		public int compare(Node n0, Node n1) {
			if(n1.fCost < n0.fCost)
				return +1;
			
			if(n1.fCost > n0.fCost)
				return -1;
			
			return 0;
		}
	};
	
	public static boolean clear() {
		if(System.currentTimeMillis() - lastTime >= 1000)
			return true;
		
		return false;
	}
	
	public static List<Node> findPath(World world, Vector2i start, Vector2i end){
		lastTime = System.currentTimeMillis();
		
		List<Node> openedList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		
		Node current = new Node(start, null, 0, getDistance(start, end));
		openedList.add(current);
		
		while(openedList.size() > 0) 
		{
			Collections.sort(openedList, nodeSorter);
			
			current = openedList.get(0);
			
			if(current.tile.equals(end)) 
			{
				List<Node> paths = new ArrayList<Node>();
				
				while(current.parent != null) 
				{
					paths.add(current);
					current = current.parent;
				}
				openedList.clear();
				closedList.clear();
				
				return paths;
			}
			
			openedList.remove(current);
			closedList.add(current);
			
			for(int i = 0; i < 9; i++) 
			{
				if(i == 4) //Essa posição é a do inimigo, não é necessário validar ela
					continue; 
				
				int x = current.tile.x;
				int y = current.tile.y;
				
				int xi = (i%3) - 1;
				int yi = (i/3) - 1;
				
				Tile tile = World.tiles[x + xi + ((y + yi) * world.WIDTH)];
				
				if(tile == null)
					continue;
				if(tile instanceof WallTile)
					continue;
				
				if(i == 0) 
				{
					Tile test = World.tiles[x + xi + 1 + ((y + yi) * world.WIDTH)];
					Tile test2 = World.tiles[x + xi + 1 + ((y + yi) * world.WIDTH)];
					
					if(test instanceof WallTile || test2 instanceof WallTile) 
						continue;
				}
				else if(i == 2) 
				{
					Tile test = World.tiles[x + xi + 1 + ((y + yi) * world.WIDTH)];
					Tile test2 = World.tiles[x + xi + ((y + yi) * world.WIDTH)];
					
					if(test instanceof WallTile || test2 instanceof WallTile) 
						continue;
				}
				else if(i == 6) 
				{
					Tile test = World.tiles[x + xi + ((y + yi - 1) * world.WIDTH)];
					Tile test2 = World.tiles[x + xi + 1 + ((y + yi) * world.WIDTH)];
					
					if(test instanceof WallTile || test2 instanceof WallTile) 
						continue;
				}
				else if(i == 8) 
				{
					Tile test = World.tiles[x + xi + ((y + yi - 1) * world.WIDTH)];
					Tile test2 = World.tiles[x + xi - 1 + ((y + yi) * world.WIDTH)];
					
					if(test instanceof WallTile || test2 instanceof WallTile) 
						continue;
				}
				
				Vector2i newVector = new Vector2i(x + xi, y + yi);
				double gCost = current.gCost + getDistance(current.tile, newVector);
				double hCost = getDistance(newVector, end);
				
				Node newNode = new Node(newVector, current, gCost, hCost);
				
				if(vectorInList(closedList, newVector) && gCost >= current.gCost)
					continue;
				
				if(!vectorInList(openedList, newVector))
					openedList.add(newNode);
				else if(gCost < current.gCost)
				{
					openedList.remove(current);
					openedList.add(newNode);
				}
			}
		}
		
		closedList.clear();
		return null;
	}
	
	private static boolean vectorInList(List<Node> list, Vector2i vector) {
		for(int i = 0; i < list.size(); i++) 
		{
			if(list.get(i).tile.equals(vector))
				return true;			
		}
		return false;
	}
	
	private static double getDistance(Vector2i tile, Vector2i goal) {
		double dx = tile.x - goal.x;
		double dy = tile.y - goal.y;
		
		return Math.sqrt(dx*dx + dy*dy);
	}
}
