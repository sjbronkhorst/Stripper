package linalg;

import java.util.StringTokenizer;

public class InlineCalculator {
	
	private static double _product(String text){ // mult & div sweep
		double calc = Double.NaN;		
		StringTokenizer st = new StringTokenizer(text, "*/", true);
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			if(token.equals("*")){
				calc *= Double.parseDouble(token = st.nextToken());
			}
			else if(token.equals("/")){
				calc /= Double.parseDouble(token = st.nextToken());				
			}
			else {
				calc = Double.parseDouble(token);
			}
		}
		return calc;
	}
	
	private static double _sum(String text){ // plus & minus sweep
		StringTokenizer st = new StringTokenizer(text, "+-", true);
		double calc = 0.0;
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			if(token.equals("+")){
				calc += _product(st.nextToken());
			}
			else if(token.equals("-")){
				calc -= _product(st.nextToken());
			}
			else {
				calc = _product(token);
			}
		}
		return calc;
	}
	
	private static String _brackets(String text){
		if(text.contains("(")){	
			int _openbracket = text.lastIndexOf('(');
			int _closebracket = text.substring(_openbracket).indexOf(')')+_openbracket;
			String inside = text.substring(_openbracket+1,_closebracket);
			double _value = _sum(inside);
			String _update = text.replace("("+inside+")", _value+"");
			return _brackets(_update);
		}
		return text;
	}
	
	public static double calc(String text) throws InvalidParameterException {
		try {
			if(!text.startsWith("="))
				throw new InvalidParameterException(text);
			return _sum(_brackets(text.substring(1)));
		}
		catch(NumberFormatException nfe){
			throw new InvalidParameterException(text);
		}
	}
	
	public static void main(String[] args) {
//		String s1  = "3.2*23.1/23.1*3";
//		System.out.println(s1 +" = "+_inner(s1));
//		String s2 = "3+2.1*2+1.2-3.2";		
//		System.out.println(s2 +" = "+_middle(s2));
		String s3 = "=-12.32*(23.1*(1*(3.2-.2)/2+4)+(3*1.2))/5+(10)*3/(4.2)*1";
		System.err.println(s3 +" = ");
		try {
			System.err.println(calc(s3));
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			System.err.println(e.getLocalizedMessage());
		}
	}
	
}
