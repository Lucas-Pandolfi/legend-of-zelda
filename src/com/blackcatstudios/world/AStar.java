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
	
	public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
	    lastTime = System.currentTimeMillis();

	    List<Node> openedList = new ArrayList<>();
	    List<Node> closedList = new ArrayList<>();

	    Node current = new Node(start, null, 0, getDistance(start, end));
	    openedList.add(current);

	    while (openedList.size() > 0) {
	        Collections.sort(openedList, nodeSorter);
	        current = openedList.get(0);

	        if (current.tile.equals(end)) {
	            List<Node> paths = new ArrayList<>();
	            while (current.parent != null) {
	                paths.add(current);
	                current = current.parent;
	            }
	            openedList.clear();
	            closedList.clear();
	            return paths;
	        }

	        openedList.remove(current);
	        closedList.add(current);

	        for (int i = 0; i < 9; i++) 
	        {
	            if (i == 4) // Posição atual
	                continue;

	            int x = current.tile.x;
	            int y = current.tile.y;

	            int xi = (i % 3) - 1;
	            int yi = (i / 3) - 1;

	            int targetX = x + xi;
	            int targetY = y + yi;

	            if (targetX < 0 || targetY < 0 || targetX >= world.WIDTH || targetY >= world.HEIGHT)
	                continue;

	            Tile tile = World.tiles[targetX + (targetY * world.WIDTH)];
	            if (tile == null || tile instanceof WallTile)
	                continue;

	            // Verificação de diagonais
	            if (xi != 0 && yi != 0) 
	            {
	                int adjX = x + xi;
	                int adjY = y;
	                int adjY2 = y + yi;
	                int adjX2 = x;

	                if (adjX < 0 || adjY2 < 0 || adjX >= world.WIDTH || adjY2 >= world.HEIGHT)
	                    continue;

	                Tile adjTile1 = World.tiles[adjX + (y * world.WIDTH)];
	                Tile adjTile2 = World.tiles[x + (adjY2 * world.WIDTH)];

	                if (adjTile1 instanceof WallTile || adjTile2 instanceof WallTile)
	                    continue;
	            }

	            Vector2i newVector = new Vector2i(targetX, targetY);
	            double gCost = current.gCost + getDistance(current.tile, newVector);
	            double hCost = getDistance(newVector, end);

	            Node newNode = new Node(newVector, current, gCost, hCost);

	            if (vectorInList(closedList, newVector) && gCost >= current.gCost)
	                continue;

	            if (!vectorInList(openedList, newVector))
	                openedList.add(newNode);
	            else if (gCost < current.gCost) 
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
