package com.tentone.constellations.tree;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.tentone.constellations.object.Creature;

public class QuadTreeIterator implements Iterator<Creature>
{
	public ConcurrentLinkedQueue<Iterator<Creature>> list;
	public Iterator<Iterator<Creature>> iterator;
	public Iterator<Creature> actual;
	
	public QuadTreeIterator(QuadTree root)
	{
		this.list = new ConcurrentLinkedQueue<Iterator<Creature>>();
		this.getIterators(root);
		
		this.iterator = this.list.iterator();
		
		this.actual = this.iterator.hasNext() ? this.iterator.next() : root.elements.iterator();
	} 
	
	//Get iterators for all leafs in the three
	public void getIterators(QuadTree node)
	{
		if(node.elements.size() > 0)
		{
			list.add(node.elements.iterator());
		}
		
		if(node.children != null)
		{
			for(int i = 0; i < node.children.length; i++)
			{
				getIterators(node.children[i]);
			}
		}
	}
	
	//Returns if there is next element
	public boolean hasNext()
	{
		return actual.hasNext() || iterator.hasNext();
	}
	
	//Get next element
	public Creature next()
	{ 
		Creature elem = actual.next();
		
		if(!actual.hasNext() && iterator.hasNext())
		{
			actual = iterator.next();
		}
		
		return elem;
	}
}
