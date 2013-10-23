package de.markschaefer.form;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public final class Util
{

	private static final int A4_HEIGHT = 297;
	private static final int LEFT = 25;
	private static final int RIGHT = 190;

	private static final int ABSENDER_Y = 27;
	private static final int ABSENDER_HEIGHT = 5;
	private static final int ABSENDER_WIDTH = 80;

	private static final int ADDRESS_Y = 32;
	private static final int ADDRESS_HEIGHT = 40;
	private static final int ADDRESS_WIDTH = 80;

	private static final int DATE_Y = 80;
	private static final int DATE_HEIGHT = 10;

	private static final int BODY_Y = 100;
	private static final int BODY_HEIGHT = 170;
	private static final int BODY_WIDTH = 165;

	private static final int SUBJECT_HEIGHT = 15;
	private static final int SUBJECT_Y = BODY_Y - SUBJECT_HEIGHT;
	private static final int SUBJECT_WIDTH = BODY_WIDTH;

	private Util()
	{
	};

	// 10 mm per cm, 2.54 cm per inch, 72 units per inch
	private static float MM_TO_UNIT = 1f / 10f / 2.54f * 72f;

	static float mmToUnit(float mm)
	{
		return mm * MM_TO_UNIT;
	}

	static Rectangle toPdfUnits(Rectangle mm)
	{
		return new Rectangle(mmToUnit(mm.getLeft()), mmToUnit(A4_HEIGHT) - mmToUnit(mm.getBottom()),
			mmToUnit(mm.getLeft()) + mmToUnit(mm.getRight()), mmToUnit(A4_HEIGHT) - mmToUnit(mm.getBottom())
				- mmToUnit(mm.getTop()));
	}

	static void createColumn(PdfContentByte canvas, Rectangle position, Element content) throws DocumentException
	{
		ColumnText columnText = new ColumnText(canvas);
		columnText.setSimpleColumn(toPdfUnits(position));
		columnText.addElement(content);
		columnText.go();
	}

	static Rectangle getAddresArea()
	{
		return new Rectangle(LEFT, ADDRESS_Y, ADDRESS_WIDTH, ADDRESS_HEIGHT);
	}

	static Rectangle getAbsenderArea()
	{
		return new Rectangle(LEFT, ABSENDER_Y, ABSENDER_WIDTH, ABSENDER_HEIGHT);
	}

	static Rectangle getDateArea()
	{
		return new Rectangle(LEFT, DATE_Y, BODY_WIDTH, DATE_HEIGHT);
	}

	static Rectangle getBodyArea()
	{
		return new Rectangle(LEFT, BODY_Y, BODY_WIDTH, BODY_HEIGHT);
	}

	static Rectangle getSubjectArea()
	{
		return new Rectangle(LEFT, SUBJECT_Y, SUBJECT_WIDTH, SUBJECT_HEIGHT);
	}
}
