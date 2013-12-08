package de.markschaefer.form;

import static de.markschaefer.form.Util.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

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

		LOG.info("*** Starting Form Filler ***");
		Configuration props = read(args[0]);
		ConfigurationUtils.dump(props, System.out);

		Document document = new Document();
		FileOutputStream os = new FileOutputStream("/home/mark/out.pdf");
		PdfWriter writer = PdfWriter.getInstance(document, os);
		document.open();

		PdfContentByte content = writer.getDirectContent();

		baseFont = BaseFont.createFont("src/test/resources/Gudea-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		content.setFontAndSize(baseFont, 17);

		InputStream is = new FileInputStream(props.getString("template"));
		// Here's the key part. Let's turn the template in to
		// usable PDF object
		PdfReader reader = new PdfReader(is);
		PdfImportedPage page = writer.getImportedPage(reader, 1);

		// Now, add it to the blank PDF document we've opened
		PdfContentByte cb = writer.getDirectContent();
		cb.addTemplate(page, 0, 0);
		is.close();

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
