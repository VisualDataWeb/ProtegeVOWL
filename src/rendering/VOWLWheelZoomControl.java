package rendering;

import prefuse.Display;
import prefuse.controls.WheelZoomControl;

import java.awt.*;
import java.awt.event.MouseWheelEvent;

/*
 * This class is nearly the same as WheelZoomControl.
 * The only difference between this class and WheelZoomControl is the
 * zoom direction of mouseWheelMoved. This direction is inverse to mouseWheelMoved'sd direction
 *
 * @author David Bold
 */
public class VOWLWheelZoomControl extends WheelZoomControl {

	private Point m_point = new Point();

	/**
	 * mouseWheelMoved is similar to mouseWheelMoved from WheelZoomControl,
	 * except the zoom direction is changed. Here we use 1 - 0.1f * e.getWheelRotation()
	 * while mouseWheelMoved from WheelZoomControl uses 1 + 0.1f * e.getWheelRotation()
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Display display = (Display) e.getComponent();
		m_point.x = display.getWidth() / 2;
		m_point.y = display.getHeight() / 2;
		zoom(display, m_point, 1 - 0.1f * e.getWheelRotation(), false);
	}

}
