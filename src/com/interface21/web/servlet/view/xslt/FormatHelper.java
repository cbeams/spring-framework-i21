/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
package com.interface21.web.servlet.view.xslt;


import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.interface21.core.NestedRuntimeException;


/**
 * Xalan extension functions to provide date and currency formatting
 * beyond the capabilities of XSLT 1.0 or 1.1.
 * <br>Note that all extension functions are static.
 * <br>These extension functions must be declared to use this class.
 * <br>Based on an example by Taylor Cowan.
 * @author Rod Johnson
 */
public class FormatHelper {

    /**
	 * Creates a formatted-date node with the given
	 * ISO language and country strings.
     */
    public static Node dateTimeElement(long date, String language, String country) {
        Locale l = new Locale(language, country);  
        return  dateTimeElement(date, l);
     }
    
    public static Node dateTimeElement (long time) {
    	return dateTimeElement(time, Locale.getDefault());
    }
    

	/**
	 * Create an XML element to represent this system time in the current locale.
	 * Enables XSLT stylesheets to display content, without needing to do the work
	 * of internationalization.
	 */
    public static Node dateTimeElement (long time, Locale locale) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element dateNode = doc.createElement("formatted-date");
            
            // Works in most locales
            SimpleDateFormat df = (SimpleDateFormat)DateFormat.
				getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
            
            Date d = new Date(time);
            df.applyPattern("MMMM"); 
            addChild(dateNode, "month", df.format(d));
            df.applyPattern("EEEE"); 
            addChild(dateNode, "day-of-week", df.format(d));
            df.applyPattern("yyyy"); 
            addChild(dateNode,"year", df.format(d));
			df.applyPattern("dd"); 
			addChild(dateNode, "day-of-month", df.format(d));
			df.applyPattern("h"); 
			addChild(dateNode, "hours", df.format(d));
			df.applyPattern("mm"); 
			addChild(dateNode, "minutes", df.format(d));
			df.applyPattern("a"); 
			addChild(dateNode, "am-pm", df.format(d));
            return  dateNode;
        } 
        catch (Exception ex) {
        	throw  new XsltFormattingException("Failed to create XML date element", ex);
        }
    }
    
    public static class XsltFormattingException extends NestedRuntimeException {
		public XsltFormattingException(String msg, Throwable ex) {
			super(msg, ex);
		}
	}
    
    /**
     * Format a currency amount in a given locale
     */
    public static String currency(double amount, Locale locale) {
    	NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    	return nf.format(amount);
    }
    
    
     /**
     * Format a currency amount in a given locale
     */
    public static String currency(double amount, String language, String country) {
    	Locale locale = null;
    	if (language == null || country == null) {
    		locale = Locale.getDefault();
    	}
    	else {
        	 locale = new Locale(language, country);  
    	}
    	return currency(amount, locale);
    }

    /**
     * Utility method for adding text nodes.
     */
    private static void addChild (Node parent, String name, String text) {
        Element child = parent.getOwnerDocument().createElement(name);
        child.appendChild(parent.getOwnerDocument().createTextNode(text));
        parent.appendChild(child);
    }
}




