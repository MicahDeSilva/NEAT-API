package com.micahdesilva.syntle.NEAT;

import java.util.ArrayList;

import com.micahdesilva.syntle.NEAT.Node.NodeType;

import static org.lwjgl.opengl.GL11.*;

public class Net
{
	EvolutionHandler e;

	public Net(int inputCount, int outputCount)
	{
		e = new EvolutionHandler(inputCount, outputCount);
		e.createFirstGenome();
	}

	public void draw(int x, int y, float scale)
	{
		ArrayList<Node> nodesToDraw = new ArrayList<Node>();

		for (Node n : e.getNodes())
		{
			if (e.getCurrentGenome().nodeIDs.contains(n.nodeID))
				nodesToDraw.add(n);
		}

		drawNet(nodesToDraw, e.getCurrentGenome().connections, x, y, scale);
	}

	public boolean[] feedForward(float[] inputFloats)
	{
		// TODO big lag
		ArrayList<Node> outputs = new ArrayList<Node>();

		for (Node n : e.getNodes())
		{
			if (n.nodeType == NodeType.OUTPUT)
				outputs.add(n);
		}

		boolean[] outBools = new boolean[outputs.size()];

		int i = 0;

		for (Node output : outputs)
		{
			outBools[i++] = getNodeValue(output, e.getCurrentGenome().connections, inputFloats) > 0.5f;
		}

		return outBools;
	}

	float getNodeValue(Node node, ArrayList<Connection> connections, float[] inputFloats)
	{
		if (node.nodeType == NodeType.INPUT)
			return inputFloats[(int) node.nodeID];
		float ret = 0;
		for (Connection c : connections)
		{
			if (c.enabled)
			{
				if (c.right == node.nodeID)
					ret += getNodeValue(e.getNodeByID(c.left), connections, inputFloats) * c.weight;
			}
		}

		return sigmoid(ret);
	}

	private float sigmoid(float x)
	{
		// TODO Auto-generated method stub
		return (float) (1 / (1 + Math.exp(-x)));
	}

	public void drawNet(ArrayList<Node> netNodes, ArrayList<Connection> netConnections, double centreX, double centreY, double scale)
	{
		for (Node n : netNodes)
		{
			Graphics.drawCircle(centreX + (n.x * scale), centreY + (n.y * scale), scale / 50, 360);
		}

		for (Connection c : netConnections)
		{
			glColor3f(1.0f, 1.0f, 1.0f);
			if (!c.enabled)
				glColor3f(0.5f, 0.5f, 0.5f);
			Node left = null, right = null;

			for (Node n : netNodes)
			{
				if (n.nodeID == c.left)
					left = n;
				if (n.nodeID == c.right)
					right = n;
			}

			if (left != null && right != null)
				Graphics.drawLine(centreX + (left.x * scale), centreY + (left.y * scale), centreX + (right.x * scale), centreY + (right.y * scale));
		}
	}

	public void next(long fitness)
	{
		e.next(fitness);
	}

}
