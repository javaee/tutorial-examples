/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.converters;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * <p>The CreditCardConverter class accepts a credit card number of type String
 * and strips blanks and
 * <code>"-"</code>, if any, from it. It also formats the credit card number so
 * that a blank space separates every four characters. Blanks and
 * <code>"-"</code> characters are the expected delimiters that could be used as
 * part of a CreditCardNumber.</p>
 */
@FacesConverter("ccno")
public class CreditCardConverter implements Converter {

    /*
     * <p>The message identifier of the Message to be created if the conversion
     * fails. The message format string for this message may optionally include
     * <code>{0}</code> and
     * <code>{1}</code> placeholders, which will be replaced by the object and
     * value.</p>
     */
    public static final String CONVERSION_ERROR_MESSAGE_ID = "ConversionError";
    private static final Logger logger =
            Logger.getLogger("dukesbookstore.converters.CreditCardConverter");

    public CreditCardConverter() {
    }

    /**
     * <p>Parses the CreditCardNumber and strips any blanks or
     * <code>"-"</code> characters from it.</p>
     */
    @Override
    public Object getAsObject(FacesContext context,
            UIComponent component, String newValue)
            throws ConverterException {

        logger.log(Level.INFO, "Entering CreditCardConverter.getAsObject");

        if (newValue.isEmpty()) {
            return null;
        }

        // Since this is only a String to String conversion,
        // this conversion does not throw ConverterException.

        String convertedValue = newValue.trim();
        if ((convertedValue.contains("-")) || (convertedValue.contains(" "))) {

            char[] input = convertedValue.toCharArray();
            StringBuilder builder = new StringBuilder(input.length);

            for (int i = 0; i < input.length; ++i) {
                if ((input[i] == '-') || (input[i] == ' ')) {
                } else {
                    builder.append(input[i]);
                }
            }

            convertedValue = builder.toString();
        }
        logger.log(Level.INFO, "Converted value is {0}", convertedValue);
        return convertedValue;
    }

    /**
     * Formats the value by inserting space after every four characters for
     * better readability if they don't already exist. In the process converts
     * any
     * <code>"-"</code> characters into blanks for consistency.
     */
    @Override
    public String getAsString(FacesContext context,
            UIComponent component, Object value)
            throws ConverterException {

        String inputVal = null;

        logger.log(Level.INFO, "Entering CreditCardConverter.getAsString");

        if (value == null) {
            return "";
        }

        // Value must be of a type that can be cast to a String.
        try {
            inputVal = (String) value;
        } catch (ClassCastException ce) {
            FacesMessage errMsg = new FacesMessage(CONVERSION_ERROR_MESSAGE_ID);
            FacesContext.getCurrentInstance().addMessage(null, errMsg);
            throw new ConverterException(errMsg.getSummary());
        }

        // Insert spaces after every four characters for better    
        // readability if they are not already present.   
        char[] input = inputVal.toCharArray();
        StringBuilder builder = new StringBuilder(input.length + 3);

        for (int i = 0; i < input.length; ++i) {
            if (((i % 4) == 0) && (i != 0)) {
                if ((input[i] != ' ') || (input[i] != '-')) {
                    builder.append(" ");

                    // if there any "-"'s convert them to blanks.    
                } else if (input[i] == '-') {
                    builder.append(" ");
                }
            }

            builder.append(input[i]);
        }

        String convertedValue = builder.toString();
        logger.log(Level.INFO, "Converted value is {0}", convertedValue);

        return convertedValue;
    }
}
