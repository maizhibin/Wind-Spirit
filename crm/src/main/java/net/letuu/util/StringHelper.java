
package net.letuu.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class StringHelper {

	private static final int ALIAS_TRUNCATE_LENGTH = 10;

	private StringHelper() { /* static methods only - hide constructor */
	}

	public static String join(String seperator, String[] strings) {
		int length = strings.length;
		if ( length == 0 ) return "";
		StringBuffer buf = new StringBuffer( length * strings[0].length() )
				.append( strings[0] );
		for ( int i = 1; i < length; i++ ) {
			buf.append( seperator ).append( strings[i] );
		}
		return buf.toString();
	}

	public static String join(String seperator, Iterator objects) {
		StringBuffer buf = new StringBuffer();
		if ( objects.hasNext() ) buf.append( objects.next() );
		while ( objects.hasNext() ) {
			buf.append( seperator ).append( objects.next() );
		}
		return buf.toString();
	}

	public static String[] add(String[] x, String sep, String[] y) {
		String[] result = new String[x.length];
		for ( int i = 0; i < x.length; i++ ) {
			result[i] = x[i] + sep + y[i];
		}
		return result;
	}

	public static String repeat(String string, int times) {
		StringBuffer buf = new StringBuffer( string.length() * times );
		for ( int i = 0; i < times; i++ ) buf.append( string );
		return buf.toString();
	}


	public static String replace(String template, String placeholder, String replacement) {
		return replace( template, placeholder, replacement, false );
	}

	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
		int loc = template.indexOf( placeholder );
		if ( loc < 0 ) {
			return template;
		}
		else {
			final boolean actuallyReplace = !wholeWords ||
					loc + placeholder.length() == template.length() ||
					!Character.isJavaIdentifierPart(template.charAt(loc + placeholder.length()));
			String actualReplacement = actuallyReplace ? replacement : placeholder;
			return new StringBuffer( template.substring( 0, loc ) )
					.append( actualReplacement )
					.append( replace( template.substring( loc + placeholder.length() ),
							placeholder,
							replacement,
							wholeWords ) ).toString();
		}
	}

	public static int getStringLength(String str){
		int result=0;
		if(str!=null){
			result=str.getBytes().length;
		}
		return result;
	}
	
	public static String replaceOnce(String template, String placeholder, String replacement) {
		int loc = template.indexOf( placeholder );
		if ( loc < 0 ) {
			return template;
		}
		else {
			return new StringBuffer( template.substring( 0, loc ) )
					.append( replacement )
					.append( template.substring( loc + placeholder.length() ) )
					.toString();
		}
	}


	public static String[] split(String seperators, String srcString) {
		
		return split( seperators, srcString, false );
	}

	public static String[] split(String seperators, String srcString, boolean include) {
		String[] result =null;
		if(srcString!=null){
			StringTokenizer tokens = new StringTokenizer( srcString, seperators, include );
			result = new String[tokens.countTokens()];
			int i = 0;
			while ( tokens.hasMoreTokens() ) {
				result[i++] = tokens.nextToken();
			}
		}
		return result;
	}

	public static String unqualify(String qualifiedName) {
		return qualifiedName.substring( qualifiedName.lastIndexOf(".") + 1 );
	}

	public static String qualifier(String qualifiedName) {
		int loc = qualifiedName.lastIndexOf(".");
		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
	}

	public static String[] suffix(String[] columns, String suffix) {
		if ( suffix == null ) return columns;
		String[] qualified = new String[columns.length];
		for ( int i = 0; i < columns.length; i++ ) {
			qualified[i] = suffix( columns[i], suffix );
		}
		return qualified;
	}

	private static String suffix(String name, String suffix) {
		return ( suffix == null ) ? name : name + suffix;
	}

	public static String root(String qualifiedName) {
		int loc = qualifiedName.indexOf( "." );
		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
	}

	public static boolean booleanValue(String tfString) {
		String trimmed = tfString.trim().toLowerCase();
		return trimmed.equals( "true" ) || trimmed.equals( "t" );
	}

	public static String toString(Object[] array) {
		int len = array.length;
		if ( len == 0 ) return "";
		StringBuffer buf = new StringBuffer( len * 12 );
		for ( int i = 0; i < len - 1; i++ ) {
			buf.append( array[i] ).append(", ");
		}
		return buf.append( array[len - 1] ).toString();
	}

	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
		String[] result = new String[]{string};
		while ( placeholders.hasNext() ) {
			result = multiply( result, (String) placeholders.next(), ( String[] ) replacements.next() );
		}
		return result;
	}

	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
		String[] results = new String[replacements.length * strings.length];
		int n = 0;
		for ( int i = 0; i < replacements.length; i++ ) {
			for ( int j = 0; j < strings.length; j++ ) {
				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
			}
		}
		return results;
	}

	public static int countUnquoted(String string, char character) {
		if ( '\'' == character ) {
			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
		}
		if (string == null)
			return 0;
		// Impl note: takes advantage of the fact that an escpaed single quote
		// embedded within a quote-block can really be handled as two seperate
		// quote-blocks for the purposes of this method...
		int count = 0;
		int stringLength = string.length();
		boolean inQuote = false;
		for ( int indx = 0; indx < stringLength; indx++ ) {
			char c = string.charAt( indx );
			if ( inQuote ) {
				if ( '\'' == c ) {
					inQuote = false;
				}
			}
			else if ( '\'' == c ) {
				inQuote = true;
			}
			else if ( c == character ) {
				count++;
			}
		}
		return count;
	}

	public static boolean isNotEmpty(String string) {
		return string != null && string.length() > 0;
	}

	public static String qualify(String prefix, String name) {
		if ( name == null ) throw new NullPointerException();
		return new StringBuffer( prefix.length() + name.length() + 1 )
				.append(prefix)
				.append('.')
				.append(name)
				.toString();
	}

	public static String[] qualify(String prefix, String[] names) {
		if ( prefix == null ) return names;
		int len = names.length;
		String[] qualified = new String[len];
		for ( int i = 0; i < len; i++ ) {
			qualified[i] = qualify( prefix, names[i] );
		}
		return qualified;
	}

	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
		int matchAt = -1;
		for ( int i = 0; i < string.length(); i++ ) {
			int curMatch = sqlString.indexOf( string.charAt( i ), startindex );
			if ( curMatch >= 0 ) {
				if ( matchAt == -1 ) { // first time we find match!
					matchAt = curMatch;
				}
				else {
					matchAt = Math.min(matchAt, curMatch);
				}
			}
		}
		return matchAt;
	}

	public static String truncate(String string, int length) {
		if ( string.length() <= length ) {
			return string;
		}
		else {
			return string.substring( 0, length );
		}
	}

	/**
	 * Generate a nice alias for the given class name or collection role
	 * name and unique integer. Subclasses of Loader do <em>not</em> have 
	 * to use aliases of this form.
	 * @return an alias of the form <tt>foo1_</tt>
	 */
	public static String generateAlias(String description, int unique) {
		return generateAlias(description) +
			Integer.toString(unique) +
			'_';
	}

	public static String generateAlias(String description) {
		final String result = truncate( unqualify(description), ALIAS_TRUNCATE_LENGTH )
			.toLowerCase()
			.replace( '$', '_' ); //classname may be an inner class
		if ( Character.isDigit(result.charAt(result.length() - 1)) ) {
			return result + "x"; //ick!
		}
		else {
			return result;
		}
	}
	
	public static String toUpperCase(String str) {
		return str==null ? null : str.toUpperCase();
	}
  
  /**
   * 方法说明：带空替换的大写转换类
   * @param str 要转换的字符串
   * @return 替换后的串
   * @author Zorel 2006-09-17
   */
  public static String toUpperCaseNullReplace(String str){
    return str==null ? "" : toUpperCase(str);
  }
	
	public static String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace( 8-formatted.length(), 8, formatted );
		return buf.toString();
	}

	public static String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace( 4-formatted.length(), 4, formatted );
		return buf.toString();
	}
	
    public synchronized static String convertHalfToFull(String sMt){
        String sReturn = sMt;
        if(sReturn==null)
            return sReturn;
        try {
            sReturn = replace(sReturn,"'","''");
            
            //sReturn = sReturn.replace('"','“');
        }
        catch (Exception ex) {
            return sMt;
        }
        return sReturn;
    }

    public synchronized static String convertDbToHtml(String sMt){
        String sReturn = sMt;
        if(sReturn==null)
            return sReturn;
        try {
            sReturn = replace(sReturn,"''","&apos;");
            
            // modified by shenxs 注释以下行，如不注释图片无法显示
            sReturn = replace(sReturn,"\"","&quot;");
            
            //sReturn = sReturn.replace('"','“');
        }
        catch (Exception ex) {
            return sMt;
        }
        return sReturn;
    }    
    
    public synchronized static String convertFullToHalf(String sMt){
        String sReturn = sMt;
        if(sReturn==null)
            return sReturn;
        try {
            sReturn = replace(sReturn,"''","'");
            //sReturn = sReturn.replace('"','“');
        }
        catch (Exception ex) {
            return sMt;
        }
        return sReturn;
    }
    
    public synchronized static int convertStringToInt(String sMt){
    	int result=0;
    	try{
    		if(sMt!=null){
    			result = Double.valueOf(sMt).intValue();
    		}
    		
    	}catch(Exception e){
    		
    	}
    	return result;
    }
    
    public synchronized static double convertStringToDouble(String sMt){
    	double result=0;
    	try{
    		result = Double.valueOf(sMt).doubleValue();
    	}catch(Exception e){
    		
    	}
    	return result;
    }

    public synchronized static float convertStringToFloat(String sMt){
    	float result=0;
    	try{
    		//edit by gongjj:凡是FLOAT类型均通过NUMBERFORMAT进行格式化后再转换
    		BigDecimal bigDecimal = new BigDecimal(sMt);
    		BigDecimal one = new BigDecimal("1");
    		result = bigDecimal.divide(one, 2, BigDecimal.ROUND_HALF_UP).floatValue();
    		//DecimalFormat numberForamt = new DecimalFormat("0.0000");
    		//System.out.print(Double.valueOf(sMt).doubleValue());
    		//result = numberForamt.parse(numberForamt.format(Double.valueOf(sMt).doubleValue())).floatValue();
    		//
    		//result = numberForamt.parse(sMt).floatValue();
    		//result = Float.valueOf(sMt).floatValue();
    	}catch(Exception e){
    		
    	}
    	return result;
    }
    
    public static Timestamp convertStringToTimeStamp(String sMt){
    	Timestamp result = null;
        try
        {
        	java.util.Date utilDate=null;
        	if(sMt!=null&&sMt.length()>0){       
        		int length=sMt.length();

        		if(length>17){
            		utilDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sMt);
        		}else if(length>13){        
        			utilDate=new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(sMt);
        		}else if(length>=10){      
        			utilDate=new SimpleDateFormat("yyyy-MM-dd").parse(sMt);
        		}else if(length>=8){
        			utilDate=new SimpleDateFormat("HH:mm:ss").parse(sMt);
        		}else if(length==7){
        			utilDate=new SimpleDateFormat("yyyy-MM").parse(sMt);
        		}else if(length==5){
        			utilDate=new SimpleDateFormat("HH:mm").parse(sMt);
        		}
        		
        		result = new Timestamp( utilDate.getTime());
        		
        	}
        	//result=sdf.parse(sMt);
            //result = Date.valueOf(sMt);
        }
        catch(Exception e) {
        	//e.printStackTrace();
        }
        
        return result;
    }

    
    public synchronized static Date convertStringToDate(String sMt){
        Date result = null;
        try
        {
        	java.util.Date utilDate=null;
        	if(sMt!=null&&sMt.length()>0){       
        		int length=sMt.length();

        		if(length>17){
            		utilDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sMt);
        		}else if(length>13){        
        			utilDate=new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(sMt);
        		}else if(length>=10){      
        			utilDate=new SimpleDateFormat("yyyy-MM-dd").parse(sMt);
        		}else if(length>=8){
        			utilDate=new SimpleDateFormat("HH:mm:ss").parse(sMt);
        		}else if(length==7){
        			utilDate=new SimpleDateFormat("yyyy-MM").parse(sMt);
        		}else if(length==5){
        			utilDate=new SimpleDateFormat("HH:mm").parse(sMt);
        		}
        		
        		result = new Date( utilDate.getTime());
        		
        	}
        	//result=sdf.parse(sMt);
            //result = Date.valueOf(sMt);
        }
        catch(Exception e) {
        	//e.printStackTrace();
        }
        
        return result;
    }
    /**
     * 转化字符串为bool值,sMt = "1" or "true" 时return true；sMt = "0" or "false"时return false；
     * @param sMt 字符串；
     * @return
     * modify by L.M.X
     * date 2006-5-26
     * modify by L.M.X 2006-06-01 增加sMt为空和空串情况的时候处理；
     */
    public synchronized static boolean convertStringToBoolean(String sMt){
    	boolean result=false;
    	try{
    		if (sMt==null||sMt.trim().equals("")) return false;
    		
    		if (sMt.equalsIgnoreCase("true")||sMt.equalsIgnoreCase("1")){
    			result = true;
    		}
    		else if (sMt.equalsIgnoreCase("false")||sMt.equalsIgnoreCase("0")){
    			result = false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    public static String toUtf8String(String s) {///源于网上
    	StringBuffer sb = new StringBuffer();
    	for (int i=0;i<s.length();i++) { 
    		char c = s.charAt(i); 
    		if (c >= 0 && c <= 255) { 
    			sb.append(c); 
    		} else { 
    			byte[] b; 
    			try { 
    				b = Character.toString(c).getBytes("utf-8");
    			} catch (Exception ex) {
    				System.out.println(ex);
    				b = new byte[0]; 
    			} 
    			for (int j = 0; j < b.length; j++) { 
    				int k = b[j]; 
    				if (k < 0) k += 256; 
    				sb.append("%" + Integer.toHexString(k).
    						toUpperCase()); 
    			} 
    		} 
    	} 
    	return sb.toString(); 
    } 
    
    
    public static String convertStringToXML(String value){
        String sReturn = value;
        if(sReturn==null)
            return sReturn;
        try {
            sReturn = replace(sReturn,"'","&apos;");
            sReturn = replace(sReturn,"\"","&quot;");
            sReturn = replace(sReturn,"<","&lt;");
            sReturn = replace(sReturn,">","&gt;");
            sReturn = replace(sReturn,"&","&amp;");
            
            //sReturn = sReturn.replace('"','“');
        }
        catch (Exception ex) {
            return value;
        }
        return sReturn;
    }
    
    public static String innerTrim(String value){
    	if(value!=null){
    		value=replace(value," ","");    		
    	}
    	return value;
    }
    public static boolean isUtf8Url(String text) {
        text = text.toLowerCase();
        int p = text.indexOf("%");
        if (p != -1 && text.length() - p > 9) {
            text = text.substring(p, p + 9);
        }

        String sign = "";
        if (text.startsWith("%e"))
        {
            p = 0;
            for (int i = 0; p != -1; i++) {
                p = text.indexOf("%", p);
                if (p != -1)
                p++;
                sign += p;
            }
        }
        return sign.equals("147-1");
    }

    /*

        para0:一个字符串

        return:Map里返回两个元素，1 decode：默认字符，2 encode UTF－8加密后的字符

    */

    public static Map urlConvert(String url)
    {
        String decode,encode;
        try{
            if (isUtf8Url(url))
            {
                decode = URLDecoder.decode(url, "UTF-8");
                encode = url;
            }
            else
            {
                decode = url;
                encode = URLEncoder.encode(url, "UTF-8");
            }
            Map mp = new HashMap();
            mp.put("decode",decode);
            mp.put("encode",encode);
            return mp;

        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public static String convertCharSet(String src,String set,String destSet){
    	String result=src;
    	try {
    		if(set==null){
    			set="iso-8859-1";
    		}
    		if(destSet==null){
    			destSet="GBK";
    		}
    		result=new String(src.getBytes(set),destSet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
    }
    
    public static Timestamp convertStrToTimestamp(String value){
    	Date date=StringHelper.convertStringToDate(value);
		Timestamp timeStamp =null;
		if(date!=null){
			timeStamp = new Timestamp( date.getTime());;
		}
		return timeStamp;
    }
   
    public static boolean  isNumber(String value){
    	//"R\\d{2}_[A-Z]{2}\\d{2}(_\\d{2})?-\\d{11}-[1|2]\\d{3}-0[1-9]";
    	//"R05_QD01-34010000000-2007-12"; 
    	
    	String regEx="\\d+\\.{0,}\\d{0,}";
    	Pattern pattern = Pattern.compile(regEx);
    	
    	Matcher matcher = pattern.matcher(value);
    	
    	boolean result = matcher.matches(); 
    	return result;
    	
    }
    
    public static String getASCII(byte[] shortMsg){
    	StringBuffer result=new StringBuffer();
    	int length=shortMsg.length;
    	for(int i=0;i<length;i++){
    		byte b=shortMsg[i];
    		
    		String ascii= Integer.toHexString(b);
    		if(b<16&&b>=0)
    		   ascii="0"+ascii;
    		if(ascii.length()>2)
    			ascii=ascii.substring(6,8);
    		byte[] tempMsg=new byte[1];
    		tempMsg[0]=b;
    		String tempStr=new String(tempMsg);
    		result.append("/").append(tempStr).append(":").append(ascii);    		
    	}    	
    	//Res.log(Res.ERROR,"ascII:"+result.toString());
    	return result.toString().toUpperCase();
    }    
        
    public static String[] splitArithExp(String expStr){
    	//根据正则表达式分割字符串，分割符为+,-,*,/
    	String regEx="[+]|[-]|[*]|[//]";
    	Pattern pattern = Pattern.compile(regEx, 0);
    	
    	String values[] = pattern.split(expStr);
    	return values;
    }
    
    public static String[] splitParamExp(String expStr){
    	//根据正则表达式分割字符串，分割符为+,-,*,/
    	String regEx="[\\w+(\\w+,\\w+)\\w+]^[,]";
    	Pattern pattern = Pattern.compile(regEx);
    	
    	String values[] = pattern.split(expStr);
    	
    	return values;
    }
    
	public static void main(String[] argv){
	/*	 String strDecode="国中";
		 try {
			String strEncode = URLEncoder.encode("中国","UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
         System.out.println("原码"+strDecode);
         Map mp = urlConvert(strDecode);
         
        
         System.out.println("加密前：" + mp.get("decode"));
         System.out.println("加密后：" + mp.get("encode"));
		
		System.out.println(System.getProperty("file.separator"));
		
		System.out.println(Double.valueOf("2.65").intValue());
		*/
		System.out.println(StringHelper.convertStringToFloat("836369.72"));
		System.out.println(StringHelper.replace("333333333 456456456", " ", "&nbsp;"));
	}
}