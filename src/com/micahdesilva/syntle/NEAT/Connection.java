package com.micahdesilva.syntle.NEAT;

public class Connection
{
	final long left, right;
	double weight;

	boolean enabled = true;
	final long id;

	Connection(long left, long right, double weight, boolean enabled, long id)
	{
		this.left = left;
		this.right = right;
		this.weight = weight;
		this.enabled = enabled;
		this.id = id;
	}

	public void toggleEnabled()
	{
		enabled = !enabled;
	}

	public boolean disable()
	{
		if (enabled)
		{
			enabled = false;
			return true;
		}
		return false;
	}
}
