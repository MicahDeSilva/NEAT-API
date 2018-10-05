package com.micahdesilva.syntle.NEAT;

import static org.lwjgl.opengl.GL11.*;

public class Graphics
{

	public static void drawCircle(double x, double y, double radius, int resolution)
	{
		glBegin(GL_POLYGON);
		{
			double resScale = 360 / (float) resolution;
			for (int i = 0; i < resolution; i += 1)
			{
				double j = Math.toRadians(i * resScale);
				glVertex2d(x + (Math.sin(j) * radius), y + (Math.cos(j) * radius));
			}
		}
		glEnd();
	}

	public static void drawCircle(float x, float y, double radius, int resolution, float r, float g, float b)
	{
		glColor3f(r, g, b);
		drawCircle(x, y, radius, resolution);
	}

	public static void drawLine(double x1, double y1, double x2, double y2)
	{
		glBegin(GL_LINE_STRIP);
		{
			glVertex2d(x1, y1);
			glVertex2d(x2, y2);
		}
		glEnd();
	}

	public static void drawSquare(double x1, double y1, double x2, double y2)
	{
		glBegin(GL_QUADS);
		{
			glVertex2d(x1, y1);
			glVertex2d(x1, y2);
			glVertex2d(x2, y2);
			glVertex2d(x2, y1);
		}
		glEnd();
	}
}
