package com.tentone.constellations.tree;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.tentone.constellations.elements.Creature;

public class QuadTree extends Rectangle
{
	private static final long serialVersionUID = 6162394780903176025L;

	//Max elements per node before subdivision
	public static final int max_elements = 10;

	//Parent and children pointer
	public QuadTree parent;
	public QuadTree children[];
	
	//Creatures and planets
	private int size;
	public ConcurrentLinkedQueue<Creature> elements;
	
	//Quad Tree constructor
	public QuadTree(float x, float y, float width, float height)
	{
		super(x, y, width, height);

		this.size = 0;
		
		this.parent = null;		
		this.children = null;
		
		this.elements = new ConcurrentLinkedQueue<Creature>();
	}

	public QuadTree(QuadTree parent, float x, float y, float width, float height)
	{
		super(x, y, width, height);

		this.size = 0;
		
		this.parent = parent;		
		this.children = null;
		
		this.elements = new ConcurrentLinkedQueue<Creature>();
	}
	
	//Get an iterator to the area of a point
	public void iterator()
	{
		//TODO <ADD CODE HERE>
	}
	
	//Add element to quad tree
	public void add(Creature creature)
	{
		if(this.size < QuadTree.max_elements)
		{
			this.elements.add(creature);
		}
		else
		{
			for(int i = 0; i < this.children.length; i++)
			{
				if(this.children[i].contains(creature))
				{
					this.children[i].add(creature);
					break;
				}
			}
		}
		
		size++;
		
		if(this.size == QuadTree.max_elements)
		{
			subdivide();
		}
	}
	
	//Remove element from quad tree
	public void remove(Creature creature)
	{
		//TODO <ADD CODE HERE>
		
		size--;
		
		if(this.size == QuadTree.max_elements)
		{
			aggregate();
		}
	}
	
	//Subdivide quad tree leaf
	public void subdivide()
	{
		float width = this.width / 2f;
		float height = this.height / 2f;
		
		this.children = new QuadTree[4];
		this.children[0] = new QuadTree(this, x, y + height, width, height);
		this.children[1] = new QuadTree(this, x + width, y + height, width, height);
		this.children[2] = new QuadTree(this, x, y, width, height);
		this.children[3] = new QuadTree(this, x + width, y, width, height);
		
		while(!this.elements.isEmpty())
		{
			Creature creature = this.elements.poll();
			
			for(int i = 0; i < this.children.length; i++)
			{
				if(this.children[i].contains(creature))
				{
					this.children[i].add(creature);
					break;
				}
			}
		}
	}
	
	//Aggregate elements and destroy children
	public void aggregate()
	{
		//TODO <ADD CODE HERE>
	}
	
	//Get size
	public int size()
	{
		return size;
	}
	
	//Debug the quad tree
	public void debug(ShapeRenderer shape)
	{
		shape.rect(x, y, width, height);
		
		if(this.children != null)
		{
			for(int i = 0; i < 4; i++)
			{
				this.children[i].debug(shape);
			}
		}
	}
}
