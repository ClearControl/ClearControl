package gui.video.video2d.jogl.test;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;

public class MobileContainer
{
	public MobileContainer()
	{
		final MobileContainerPanel mcp = new MobileContainerPanel();
		final JButton add = new JButton("add"), // add a new component
		sort = new JButton("sort"), // re-order components
		clear = new JButton("clear"); // remove all components
		ActionListener l = new ActionListener()
		{
			int componentCount = 0;

			public void actionPerformed(ActionEvent e)
			{
				JButton button = (JButton) e.getSource();
				if (button == add)
				{
					JSlider label = new JSlider();
					label.setBorder(BorderFactory.createEtchedBorder());
					mcp.addNext(label);
				}
				if (button == sort)
					mcp.renewLayout();
				if (button == clear)
				{
					mcp.clear();
					componentCount = 0;
				}
			}
		};
		add.addActionListener(l);
		sort.addActionListener(l);
		clear.addActionListener(l);
		JPanel north = new JPanel();
		north.add(add);
		north.add(sort);
		north.add(clear);
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(north, "North");
		f.getContentPane().add(mcp);
		f.setSize(400, 400);
		f.setLocation(200, 200);
		f.setVisible(true);
	}

	public static void main(String[] args)
	{
		new MobileContainer();
	}
}

/**
 * container with null layout to which you can add components and move them
 * around with the mouse. components can be re-ordered in a new layout with
 * renewLayout method.
 */
class MobileContainerPanel extends JPanel
{
	List componentList;
	ComponentWrangler wrangler;
	final int PAD = 10;

	public MobileContainerPanel()
	{
		componentList = new ArrayList();
		wrangler = new ComponentWrangler();
		setLayout(null);
	}

	public void addNext(Component c)
	{
		componentList.add(c);
		c.addMouseListener(wrangler);
		c.addMouseMotionListener(wrangler);
		add(c);
		Dimension d = c.getPreferredSize();
		Point p = getNextLocation(d);
		c.setBounds(p.x, p.y, d.width, d.height);
		repaint();
	}

	private Point getNextLocation(Dimension d)
	{
		int maxX = 0, maxY = 0;
		Component c, last = null;
		Rectangle r;
		// find level of lowest component(s)
		for (int j = 0; j < componentList.size(); j++)
		{
			c = (Component) componentList.get(j);
			r = c.getBounds();
			if (r.y + r.height > maxY)
			{
				maxY = r.y + r.height;
				last = c;
			}
		}
		// find last (in row) of lowest components
		for (int j = 0; j < componentList.size(); j++)
		{
			c = (Component) componentList.get(j);
			r = c.getBounds();
			if (r.y + r.height == maxY && r.x + r.width > maxX)
			{
				maxX = r.x + r.width;
				last = c;
			}
		}
		// determine location of next component based on location of last
		Point p = new Point();
		if (last == null) // first component
		{
			p.x = PAD;
			p.y = PAD;
			return p;
		}
		r = last.getBounds();
		int x, y;
		if (r.x + r.width + PAD + d.width < getWidth()) // next in row
		{
			p.x = r.x + r.width + PAD;
			p.y = r.y;
		}
		else
		// skip to new row
		{
			p.x = PAD;
			p.y = r.y + r.height + PAD;
		}
		return p;
	}

	public void renewLayout()
	{
		removeAll();
		Component c;
		Dimension d;
		// set location of all components to offscreen positions
		for (int j = 0; j < componentList.size(); j++)
		{
			c = (Component) componentList.get(j);
			d = c.getSize();
			c.setBounds(-d.width, -d.height, d.width, d.height);
		}
		Point p;
		// add components and reset their location
		for (int j = 0; j < componentList.size(); j++)
		{
			c = (Component) componentList.get(j);
			add(c);
			d = c.getSize();
			p = getNextLocation(d);
			c.setBounds(p.x, p.y, d.width, d.height);
		}
		repaint();
	}

	public void clear()
	{
		removeAll();
		componentList.clear();
		repaint();
	}

	/**
	 * select and drag components with the mouse
	 */
	private class ComponentWrangler extends MouseInputAdapter
	{
		Component selectedComponent;
		Point offset;
		boolean dragging;

		public ComponentWrangler()
		{
			dragging = false;
		}

		public void mousePressed(MouseEvent e)
		{
			if (e.isControlDown())
			{
				selectedComponent = (Component) e.getSource();
				offset = e.getPoint();
				dragging = true;
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isControlDown())
			{
				dragging = false;
			}
		}

		public void mouseDragged(MouseEvent e)
		{

			if (e.isControlDown() && dragging)
			{
				Rectangle r = selectedComponent.getBounds();
				r.x += e.getX() - offset.x;
				r.y += e.getY() - offset.y;
				selectedComponent.setBounds(r);
			}
		}
	}
}