package com.interface21.samples.countries.web.views;

import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.SortDefinition;
import com.interface21.samples.countries.appli.ICountry;
import com.interface21.samples.countries.utils.Convert;
import com.interface21.util.RefreshablePagedListHolder;
import com.interface21.web.servlet.view.document.AbstractPdfView;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This view demonstrates how to send a Pdf file with the Spring Framework
 * using the lowagie's iText library.
 *
 * @author Jean-Pierre Pawlak
 */
public class CountriesPdfView extends AbstractPdfView {

	/**
	 * @see com.interface21.web.servlet.view.document.AbstractPdfView#buildPdfDocument(java.util.Map, com.lowagie.text.Document, com.lowagie.text.pdf.PdfWriter, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	//~ Static fields/initializers ---------------------------------------------

	private static final Font    HEADLINE_FONT = new Font( Font.HELVETICA, 18, Font.BOLD, Color.blue );
	private static final Font    HEADING_FONT = new Font( Font.HELVETICA, 12, Font.ITALIC, Color.black );
	private static final Font    HEADING_DATA_FONT = new Font( Font.HELVETICA, 12, Font.ITALIC, Color.blue );
	private static final Font    DATA_HEAD_FONT = new Font( Font.HELVETICA, 10, Font.ITALIC, Color.black );
	private static final Font    TEXT_FONT = new Font( Font.TIMES_NEW_ROMAN, 8, Font.NORMAL, Color.black );
	private static final Font    BOLD_FONT = new Font( Font.TIMES_NEW_ROMAN, 8, Font.BOLD, Color.black );
	private static final int     MARGIN = 32;

	protected void buildPdfDocument(
		Map model,
		Document pdfDoc,
		PdfWriter writer,
		HttpServletRequest request,
		HttpServletResponse response)
		throws DocumentException {

		Paragraph              par;

		// We search the data to insert
		RefreshablePagedListHolder pgHolder = ( RefreshablePagedListHolder ) model.get( "countries" );
		Locale loc = pgHolder.getLocale();
		Convert conv = new Convert(loc, loc);

		// We prepare some data
		SortDefinition  sort = pgHolder.getSort();
		BeanWrapper bw = new BeanWrapperImpl( pgHolder.getFilter() );
		String filterName = (String)bw.getPropertyValue("name");
		String filterCode = (String)bw.getPropertyValue("code");

		// We create and add the event handler
		// So we can well paging, ensuring that only entire cells are printed
		// at end of pages (the latter is useless in this example as records
		// keep in one row, but in your own developpment it's not always the case).
		MyPageEvents events = new MyPageEvents(loc);
		writer.setPageEvent( events );
		events.onOpenDocument( writer, pdfDoc );
		
		String title = getMessage("app.name", loc);
		pdfDoc.add( new Paragraph(title, HEADLINE_FONT));
		pdfDoc.add( new Paragraph(" "));
		pdfDoc.add( new Paragraph(" "));
		pdfDoc.add( new Paragraph(" "));
	
		// We create a table for used criteria and extracting information
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(50);
		table.getDefaultCell().setBorderWidth(1);
		table.getDefaultCell().setBorderColor(Color.black);
		table.getDefaultCell().setPadding(4);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		table.getDefaultCell().setVerticalAlignment( Element.ALIGN_MIDDLE);

		PdfPCell cell = new PdfPCell(new Phrase(getMessage("criteria", loc), HEADING_FONT));
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill(0.7f);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(getMessage( "property",loc), HEADING_FONT));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill(0.9f);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(getMessage( "value", loc), HEADING_FONT));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill(0.9f);
		table.addCell(cell);

		// We put the used criteria and extracting information	
		cell = new PdfPCell(new Phrase(getMessage( "date.extraction", loc), HEADING_FONT));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(conv.fromDate(pgHolder.getRefreshDate()), HEADING_DATA_FONT));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(getMessage( "nbRecords", loc), HEADING_FONT));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(String.valueOf(pgHolder.getNrOfElements()), HEADING_DATA_FONT));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(getMessage( "sort.name", loc), HEADING_FONT));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(sort.getProperty(), HEADING_DATA_FONT));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(getMessage( "sort.asc", loc), HEADING_FONT));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(getMessage(Boolean.toString(sort.isAscending()), loc), HEADING_DATA_FONT));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(getMessage( "sort.igncase", loc), HEADING_FONT));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(getMessage(Boolean.toString(sort.isIgnoreCase()), loc), HEADING_DATA_FONT));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(getMessage( "filter.name", loc), HEADING_FONT));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(null == filterName ? "" : filterName, HEADING_DATA_FONT));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(getMessage( "filter.code", loc), HEADING_FONT));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(null == filterCode ? "" : filterCode, HEADING_DATA_FONT));
		table.addCell(cell);

		pdfDoc.add(table);
		pdfDoc.newPage();

		// We can go now on the countries list
		table = new PdfPTable(2);
		int headerwidths[] = {20, 80};
		table.setWidths(headerwidths);
		table.setWidthPercentage(60);
		table.getDefaultCell().setBorderWidth(2);
		table.getDefaultCell().setBorderColor(Color.black);
		table.getDefaultCell().setGrayFill(0.75f);
		table.getDefaultCell().setPadding(3);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

		table.addCell(new Phrase(getMessage( "code", loc), DATA_HEAD_FONT));
		table.addCell(new Phrase(getMessage( "name", loc), DATA_HEAD_FONT));

		// We set the above row as remaining title
		// and adjust properties for normal cells
		table.setHeaderRows(1);
		table.getDefaultCell().setBorderWidth(1);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
	
		// We iterate now on the countries list	
		boolean even = false;
		Iterator it = pgHolder.getSource().iterator();  
		while(it.hasNext()) {
			if (even) {
				table.getDefaultCell().setGrayFill(0.95f);
				even = false;
			} else {
				table.getDefaultCell().setGrayFill(1.00f);
				even = true;
			}
			ICountry country = (ICountry)it.next();
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(new Phrase(country.getCode(), BOLD_FONT));
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(new Phrase(country.getName(), TEXT_FONT));
		}
		pdfDoc.add(table);
	}

	private String getMessage(String key, Locale locale) {
		return this.getApplicationContext().getMessage(key, null, key, locale);
	}

	//~ Inner Classes ----------------------------------------------------------

	class MyPageEvents extends PdfPageEventHelper {
		//~ Instance fields ----------------------------------------------------

		// This is the contentbyte object of the writer
		PdfContentByte cb;

		// we will put the final number of pages in a template
		PdfTemplate template;

		// this is the BaseFont we are going to use for the header / footer
		BaseFont bf = null;
		
		// this the locale to use for retrieved messages
		private Locale loc;

			//~ Constructors -------------------------------------------------------
		public MyPageEvents(Locale loc) {
			this.loc = loc;
		}

		//~ Methods ------------------------------------------------------------

		// we override the onOpenDocument method
		public void onOpenDocument( PdfWriter writer, Document  document ) {
			try	{
				bf = BaseFont.createFont( BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED );
				cb = writer.getDirectContent();
				template = cb.createTemplate(50, 50);
			} catch (DocumentException de) {
			} catch (IOException ioe) {}
		}

		// we override the onEndPage method
		public void onEndPage(PdfWriter writer, Document document) {
			int pageN = writer.getPageNumber();
			String text = getMessage( "page", loc) + " " + pageN + " " + getMessage( "on", loc) + " ";
			float  len = bf.getWidthPoint( text, 8 );
			cb.beginText();
			cb.setFontAndSize(bf, 8);

			cb.setTextMatrix(MARGIN, 16);
			cb.showText(text);
			cb.endText();

			cb.addTemplate(template, MARGIN + len, 16);
			cb.beginText();
			cb.setFontAndSize(bf, 8);

			cb.endText();
		}

		// we override the onCloseDocument method
		public void onCloseDocument(PdfWriter writer, Document document) {
			template.beginText();
			template.setFontAndSize(bf, 8);
			template.showText(String.valueOf( writer.getPageNumber() - 1 ));
			template.endText();
		}
	}
}
