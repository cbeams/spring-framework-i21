package com.interface21.web.servlet.view.document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.servlet.support.RequestContextUtils;
import com.interface21.web.servlet.view.AbstractView;

/**
 * Convenient superclass for Excel-document views.
 * Properties:
 * <li>url (optional): The url of an existing Excel document to pick as a starting point.
 * It is done without localization part nor the .xls extension.
 * <br>The file will be searched with names in the following order:
 * <li>[url]_[language][country].xls
 * <li>[url]_[language].xls
 * <li>[url].xls
 * <br>For work with th workBook in the subclass, see <a href="http://jakarta.apache.org/poi/index.html">Jakarta's POI site</a>
 * <br>As an example, you can try this snippet:<br>
 * <code>
 * <br>protected void buildExcelDocument(
 * <br>    Map model,
 * <br>    HSSFWorkbook wb,
 * <br>    HttpServletRequest request,
 * <br>    HttpServletResponse response )
 * <br>{
 * <br>    // AModel aModel = ( AModel ) model.get( "amodel" );
 * <br>
 * <br>    HSSFSheet sheet;
 * <br>    HSSFRow   sheetRow;
 * <br>    HSSFCell  cell;
 * <br>
 * <br>    // Go to the first sheet
 * <br>    // getSheetAt: only if wb is created from an existing document
 * <br>	   //sheet = wb.getSheetAt( 0 );
 * <br>	   sheet = wb.createSheet("Spring");
 * <br>	   sheet.setDefaultColumnWidth((short)12);
 * <br>
 * <br>    // write a text at A1
 * <br>    cell = getCell( sheet, 0, 0 );
 * <br>    setText(cell,"Spring POI test");
 * <br>
 * <br>    // Write the current date at A2
 * <br>    HSSFCellStyle dateStyle = wb.createCellStyle(  );
 * <br>    dateStyle.setDataFormat( HSSFDataFormat.getBuiltinFormat( "m/d/yy" ) );
 * <br>    cell = getCell( sheet, 1, 0 );
 * <br>    cell.setCellValue( new Date() );
 * <br>    cell.setCellStyle( dateStyle );
 * <br>
 * <br>    // Write a number at A3
 * <br>    getCell( sheet, 2, 0 ).setCellValue( 458 );
 * <br>
 * <br>    // Write a range of numbers
 * <br>    sheetRow = sheet.createRow( 3 );
 * <br>    for (short i = 0; i<10; i++) {
 * <br>        sheetRow.createCell(i).setCellValue( i*10 );
 * <br>    }
 * <br>}
 * <br>
 * </code>
 * <br>Don't forget to add on web.xml:
 * <code>
 * <br>&lt;servlet-mapping&gt;
 * <br>    &lt;servlet-name&gt;[your Spring servlet]&lt;/servlet-name&gt;
 * <br>    &lt;url-pattern&gt;*.xls&lt;/url-pattern&gt;
 * <br>&lt;/servlet-mapping&gt;
 * </code>
 * <br>The use of this view is close to the AbstractPdfView
 * @see AbstractPdfView
 * @author <a href="mailto:jp.pawlak@tiscali.fr">Jean-Pierre Pawlak</a>
 */
public abstract class AbstractExcelView extends AbstractView {

	private static final String EXTENSION = ".xls";

	private static final String SEPARATOR = "_";

	//~ Instance fields --------------------------------------------------------

	private String url;

	private HSSFWorkbook wb;

	//~ Constructors -----------------------------------------------------------

