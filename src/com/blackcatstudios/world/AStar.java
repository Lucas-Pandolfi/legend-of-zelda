package com.blackcatstudios.world;

import java.util.*;

public class AStar {

    private static final int TILE_SIZE = 16;

    public static List<Node> findPath(int startX, int startY, int endX, int endY) {
        int sx = startX / TILE_SIZE;
        int sy = startY / TILE_SIZE;
        int ex = endX / TILE_SIZE;
        int ey = endY / TILE_SIZE;

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();

        Node start = new Node(sx, sy, null, 0, getDistance(sx, sy, ex, ey));
        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            if (current.x == ex && current.y == ey) {
                return reconstructPath(current);
            }

            closedList.add(current);

            for (int[] dir : new int[][] {
                { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 }
            }) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (!World.collidedWithWallTile(nx * TILE_SIZE, ny * TILE_SIZE, TILE_SIZE, TILE_SIZE)) continue;

                Node neighbor = new Node(nx, ny, current, current.gCost + 1, getDistance(nx, ny, ex, ey));

                if (closedList.contains(neighbor)) continue;

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                }
            }
        }

        return null;
    }

    private static double getDistance(int x1, int y1, int x2, int y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    private static List<Node> reconstructPath(Node end) {
        List<Node> path = new ArrayList<>();
        Node current = end;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}