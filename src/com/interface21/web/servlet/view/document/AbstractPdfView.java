package com.interface21.web.servlet.view.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.view.AbstractView;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
 
/**
 * Abstract superclass for PDF views, using Bruno Lowagie's
 * iText package. Application-specific view classes will extend this class.
 * The view will be held in such a subclass, not a template such as a JSP.
 * <br>See <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a> 
 * by Rod Johnson, pp 571-575 for an example of use of this class.
 * <br>NB: Internet Explorer requires a .pdf extension, as
 * it doesn't always respect the declared content type.
 * <br>Exposes page width and height as bean properties.
 * @version $Id$
 * @author Rod Johnson
 */
public abstract class AbstractPdfView extends AbstractView {
	
	private int width = 600;
	
	private int	height = 750;

	/**
	 * Set the appropriate content type.
	 * Note that IE won't take much notice of this,
	 * but there's not a lot we can do about this.
	 * Generated documents should have a .pdf extension.
 	*/
	public AbstractPdfView() {
		setContentType("application/pdf");
	}
	
	/**
	 * TODO: bean property not currently used
	 */
	public void setWidth(int width) {
		this.width = width;
	}
		
	/**
	 * TODO: bean property not currently used
	 */
	public void setHeight(int height) {
		this.height = height;
	}


	/**
	 * @see com.interface21.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected final void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		//Rectangle pageSize = new Rectangle(this.width, this.height);
		//pageSize.setBackgroundColor(new java.awt.Color(0xFF, 0xFF, 0xEE));

		// TODO: could allow setting through bean properties
		Document document = new Document(PageSize.A4);

		try {
			// The following simple method doesn't work in IE, which
			// needs to know the content length.
			// PdfWriter.getInstance(document, response.getOutputStream());
			//document.open();
			//doPdfDocument(model, document);
			//document.close();
			
			// See	http://www.lowagie.com/iText/faq.html#msie
			// for an explanation of why we can't use the obvious form above.
			
			// IE workaround
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			
			// **TODO: could expose these preferences as bean properties also
			writer.setViewerPreferences(PdfWriter.AllowPrinting | PdfWriter.PageLayoutSinglePage);
			document.open();
			buildPdfDocument(model, document, writer, request, response);
			document.close();

			response.setContentLength(baos.size());
			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		}
		catch (DocumentException ex) {
			throw new ServletException("Error creating PDF document", ex);
		}
	}	// renderMergedOutputModel


	/**
	 * Subclasses must implement this method to create an iText PDF document,
	 * given the model.
	 * @param request in case we need locale etc. Shouldn't look at attributes
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 */
	protected abstract void buildPdfDocument(Map model, Document pdfDoc, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws DocumentException;

}