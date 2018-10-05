package com.micahdesilva.syntle.NEAT;

import java.util.ArrayList;

public class Node
{
	public enum NodeType
	{
		INPUT, OUTPUT, HIDDEN
	}

	public final long nodeID;
	final double x, y;
	final NodeType nodeType;

	Node(NodeType nodeType, double x, double y, long id)
	{
		nodeID = id;
		this.x = x;
		this.y = y;
		this.nodeType = nodeType;
	}
}
