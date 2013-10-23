package de.markschaefer.form;

import static de.markschaefer.form.Util.*;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.configuration.*;
import org.apache.commons.lang.*;
import org.slf4j.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class Main
{
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static BaseFont baseFont;

	public static void main(String[] args) throws DocumentException, IOException, ConfigurationException
	{
		LOG.info("*** Starting Form Filler ***");

		if (args.length < 1)
		{
			LOG.warn("No input given");
			return;
		}

		Configuration props = read(args[0]);
		ConfigurationUtils.dump(props, System.out);

		Document document = new Document();
		FileOutputStream os = new FileOutputStream("d:/out.pdf");
		PdfWriter writer = PdfWriter.getInstance(document, os);
		document.open();

		PdfContentByte content = writer.getDirectContent();

		baseFont = BaseFont.createFont("src/test/resources/Gudea-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		content.setFontAndSize(baseFont, 17);

		createColumn(content, getAbsenderArea(), getAbsender(props));
		createColumn(content, getAddresArea(), getAdresse(props));
		createColumn(content, getDateArea(), getDate(props));
		createColumn(content, getSubjectArea(), getSubject(props));
		createColumn(content, getBodyArea(), getBody(props));

		document.close();
		os.close();

	}

	private static PdfPTable getBody(Configuration props)
	{
		Font font = new Font(baseFont, 11);
		Font bold = new Font(baseFont, 11, Font.BOLD);

		PdfPTable table = new PdfPTable(new float[] { 2, 6, 2, 2, 2 });
		table.setWidthPercentage(90);

		addCells(table, bold, cL("Posten"), cL("Bezeichnung"), cL("Anzahl"), cL("Einheit"), cL("Preis"));

		for (int i = 0; i < 10; ++i)
		{
			addCells(table, font, cR("" + i), cL("ds"), cR("ds"), cR("ds"), cR("ds"));
		}

		return table;
	}

	static Content c(String text, int alignment)
	{
		return new Content(text, alignment);
	}

	static Content cL(String text)
	{
		return new Content(text, Element.ALIGN_LEFT);
	}

	static Content cR(String text)
	{
		return new Content(text, Element.ALIGN_RIGHT);
	}

	static class Content
	{
		final String text;
		final int alignment;

		public Content(String text, int alignment)
		{
			this.text = text;
			this.alignment = alignment;
		}
	}

	private static void addCells(PdfPTable table, Font font, Content... content)
	{
		for (Content c : content)
		{
			Paragraph par = new Paragraph(c.text);
			par.setFont(font);
			par.setAlignment(c.alignment);
			par.setIndentationLeft(10);
			par.setIndentationRight(10);
			PdfPCell cell = new PdfPCell();
			cell.addElement(par);
			cell.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cell);
		}
	}

	private static Paragraph getAbsender(Configuration props)
	{
		Paragraph absender = new Paragraph(props.getString("absender"));
		absender.setFont(new Font(baseFont, 7));
		return absender;
	}

	private static Paragraph getAdresse(Configuration props)
	{
		Paragraph adresse = new Paragraph(StringUtils.join(props.getStringArray("adresse"), "\n"));
		adresse.setFont(new Font(baseFont, 12, Font.BOLD));
		return adresse;
	}

	private static Paragraph getDate(Configuration props)
	{
		Paragraph adresse = new Paragraph(props.getString("date"));
		adresse.setFont(new Font(baseFont, 10, Font.BOLD));
		adresse.setAlignment(Paragraph.ALIGN_RIGHT);
		return adresse;
	}

	private static Paragraph getSubject(Configuration props)
	{
		Paragraph adresse = new Paragraph(props.getString("betreff"));
		adresse.setFont(new Font(baseFont, 11, Font.BOLD));
		return adresse;
	}

	private static Configuration read(String file) throws FileNotFoundException, IOException, ConfigurationException
	{
		PropertiesConfiguration result = new PropertiesConfiguration();
		result.setEncoding("UTF-8");
		result.load(file);
		result.addProperty("currentDate", new SimpleDateFormat("dd.MM.YYYY").format(new Date()));
		return result.interpolatedConfiguration();
	}
}
