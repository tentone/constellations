package com.tentone.constellations.tree;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.tentone.constellations.elements.Creature;
import com.tentone.constellations.utils.Generator;

public class QuadTree extends Rectangle
{
	private static final long serialVersionUID = 6162394780903176025L;

	//Max elements per node before subdivision
	public static final int max_elements = 10;

	//Parent and children pointer
	public QuadTree parent;
	public QuadTree children[];
	
	//Identification
	public int id;
	
	//Creatures and planets
	public ConcurrentLinkedQueue<Creature> elements;
	
	//Quad Tree constructor
	public QuadTree(float x, float y, float width, float height)
	{
		super(x, y, width, height);

		this.parent = null;		
		this.children = null;
		
		this.id = Generator.generateID();
		
		this.elements = new ConcurrentLinkedQueue<Creature>();
	}

	public QuadTree(QuadTree parent, float x, float y, float width, float height)
	{
		super(x, y, width, height);
		
		this.parent = parent;		
		this.children = null;
		
		this.id = Generator.generateID();
		
		this.elements = new ConcurrentLinkedQueue<Creature>();
	}
	
	//Add element to quad tree
	public boolean add(Creature creature)
	{
		//Check if element is inside this node
		if(this.contains(creature))
		{
			//If this node is a leaf
			if(this.isLeaf())
			{
				creature.parent = this;
				this.elements.add(creature);
				
				if(this.size() == QuadTree.max_elements + 1)
				{
					subdivide();
				}
				
				return true;
			}
			//If is not a leaf check where to put this element
			else
			{
				for(int i = 0; i < this.children.length; i++)
				{
					if(this.children[i].contains(creature))
					{
						return this.children[i].add(creature);
					}
				}
			}
		}
		//If does not contain the element and its not root add to parent
		else if(!this.isRoot())
		{
			return this.parent.add(creature);
		}
		
		return false;
	}
	
	//Remove element from quad tree
	public boolean remove(Creature creature)
	{
		boolean removed = false;
		
		creature.parent = null;
		
		//Try to remove from this node
		if(this.elements.remove(creature))
		{
			removed = true;
		}
		//If its not leaf remove from children
		else if(this.isLeaf())
		{
			for(int i = 0; i < this.children.length; i++)
			{
				if(this.children[i].remove(creature))
				{
					removed = true;
					break;
				}
			}
		}
		
		//If does not contain the element and its not root add to parent
		if(!removed && !this.isRoot())
		{
			return this.parent.remove(creature);
		}
		
		//Is size is less than the element limit try to aggregate
		if(this.size() <= QuadTree.max_elements)
		{
			this.aggregate();
		}
		
		return removed;
	}
	
	//Update creature location in the tree (should be called by the node containing this creature)
	public void update(Creature creature)
	{
		//Check if creature is still inside this node
		if(!this.contains(creature))
		{
			//Remove creature from elements and move it to a different branch
			this.remove(creature);
			
			//Add to parent
			this.add(creature);
		}
	}
	
	//Clear the quad tree
	public void clear()
	{
		this.children = null;
		this.elements.clear();
	}
	
	//Subdivide quad tree leaf
	public void subdivide()
	{
		if(this.isLeaf())
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
		else
		{
			System.err.println("Trying to divide middle node");
		}
	}
	
	//Aggregate elements and destroy children
	public void aggregate()
	{
		System.out.println("Aggregate");
		
		if(!this.isLeaf())
		{
			QuadTree[] child = this.children;
			
			this.children = null;
			
			System.out.println(this.size());
			
			for(int i = 0; i < child.length; i++)
			{
				while(!child[i].elements.isEmpty())
				{
					this.add(child[i].elements.poll());
				}
			}
		}
		else
		{
			System.err.println("Trying to aggregate a leaf");
		}
	}
	
	//Get size
	public int size()
	{
		int size = this.elements.size();
		
		if(!this.isLeaf())
		{
			for(int i = 0; i < this.children.length; i++)
			{
				size += this.children[i].size();
			}
		}
		
		return size;
	}
	
	//Check if quad tree is a leaf
	public boolean isLeaf()
	{
		return this.children == null;
	}
	
	//Check if quad tree is the root
	public boolean isRoot()
	{
		return this.parent == null;
	}
	
	//Debug the quad tree
	public void debug(ShapeRenderer shape)
	{
		shape.set(ShapeType.Line);
		shape.setColor(0.0f, 0.2f, 0.0f, 1.0f);
		shape.rect(x, y, width, height);
		
		shape.set(ShapeType.Filled);
		shape.setColor((float)this.elements.size()/(float)QuadTree.max_elements, 0.0f, 0.0f, 0.1f);
		shape.rect(x, y, width, height);
		
		if(!this.isLeaf())
		{
			for(int i = 0; i < 4; i++)
			{
				this.children[i].debug(shape);
			}
		}
	}
}
