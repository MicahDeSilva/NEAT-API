package com.micahdesilva.syntle.NEAT;

import java.util.ArrayList;
import java.util.Random;

import com.micahdesilva.syntle.NEAT.Node.NodeType;

public class EvolutionHandler
{
	ArrayList<Genome> genomes = new ArrayList<Genome>();
	ArrayList<Genome> genePool = new ArrayList<Genome>();

	ArrayList<Node> nodes = new ArrayList<Node>();
	ArrayList<Connection> connections = new ArrayList<Connection>();
	int currentGenome = 0, currentGeneration = 0;
	long currentNodeID = 0, currentConnectionID = 0, baseNodeCutoff;

	Random rand = new Random();

	public int inputCount, outputCount;

	public EvolutionHandler(int inputCount, int outputCount)
	{
		this.inputCount = inputCount;
		this.outputCount = outputCount;
	}

	@SuppressWarnings("unchecked")
	public void next(long fitness)
	{
		System.out.println(getCurrentGenome().inheritance + ": " + fitness);
		getCurrentGenome().fitness = fitness;
		currentGenome++;
		if (currentGenome > 26)
		{
			currentGeneration++;
			currentGenome = 0;
			genePool.clear();
			genePool = cullWeakerGenomesInSpecies((ArrayList<Genome>) genomes.clone());
			genomes.clear();
		}
		createNewGenome();
	}

	private void createNewGenome()
	{
		if (currentGeneration == 0)
		{
			ArrayList<Long> baseNodes = new ArrayList<Long>();
			for (long i = 0; i < baseNodeCutoff; i++)
			{
				baseNodes.add(i);
			}

			genomes.add(new Genome(new ArrayList<Connection>(), baseNodes, Character.toString((char) (97 + currentGenome))));
			Mutator.linkMutate(getCurrentGenome().nodeIDs, this, getCurrentGenome().connections);
			Mutator.mutate(getCurrentGenome().nodeIDs, this, getCurrentGenome().connections);
			Mutator.mutate(getCurrentGenome().nodeIDs, this, getCurrentGenome().connections);
			Mutator.mutate(getCurrentGenome().nodeIDs, this, getCurrentGenome().connections);
		}
		else
		{

			/*
			 * if (rand.nextFloat() > 0.8f) genomes.add(crossover(genePool.get(rand.nextInt(genePool.size())), genePool.get(rand.nextInt(genePool.size())))); else
			 */
			{
				Genome g = genePool.get(rand.nextInt(genePool.size()));
				@SuppressWarnings("unchecked")
				Genome gNew = new Genome((ArrayList<Connection>) g.connections.clone(), (ArrayList<Long>) g.nodeIDs.clone(), g.inheritance + Character.toString((char) (97 + currentGenome)));
				genomes.add(gNew);

				Mutator.mutate(getCurrentGenome().nodeIDs, this, getCurrentGenome().connections);
			}
		}

	}

	public void createFirstGenome()
	{
		for (int i = 0; i < inputCount; i++)
			nodes.add(new Node(NodeType.INPUT, -1, ((float) -(inputCount - 1) / 2 + i) * 0.25, currentNodeID++));

		for (int i = 0; i < outputCount; i++)
			nodes.add(new Node(NodeType.OUTPUT, 1, ((float) -(outputCount - 1) / 2 + i) * 0.25, currentNodeID++));

		ArrayList<Long> baseNodes = new ArrayList<Long>();
		baseNodeCutoff = currentNodeID;
		for (long i = 0; i < baseNodeCutoff; i++)
		{
			baseNodes.add(i);
		}

		genomes.add(new Genome(new ArrayList<Connection>(), baseNodes, "a"));
		Mutator.linkMutate(baseNodes, this, genomes.get(0).connections);
		Mutator.nodeMutate(baseNodes, this, genomes.get(0).connections);
		Mutator.linkMutate(baseNodes, this, genomes.get(0).connections);
		Mutator.nodeMutate(baseNodes, this, genomes.get(0).connections);
	}

