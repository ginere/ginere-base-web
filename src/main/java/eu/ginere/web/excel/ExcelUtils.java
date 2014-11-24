/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: ExcelUtils.java,v 1.3 2006/05/07 14:42:40 ventura Exp $
 */
package eu.ginere.web.excel;

import org.apache.commons.lang.StringUtils;

//import venturasoft.util.lang.StringUtils;

/**
 * Use this action to import labels for the i18n module.
 * this action reads data from a text file and will parse it.
 * FILE: The file to upload
 * @author Angel Mendo
 * @version $Revision: 1.3 $
 */
class ExcelUtils  {

    static String COLUMN_SEPARATOR = "\t";
    static String LINE_SEPARATOR = "\n";
    static String DOS_LINE_SEPARATOR = "\r";

    static String ESC_COLUMN_SEPARATOR = "\\t";
    static String ESC_LINE_SEPARATOR = "\\n";
    static String ESC_DOS_LINE_SEPARATOR = "\\r";

	static String escape(String excelLine){

		if (excelLine != null) {
            String ret = StringUtils.replace(excelLine, COLUMN_SEPARATOR, ESC_COLUMN_SEPARATOR);
			
            ret = StringUtils.replace(ret, LINE_SEPARATOR, ESC_LINE_SEPARATOR);
            ret = StringUtils.replace(ret, DOS_LINE_SEPARATOR, ESC_DOS_LINE_SEPARATOR);

            return ret;
        } else {
            return "";
        }
	}
	
	static String unEscape(String excelLine){
        if (excelLine != null) {
            String ret = StringUtils.replace(excelLine, ESC_COLUMN_SEPARATOR, COLUMN_SEPARATOR);
            ret = StringUtils.replace(ret, ESC_LINE_SEPARATOR, LINE_SEPARATOR);
            ret = StringUtils.replace(ret, ESC_DOS_LINE_SEPARATOR, DOS_LINE_SEPARATOR);
            return ret;
        } else {
            return excelLine;
        }
	}
}
