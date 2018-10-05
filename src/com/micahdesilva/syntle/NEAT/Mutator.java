package com.micahdesilva.syntle.NEAT;

import java.util.ArrayList;
import java.util.Random;

import com.micahdesilva.syntle.NEAT.Node.NodeType;

public class Mutator
{
	static float pointMutateRate = 0.3f, linkMutateRate = 0.1f, nodeMutateRate = 0.05f, enableMutateRate = 0.2f;
	static Random rand = new Random();

	static void mutate(ArrayList<Long> nodeIDs, EvolutionHandler e, ArrayList<Connection> connections)
	{
		if (rand.nextDouble() < pointMutateRate)
			pointMutate(e, connections);
		if (rand.nextDouble() < linkMutateRate)
			linkMutate(nodeIDs, e, connections);
		if (rand.nextDouble() < nodeMutateRate)
			nodeMutate(nodeIDs, e, connections);
		if (rand.nextDouble() < enableMutateRate)
			enableMutate(e, connections);
	}

	static void enableMutate(EvolutionHandler e, ArrayList<Connection> connections)
	{
		if (!connections.isEmpty())
			connections.get(rand.nextInt(connections.size())).toggleEnabled();
	}

	static void nodeMutate(ArrayList<Long> nodeIDs, EvolutionHandler e, ArrayList<Connection> connections)
	{
		if (!connections.isEmpty())
		{
			Connection c = connections.get(rand.nextInt(connections.size()));
			if (c.disable())
			{
				Node leftNode = e.getNodeByID(c.left);
				Node rightNode = e.getNodeByID(c.right);
				Node n = new Node(NodeType.HIDDEN, (leftNode.x + rightNode.x) / 2, (leftNode.y + rightNode.y) / 2 + 0.1 - rand.nextDouble() * 0.2, e.getNextNodeID());
				e.nodes.add(n);
				nodeIDs.add(n.nodeID);
				Connection replacementA = new Connection(c.left, n.nodeID, c.weight / 2, true, e.getNextConnectionID());
				Connection replacementB = new Connection(n.nodeID, c.right, c.weight / 2, true, e.getNextConnectionID());
				connections.add(replacementA);
				connections.add(replacementB);
				e.connections.add(new Connection(c.left, n.nodeID, replacementA.weight, true, replacementA.id));
				e.connections.add(new Connection(n.nodeID, c.right, replacementB.weight, true, replacementB.id));
			}
		}
	}

	static void linkMutate(ArrayList<Long> nodeIDs, EvolutionHandler e, ArrayList<Connection> connections)
	{

		Node leftNode = e.getNodeByID(nodeIDs.get(rand.nextInt(nodeIDs.size())));

		if (leftNode.nodeType != NodeType.OUTPUT)
		{
			ArrayList<Node> connectableNodes = new ArrayList<Node>();
			for (Node node : e.getNodes())
			{
				if (nodeIDs.contains(node.nodeID))
				{
					if (node.x > leftNode.x)
						connectableNodes.add(node);
				}
			}

			Node rightNode = connectableNodes.get(rand.nextInt(connectableNodes.size()));

			long connectionID = connectionExists(leftNode, rightNode, e);

			if (connectionID == -1)
			{
				double weight = 2 - rand.nextDouble() * 4;
				Connection c = new Connection(leftNode.nodeID, rightNode.nodeID, weight, true, e.getNextConnectionID());
				e.connections.add(new Connection(leftNode.nodeID, rightNode.nodeID, weight, true, c.id));
				connections.add(c);
			}
			else
			{
				double weight = 2 - rand.nextDouble() * 4;
				if (connectionExistsLocal(leftNode, rightNode, connections) == -1)
					connections.add(new Connection(leftNode.nodeID, rightNode.nodeID, weight, true, connectionID));
			}
		}
		else
			linkMutate(nodeIDs, e, connections);
	}

	static void pointMutate(EvolutionHandler e, ArrayList<Connection> connections)
	{
		if (!connections.isEmpty())
		{
			double weight = 2 - rand.nextDouble() * 4;
			connections.get(rand.nextInt(connections.size())).weight = weight;
		}
	}

	private static long connectionExists(Node leftNode, Node rightNode, EvolutionHandler e)
	{
		for (Connection c : e.connections)
		{
			if (c.left == leftNode.nodeID && c.right == rightNode.nodeID)
				return c.id;
		}
		return -1;
	}

	private static long connectionExistsLocal(Node leftNode, Node rightNode, ArrayList<Connection> connections)
	{
		for (Connection c : connections)
		{
			if (c.left == leftNode.nodeID && c.right == rightNode.nodeID)
				return c.id;
		}
		return -1;
	}
}
