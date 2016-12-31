package com.tentone.constellations.tree;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.tentone.constellations.object.Creature;
import com.tentone.constellations.utils.Generator;

public class QuadTree extends Rectangle
{
	private static final long serialVersionUID = 6162394780903176025L;
	
	//Children tree position
	public static final int LEFT_UP = 0, RIGHT_UP = 1, LEFT_DOWN = 2, RIGHT_DOWN = 3;
	
	//Empty children array
	public static final QuadTree EMPTY[] = new QuadTree[0];
	
	//Parent and children pointer
	public QuadTree parent;
	public QuadTree children[];
	
	//Max elements per node before subdivision
	public int max_elements = 5;
	
	//Identification and level
	public int id, level;
	
	//Creatures
	public ConcurrentLinkedQueue<Creature> elements;
	
	//Quad Tree constructor
	public QuadTree(float x, float y, float width, float height)
	{
		super(x, y, width, height);

		this.parent = null;		
		this.children = EMPTY;
		
		this.id = Generator.generateID();
		this.level = 0;

		this.elements = new ConcurrentLinkedQueue<Creature>();
	}

	public QuadTree(QuadTree parent, float x, float y, float width, float height)
	{
		super(x, y, width, height);
		
		this.parent = parent;
		this.children = EMPTY;
		
		this.id = Generator.generateID();
		this.level = parent.level + 1;
		
		this.elements = new ConcurrentLinkedQueue<Creature>();
	}
	
	//Add element to quad tree
	public synchronized boolean add(Creature creature)
	{
		//Check if element is inside this node
		if(this.contains(creature))
		{
			//If this node is a leaf
			if(this.isLeaf())
			{
				creature.parent = this;
				
				this.elements.add(creature);
				
				if(this.size() > this.max_elements)
				{
					this.subdivide();
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
		//If its root and does not contain the creature create a new root and add this quadtree as its children
		else
		{
			//TODO <ADD CODE HERE>
			
			/*if(creature.x < this.x)
			{
				if(creature.y < this.y)
				{
					this.parent = new QuadTree(this.x - this.width, this.y - this.height, this.width * 2, this.height * 2);
					this.parent.subdivide();
					this.parent.children[2] = this;
				}
				else// if(creature.y > this.y + this.height)
				{
					this.parent = new QuadTree(this.x - this.width, this.y + this.height, this.width * 2, this.height * 2);
					this.parent.subdivide();
					this.parent.children[3] = this;
				}
			}
			else// if(creature.x > this.x + this.width)
			{

			}*/
		}
		
		return false;
	}
	
	//Remove element from quad tree
	public synchronized boolean remove(Creature creature)
	{
		boolean removed = false;

		//Try to remove from this node
		if(this.elements.remove(creature))
		{	
			creature.parent = null;
			removed = true;
		}
		//If its not leaf remove from children
		else if(!this.isLeaf())
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
		
		//If its not root
		if(!this.isRoot())
		{
			//If does not contain the element add to parent
			if(!removed)
			{
				removed = this.parent.remove(creature);
			}
			
			//Is size is less than the element limit try to aggregate
			if(this.parent.size() <= this.max_elements)
			{
				this.parent.aggregate();
			}
		}
		
		return removed;
	}
	
	//Update creature location in the tree (should be called by the node containing this creature)
	public synchronized void update(Creature creature)
	{
		//Check if creature is still inside this node
		if(!this.contains(creature))
		{
			//Remove creature from elements and move it to a different
			this.remove(creature);

			this.add(creature);
		}
	}
	
	//Subdivide quad tree leaf
	public synchronized void subdivide()
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
	}
	
	//Aggregate elements and destroy children
	public synchronized void aggregate()
	{
		if(this.level > 0 && !this.isLeaf())
		{
			QuadTree[] child = this.children;
			
			this.children = EMPTY;
			
			for(int i = 0; i < child.length; i++)
			{
				child[i].aggregate();
				
				while(!child[i].elements.isEmpty())
				{
					this.add(child[i].elements.poll());
				}
			}
		}
	}
	
	//Get linked list
	public ConcurrentLinkedQueue<Creature> toLinkedList()
	{
		ConcurrentLinkedQueue<Creature> list = new ConcurrentLinkedQueue<Creature>();
		
		fillLinkedList(this, list);
		
		return list;
	}
	
	//Fill a linked list with data from a quad tree
	public static void fillLinkedList(QuadTree node, ConcurrentLinkedQueue<Creature> list)
	{
		if(node.elements.size() > 0)
		{
			Iterator<Creature> it = node.elements.iterator();
			while(it.hasNext())
			{
				list.add(it.next());
			}
		}
		
		if(!node.isLeaf())
		{
			for(int i = 0; i < node.children.length; i++)
			{
				fillLinkedList(node.children[i], list);
			}
		}
	}
	
	//Clear the quad tree
	public void clear()
	{
		this.elements.clear();
		this.children = EMPTY;
	}
	
	//Iterator
	public Iterator<Creature> iterator()
	{
		return new QuadTreeIterator(this);
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
		return this.children == EMPTY;
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
		shape.setColor((float)this.elements.size()/(float)this.max_elements, 0.0f, 0.0f, 0.1f);
		shape.rect(x, y, width, height);
		
		if(!this.isLeaf())
		{
			for(int i = 0; i < this.children.length; i++)
			{
				this.children[i].debug(shape);
			}
		}
	}
}