	Genome crossover(Genome gene1, Genome gene2)
	{
		ArrayList<Long> availableNodes = new ArrayList<Long>();

		// Add all available nodes to the pool
		for (long nodeID : gene1.nodeIDs)
			availableNodes.add(nodeID);
		for (long nodeID : gene2.nodeIDs)
		{
			if (!availableNodes.contains(nodeID))
				availableNodes.add(nodeID);
		}

		// Take the connections from the dominant gene first
		ArrayList<Connection> connections = new ArrayList<Connection>();

		for (Connection c : gene1.connections)
		{
			connections.add(new Connection(c.left, c.right, c.weight, c.enabled, c.id));
		}

		// Add any connections from the recessive gene that have not been added by the dominant
		for (Connection c : gene2.connections)
		{
			if (!connections.contains(c))
				connections.add(new Connection(c.left, c.right, c.weight, c.enabled, c.id));
		}

		String inheritance = mergeInheritance(gene1.inheritance, gene2.inheritance);
		return new Genome(connections, availableNodes, gene1.inheritance + "&" + gene2.inheritance);
	}

	private String mergeInheritance(String inheritance, String inheritance2)
	{
		String ret = "";
		char[] inheritance2Arr = inheritance2.toCharArray();
		int i = 0;
		for (i = 0; i < inheritance.length(); i++)
		{
			if (!(i < inheritance2Arr.length))
				break;
			if (inheritance.toCharArray()[i] != inheritance2Arr[i])
				break;
			ret += inheritance2Arr[i];
		}

		ret += inheritance.substring(i, inheritance.length());
		ret += "&";
		ret += inheritance2.substring(i, inheritance2.length());

		return null;
	}

	public Genome getCurrentGenome()
	{
		return genomes.get(currentGenome);
	}

	public ArrayList<Node> getNodes()
	{
		return nodes;
	}

	public Node getNodeByID(Long nodeID)
	{
		for (Node n : nodes)
		{
			if (n.nodeID == nodeID)
				return n;
		}
		return null;
	}

	public long getNextConnectionID()
	{
		return currentConnectionID++;
	}

	public long getNextNodeID()
	{
		return currentNodeID++;
	}

	boolean connectionEquals(Connection c1, Connection c2)
	{
		if (c1.left == c2.left && c1.right == c2.right)
			return true;
		return false;
	}

	private ArrayList<Genome> cullWeakerGenomesInSpecies(ArrayList<Genome> species)
	{
		ArrayList<Genome> culledList = new ArrayList<Genome>();

		// Sort genomes by fitness (bubble sort)
		boolean swapPerformed = true;
		while (swapPerformed)
		{
			swapPerformed = false;
			for (int i = 0; i < species.size() - 1; i++)
			{
				if (species.get(i).fitness < species.get(i + 1).fitness)
				{
					swapPerformed = true;
					Genome temp = species.get(i);
					species.set(i, species.get(i + 1));
					species.set(i + 1, temp);
				}
			}
		}

		int desiredLength = Math.max(species.size() / 4, 1);

		culledList.add(species.get(0));
		int index = 1;

		while (culledList.size() < desiredLength)
		{
			/*
			 * // Gives a normally distributed value with 70% chance between 0 and 1, 25% chance between 1 and 2 double randValue = Math.abs(rand.nextGaussian());
			 * 
			 * // Clamp while (randValue > 2) randValue -= 1;
			 * 
			 * // Scale value for size of species randValue *= (species.size() - 1) / 2;
			 * 
			 * int index = Math.round((float) Math.floor(randValue));
			 */

			culledList.add(species.get(index++));
		}

		System.out.println("\nEnd of generation");
		for (Genome g : culledList)
			System.out.println(g.inheritance + ": " + g.fitness);
		System.out.println("");
		return culledList;
	}
}
