package rtlib.gui.video.video2d.jogl.demo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.MouseInputAdapter;

public class MobileContainer
{
	public MobileContainer()
	{
		final MobileContainerPanel mcp = new MobileContainerPanel();
		final JButton add = new JButton("add"), // add a new component
		sort = new JButton("sort"), // re-order components
		clear = new JButton("clear"); // remove all components
		final ActionListener l = new ActionListener()
		{
			int componentCount = 0;

			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final JButton button = (JButton) e.getSource();
				if (button == add)
				{
					final JSlider label = new JSlider();
					label.setBorder(BorderFactory.createEtchedBorder());
					mcp.addNext(label);
				}
				if (button == sort)
				{
					mcp.renewLayout();
				}
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
		final JPanel north = new JPanel();
		north.add(add);
		north.add(sort);
		north.add(clear);
		final JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(north, "North");
		f.getContentPane().add(mcp);
		f.setSize(400, 400);
		f.setLocation(200, 200);
		f.setVisible(true);
	}

	public static void main(final String[] args)
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

	public void addNext(final Component c)
	{
		componentList.add(c);
		c.addMouseListener(wrangler);
		c.addMouseMotionListener(wrangler);
		add(c);
		final Dimension d = c.getPreferredSize();
		final Point p = getNextLocation(d);
		c.setBounds(p.x, p.y, d.width, d.height);
		repaint();
	}

	private Point getNextLocation(final Dimension d)
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
		final Point p = new Point();
		if (last == null) // first component
		{
			p.x = PAD;
			p.y = PAD;
			return p;
		}
		r = last.getBounds();
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

		@Override
		public void mousePressed(final MouseEvent e)
		{
			if (e.isControlDown())
			{
				selectedComponent = (Component) e.getSource();
				offset = e.getPoint();
				dragging = true;
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e)
		{
			if (e.isControlDown())
			{
				dragging = false;
			}
		}

		@Override
		public void mouseDragged(final MouseEvent e)
		{

			if (e.isControlDown() && dragging)
			{
				final Rectangle r = selectedComponent.getBounds();
				r.x += e.getX() - offset.x;
				r.y += e.getY() - offset.y;
				selectedComponent.setBounds(r);
			}
		}
	}
}