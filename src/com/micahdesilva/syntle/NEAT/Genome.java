package com.micahdesilva.syntle.NEAT;

import java.util.ArrayList;

public class Genome
{
	ArrayList<Connection> connections;
	ArrayList<Long> nodeIDs;
	long fitness = 0;
	String inheritance;

	Genome(ArrayList<Connection> connections, ArrayList<Long> nodes, String inheritance)
	{
		this.inheritance = inheritance;
		this.connections = new ArrayList<Connection>();
		nodeIDs = new ArrayList<Long>();
		if (connections.size() > 0)
		{
			for (Connection c : connections)
			{
				this.connections.add(new Connection(c.left, c.right, c.weight, c.enabled, c.id));
			}
		}

		for (long nodeID : nodes)
		{
			nodeIDs.add(nodeID);
		}
	}
}
