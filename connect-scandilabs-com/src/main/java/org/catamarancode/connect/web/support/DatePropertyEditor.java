package org.catamarancode.connect.web.support;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.catamarancode.entity.support.PersistableBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Check with inspectortime for singleton issues...
 * @author mkvalsvik
 *
 */
public class DatePropertyEditor extends PropertyEditorSupport {
	
	private static Logger logger = LoggerFactory
			.getLogger(DatePropertyEditor.class);

	
	private String format;
	
	public DatePropertyEditor(String format) {
		this.format = format;
	}
	
	public void setAsText(String value) {
        try {
            setValue(new SimpleDateFormat(format).parse(value));
        } catch(ParseException e) {
        	logger.warn(String.format("Could not convert %s to a date using format '%s': %s", value, this.format, e.getMessage()));
            setValue(null);
        }
    }

    public String getAsText() {
    	Date date = (Date) getValue();
    	if (date != null) {
    		return new SimpleDateFormat(format).format(date);
    	}
    	return "";
    }     

}
