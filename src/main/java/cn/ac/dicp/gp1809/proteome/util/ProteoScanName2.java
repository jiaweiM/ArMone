package cn.ac.dicp.gp1809.proteome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author JiaweiMao
 * @since 27 Jun 2018, 4:47 PM
 * @version 1.0.0
 */
public class ProteoScanName2 extends AbstractKnownScanName {

	private static final Pattern PATTERN = Pattern.compile(
	        "(.*)\\.([\\d]*)\\.([\\d]*)\\.[\\d]*[\\W]*.*", Pattern.CASE_INSENSITIVE);
	
	private String scanName;
	private short charge;
	
	public ProteoScanName2(String scanName){

		this.scanName = scanName;
		Matcher matcher = PATTERN.matcher(scanName);

		if (matcher.matches()) {
			
			int count = matcher.groupCount();
			if(count==3){
				
				String baseName = matcher.group(1);
				int scanBeg = Integer.parseInt(matcher.group(2));
				int scanEnd = Integer.parseInt(matcher.group(3));
				
				this.setBaseName(baseName);
				this.setScanNumBeg(scanBeg);
				this.setScanNumEnd(scanEnd);
				
			}else{
				throw new IllegalArgumentException(
				        "Error in parsing the formatted scanname: \"" + scanName
				                + "\".");
			}
		}else{
			throw new IllegalArgumentException(
			        "Error in parsing the formatted scanname: \"" + scanName
			                + "\".");
		}
	
	}

	public ProteoScanName2(String baseName, int scanBeg, int scanEnd) {
		this.setBaseName(baseName);
		this.setScanNumBeg(scanBeg);
		this.setScanNumEnd(scanEnd);
	}
	
	public static boolean isFormat(String scanNum) {
		return (PATTERN.matcher(scanNum).matches());
	}

	@Override
	public void setCharge(short charge) {
		this.charge = charge;
	}
	
	/**
	 * The charge state. <b>Always return 0</b>
	 */
	@Override
	public short getCharge() {
		return charge;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName#deepClone()
	 */
	@Override
	public IKnownFormatScanName deepClone() {
		// TODO Auto-generated method stub
		return this.clone();
	}

	public ProteoScanName2 clone() {
		try {
	        return (ProteoScanName2) super.clone();
        } catch (CloneNotSupportedException e) {
	        e.printStackTrace();
        }
        
        return null;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getScanName()
	 */
	@Override
	public String getScanName() {
		// TODO Auto-generated method stub
		
		if(this.scanName==null || this.scanName.length()==0){
			
			StringBuilder sb = new StringBuilder(18);
			String basename = this.getBaseName();
			if(basename != null && basename.length()>0)
				sb.append(basename).append(", ");
			
			int scanBeg = this.getScanNumBeg();
			int scanEnd = this.getScanNumEnd();
			
			sb.append(scanBeg);
			if(scanBeg != scanEnd)
				sb.append(" - ").append(scanEnd);
			
			this.scanName = sb.toString();
			
		}
		return this.scanName;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pattern p = ProteoScanName2.PATTERN;
		
		String s = "50mM_CON1.2229.2229.2 File:~50mM_CON1.RAW~, NativeID:~controllerType=0 controllerNumber=1 scan=2229~";
		ProteoScanName2 sn = new ProteoScanName2(s);
		System.out.println(sn.getBaseName()+"\t"+sn.getScanNumBeg());

		System.out.println(s);
	}
	
}
