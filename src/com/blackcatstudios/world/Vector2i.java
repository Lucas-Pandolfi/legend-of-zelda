package com.blackcatstudios.world;

public class Vector2i {

	public int x, y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
	    if (!(obj instanceof Vector2i)) 
	    	return false;
	    
	    Vector2i vec = (Vector2i) obj;
	    
	    return this.x == vec.x && this.y == vec.y;
	}
}
