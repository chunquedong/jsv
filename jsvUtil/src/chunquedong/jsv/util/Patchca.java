//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class Patchca {
	// image size
	public int width = 100;
	public int height = 25;

	// char num
	public int n = 4;

	public int margin = 5;
	public int charPad = 15;
	public int noise = 3;

	public int fontSize = 24;
	
	public Font font = null;
	
	public String charSet = "abcdefghijkmnopqrstuvwxyz0123456789";

	/**
	 ** output the image to stream, and return the generate code
	 **/
	public String create(OutputStream out) {
		String code = randomCode();
		outputImage(out, code);
		return code;
	}

	public void outputImage(OutputStream out, String code) {
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();

		g.setBackground(Color.white);
		g.clearRect(0, 0, width, height);

		setRandomColor(g);
		for (int i = 0; i < code.length(); ++i) {
			char c = code.charAt(i);
			rotate(g, center(i), middle());
			drawCode(c, g, i);
			drawPointNoise(g);
		}
		resetTransform(g);
		drawNoise(g);

		try {
			ImageIO.write(img, "JPG", (out));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void drawCode(char c, Graphics2D g, int i) {
		if (font != null) {
			Font f = font.deriveFont(Font.BOLD | (Font.ITALIC), fontSize);
			g.setFont(f);
		} else {
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD | (Font.ITALIC), fontSize));
		}
		g.drawString(new String(new char[] { c }), left(i), base());
	}

	// get random code
	public String randomCode() {
		String list = charSet;
		char[] code = new char[n];
		for (int i = 0; i < n; ++i) {
			int random = (int) (Math.random() * list.length());
			if (random >= list.length()) {
				random = list.length()-1;
			}
			code[i] = (list.charAt(random));
		}
		return new String(code);
	}

	// ////////////////////////////////////////////////////////////////////////
	// util
	// ////////////////////////////////////////////////////////////////////////

	private void setRandomColor(Graphics2D g) {
		int r = (int) Math.random() * 200;
		int g_ = (int) Math.random() * 200;
		int b = (int) Math.random() * 200;
		g.setColor(new Color(r, g_, b));
	}

	private void rotate(Graphics2D g, Float anchorx, Float anchory) {
		double theta = Math.random() - 0.6f;
		AffineTransform transform = new AffineTransform();
		transform.rotate(theta, anchorx, anchory);

		g.setTransform(transform);
	}

	private void resetTransform(Graphics2D g) {
		AffineTransform transform = new AffineTransform();
		g.setTransform(transform);
	}

	// ////////////////////////////////////////////////////////////////////////
	// letter position
	// ////////////////////////////////////////////////////////////////////////

	// ** one char size
	private float charWidth() {
		float contentWidth = width - (margin + margin);
		float fontWidth = contentWidth - (n * charPad);
		return (fontWidth / n);
	}

	@SuppressWarnings("unused")
	private float top() {
		return margin;
	}

	private float base() {
		return height - margin;
	}

	private float left(int i) {
		return margin + ((charWidth() + charPad) * i);
	}

	private float middle() {
		return height / 2f;
	}

	private float center(int i) {
		return left(i) + charWidth() / 2f;
	}

	// ////////////////////////////////////////////////////////////////////////
	// noise
	// ////////////////////////////////////////////////////////////////////////

	private void drawNoise(Graphics2D g) {
		drawLongLine(g);
	}

	private void drawPointNoise(Graphics2D g) {
		for (int i = 0; i < noise; ++i) {
			drawPoint(g);
		}
	}

	private int genRandom(int min, int max) {
		return ((int) (Math.random() * (max - min))) + min;
	}

	private void drawLongLine(Graphics2D g) {
		int x1 = genRandom(margin, margin + 10);
		int y1 = genRandom(margin, height - margin);
		int x2 = genRandom((width - (margin + 10)), (width - margin));
		int y2 = genRandom(margin, height - margin);

		// setRandomColor(g)
		g.setStroke(new BasicStroke(2f));
		g.drawLine(x1, y1, x2, y2);
	}

	private void drawPoint(Graphics2D g) {
		int x1 = genRandom(0, width);
		int y1 = genRandom(0, height);
		int w = genRandom(1, 3);
		int h = genRandom(1, 3);

		g.fillOval(x1, y1, w, h);
	}
}
