package com.tentone.constellations.tree;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.tentone.constellations.elements.Creature;

public class QuadTree
{
	public QuadTree parent;
	public QuadTree children[];

	public ConcurrentLinkedQueue<Creature> creatures;
	
	//QuadTree constructor
	public QuadTree(QuadTree parent)
	{
		this.parent = parent;

		this.children = new QuadTree[4];
		this.creatures = new ConcurrentLinkedQueue<Creature>();
	}
}
