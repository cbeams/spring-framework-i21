package com.interface21.samples.countries.utils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.Locale;


/**
 * Localized conversions
 * <br>Locales are passed to the constructor.
 * <br>This class is useful for many conversions for the same locale,
 * otherwise a static implementation could be better.
 *
 *  @author Jean-Pierre Pawlak
 */
public class Convert
{
    //~ Instance fields --------------------------------------------------------

    private Locale       inLocale;
    private Locale       outLocale;
    private DateFormat   inDate_f = null;
    private DateFormat   outDate_f = null;
    private DateFormat   inDatetime_f = null;
    private DateFormat   outDatetime_f = null;
    private DateFormat   inTime_f = null;
    private DateFormat   outTime_f = null;
    private NumberFormat inNumber_f = null;
    private NumberFormat outNumber_f = null;

    //~ Constructors -----------------------------------------------------------

    public Convert(Locale inLocale, Locale outLocale) {
        if ( null == inLocale ) {
            String msg = "Convert init: inLocale is null";

            //log.error(msg);
            throw new RuntimeException( msg );
        }

        if (null == outLocale) {
            String msg = "Convert init: outLocale is null";

            //log.error(msg);
            throw new RuntimeException( msg );
        }

        this.inLocale  = inLocale;
        this.outLocale = outLocale;
    }

    //~ Methods ----------------------------------------------------------------

    public String cDate( String data ) throws ParseException {
        if (( data == null) || (data.length() < 1 )) {
            return data;
        }
        if (null == inDate_f) {
            inDate_f = DateFormat.getDateInstance( DateFormat.SHORT, inLocale );
        }
        if (null == outDate_f) {
            outDate_f = DateFormat.getDateInstance( DateFormat.SHORT, outLocale );
        }
        Date lDate = null;
        lDate = inDate_f.parse(data);
        return outDate_f.format(lDate);
    }

    public Date getDate(String data) throws ParseException {
        if ( (data == null) || (data.length() < 1 )) {
            return null;
        }
        if (null == inDate_f) {
            inDate_f = DateFormat.getDateInstance( DateFormat.SHORT, inLocale );
        }
        Date lDate = null;
        lDate = inDate_f.parse(data);
        return lDate;
    }

    public String cDateTime( String data ) throws ParseException {
        if ((data == null) || (data.length() < 1)) {
            return data;
        }
        if (null == inDatetime_f) {
            inDatetime_f = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, inLocale );
        }
        if (null == outDatetime_f) {
            outDatetime_f = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, outLocale );
        }
        Date lDate = null;
        lDate = inDatetime_f.parse(data);
        return outDatetime_f.format(lDate);
    }

    public Date getDateTime(String data) throws ParseException {
        if ((data == null) || (data.length() < 1 )) {
            return null;
        }
        if (null == inDatetime_f) {
            inDatetime_f = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, inLocale );
        }
        Date lDate = null;
        lDate = inDatetime_f.parse(data);
        return lDate;
    }

    public String cNumber(String data) throws ParseException {
        if ((data == null) || (data.length() < 1 )) {
            return data;
        }
        if (null == inNumber_f) {
            inNumber_f = NumberFormat.getNumberInstance( inLocale );
        }
        if (null == outNumber_f) {
            outNumber_f = NumberFormat.getNumberInstance( outLocale );
        }
        double lNumber = inNumber_f.parse( data ).doubleValue(  );
        return outNumber_f.format( lNumber );
    }

    public Double getNumber(String data) throws ParseException
    {
        if ((data == null) || (data.length() < 1 )) {
            return null;
        }
        if (null == inNumber_f) {
            inNumber_f = NumberFormat.getNumberInstance( inLocale );
        }
        Double lNumber = new Double(inNumber_f.parse(data).doubleValue());
        return lNumber;
    }

    public String cCurrency(String data) throws ParseException {
        if ((data == null) || (data.length() < 1 )) {
            return data;
        }
        if (null == inNumber_f) {
            inNumber_f = NumberFormat.getNumberInstance( inLocale );
        }
        if (null == outNumber_f) {
            outNumber_f = NumberFormat.getNumberInstance( outLocale );
        }
        double lNumber = inNumber_f.parse(data).doubleValue();
        return outNumber_f.format(lNumber);
    }

    public String fromDate(Date inDate) {
        if (null == inDate) {
            return null;
        }
        if (null == inDate_f) {
            inDate_f = DateFormat.getDateInstance( DateFormat.SHORT, inLocale );
        }
        return inDate_f.format(inDate);
    }

    public String fromDateTime(Date inDate) {
        if (null == inDate) {
            return null;
        }
        if (null == inDatetime_f) {
            inDatetime_f = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, inLocale );
        }
        return inDatetime_f.format( inDate );
    }

    public String fromTime(Date inDate)
    {
        if (null == inDate) {
            return null;
        }
        if (null == inTime_f) {
            inTime_f = DateFormat.getTimeInstance( DateFormat.SHORT, inLocale );
        }
        return inTime_f.format( inDate );
    }

    public String fromNumber(Double inNumber) {
        if (null == inNumber) {
            return null;
        }
        if (null == inNumber_f) {
            inNumber_f = NumberFormat.getNumberInstance( inLocale );
        }
        return inNumber_f.format( inNumber );
    }

    // -------------------------------------------------------------- Accessors
    // ------------------------------------------------------------------------

    public DateFormat getInDate_f() {
        return inDate_f;
    }

    public DateFormat getInDatetime_f() {
        return inDatetime_f;
    }

    public Locale getInLocale() {
        return inLocale;
    }

    public NumberFormat getInNumber_f() {
        return inNumber_f;
    }

    public DateFormat getOutDate_f() {
        return outDate_f;
    }

    public DateFormat getOutDatetime_f() {
        return outDatetime_f;
    }

    public Locale getOutLocale() {
        return outLocale;
    }

    public NumberFormat getOutNumber_f() {
        return outNumber_f;
    }

    public void setInDate_f(DateFormat inDate_f) {
        this.inDate_f = inDate_f;
    }

    public void setInDatetime_f(DateFormat inDatetime_f) {
        this.inDatetime_f = inDatetime_f;
    }

    public void setInLocale(Locale inLocale) {
        this.inLocale = inLocale;
    }

    public void setInNumber_f(NumberFormat inNumber_f) {
        this.inNumber_f = inNumber_f;
    }

    public void setOutDate_f(DateFormat outDate_f) {
        this.outDate_f = outDate_f;
    }

    public void setOutDatetime_f(DateFormat outDatetime_f) {
        this.outDatetime_f = outDatetime_f;
    }

    public void setOutLocale(Locale outLocale) {
        this.outLocale = outLocale;
    }

    public void setOutNumber_f(NumberFormat outNumber_f) {
        this.outNumber_f = outNumber_f;
    }

    public DateFormat getInTime_f() {
        return inTime_f;
    }

    public DateFormat getOutTime_f() {
        return outTime_f;
    }

    public void setInTime_f(DateFormat inTime_f) {
        this.inTime_f = inTime_f;
    }

    public void setOutTime_f(DateFormat outTime_f) {
        this.outTime_f = outTime_f;
    }
}