	public AbstractExcelView() {
		setContentType("application/vnd.ms-excel");
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Renders the view given the specified model.
	 * @see com.interface21.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected final void renderMergedOutputModel(Map model,
																							 HttpServletRequest request,
																							 HttpServletResponse response)
			throws ServletException, IOException {
		if (null != url) {
			wb = getTemplateSource(url, request);
		}
		else {
			wb = new HSSFWorkbook();
			logger.info("Excel WorkBook created from scratch");
		}

		buildExcelDocument(model, wb, request, response);

		// response.setContentLength(wb.getBytes().length);
		response.setContentType(getContentType());
		ServletOutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
	}

	/**
	 * Creates the workBook from an existing .xls document.
	 * @param url url of the Excle template without localization part nor extension
	 * @param request
	 * @return HSSFWorkbook
	 */
	protected HSSFWorkbook getTemplateSource(String url, HttpServletRequest request) throws ServletException {
		String source = null;
		String realPath = null;
		FileInputStream inputFile = null;
		Locale userLocale = RequestContextUtils.getLocale(request);
		String lang = userLocale.getLanguage();
		String country = userLocale.getCountry();

		// Check for document with language and country localisation
		if (country.length() > 1) {
			source = url + SEPARATOR + lang + country + EXTENSION;
			realPath = ((WebApplicationContext) getApplicationContext()).getServletContext().getRealPath(source);
			try {
				inputFile = new FileInputStream(realPath);
			}
			catch (FileNotFoundException e) {
				// Nothing: at this stage, it is acceptable
			}
		}
		// Check for document with language localisation
		if (lang.length() > 1 && null == inputFile) {
			source = url + SEPARATOR + lang + EXTENSION;
			realPath = ((WebApplicationContext) getApplicationContext()).getServletContext().getRealPath(source);
			try {
				inputFile = new FileInputStream(realPath);
			}
			catch (FileNotFoundException e) {
				// Nothing: at this stage, it is acceptable
			}
		}
		// Check for document without localisation
		if (null == inputFile) {
			source = url + EXTENSION;
			realPath = ((WebApplicationContext) getApplicationContext()).getServletContext().getRealPath(source);
			try {
				inputFile = new FileInputStream(realPath);
			}
			catch (FileNotFoundException e) {
				throw new ApplicationContextException(
						"Can't resolve real path for EXCEL template at '"
						+ source
						+ "'; probably results from container restriction: override ExcelView.getTemplateSource() to use an alternative approach to getRealPath()");
			}
		}
		// Create the Excel document from source
		try {
			POIFSFileSystem fs = new POIFSFileSystem(inputFile);
			HSSFWorkbook workBook = new HSSFWorkbook(fs);
			logger.info("Loaded Excel workBook " + source);
			return workBook;
		}
		catch (IOException e) {
			throw new ApplicationContextException("IOException with '" + source + "': " + e.getMessage());
		}
	}

	/**
	 * Subclasses must implement this method to create an Excel HSSFWorkbook document,
	 * given the model.
	 * @param model
	 * @param wb The Excel workBook to complete
	 * @param request in case we need locale etc. Shouldn't look at attributes
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 */
	protected abstract void buildExcelDocument(Map model,
																						 HSSFWorkbook wb,
																						 HttpServletRequest request,
																						 HttpServletResponse response);

	/**
	 * Convenient method to obtain the cell in the given sheet, row and column
	 * <br>Creating by the way the row and the cell if they still doesn't exist
	 * <br>Thus, the column can be passed as an int, the method making the needed
	 * downcasts.
	 * @param sheet A sheet Object. The first sheet is usually obtained by wb.getSheetAt(0)
	 * @param row
	 * @param col
	 * @return HSSFCell
	 */
	protected HSSFCell getCell(HSSFSheet sheet, int row, int col) {
		HSSFRow sheetRow = sheet.getRow(row);
		if (null == sheetRow) {
			sheetRow = sheet.createRow(row);
		}
		HSSFCell cell = sheetRow.getCell((short) col);
		if (null == cell) {
			cell = sheetRow.createCell((short) col);
		}
		return cell;
	}

	/**
	 * Convenient method to set a String as text content in a cell.
	 * @param cell The cell in which the text must be put
	 * @param text The text to put in the cell
	 */
	protected void setText(HSSFCell cell, String text) {
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(text);
	}

	/**
	 * Sets the url.
	 * @param url The Excel workBook source without localization part nor extension
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
