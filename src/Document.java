import org.omg.CORBA.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hamster
 * Date: 3/7/2015
 * Time: 12:35 AM
 */
public class Document
{
	DecisionTreeClassifier main;
	
	private boolean label;
	private String documentString;
	
	private HashMap<String, Term> wordVector;
	/*private HashMap<String, Double> wordTF;
	private HashMap<String, Double> wordWeight;
	*/
	public class Term
	{
		public int num;
		public double tf;
		public double weight;

		public Term()
		{
			num = 1;
		}
	}
	
	public Document(DecisionTreeClassifier main, String line)
	{
		this.main = main;
		System.out.println(line);
		String ll = line.substring(0, 1);
		if (ll.equals("1 "))
		{
			label = true;
		}
		else
		{
			label = false;
		}
		
		wordVector = new HashMap<String, Term>(100);
		//wordTF = new HashMap<String, Double>(100);
		//wordWeight = new HashMap<String, Double>(100);
		
		int maxFreq = 1;
		
		String[] tokens = line.substring(2).split(" ");
		for(String token:tokens)
		{
			String wordString = wordNormalizer(token);
			Boolean bb = DecisionTreeClassifier.STOP_WORDS.get(wordString);
			if (bb != null)
				continue;
			Term tt = wordVector.get(wordString);
			if (tt == null)
			{
				Term nw = new Term();
				wordVector.put(wordString, nw);
				main.incIDFTerm(wordString);
			}
			else
			{
				tt.num++;
				if (tt.num > maxFreq)
					maxFreq = tt.num;
			}
			
			
		}
		
		for (String kk:wordVector.keySet())
		{
			Term ww = wordVector.get(kk);
			int wn = ww.num;
			ww.tf = 0.5 + 0.5 * wn / maxFreq; 
			//System.out.print(kk + ":" + wordVector.get(kk) + "," + tf + " | ");
		}
	}
	
	public void calcWeight()
	{
		for (String kk:wordVector.keySet())
		{
			Term ww = wordVector.get(kk);
			ww.weight = ww.tf * main.getIDF(kk);
			System.out.println(kk + " w:" + ww.weight);
		}
	}
	
	public void setAttrs(HashMap<String, Boolean>map)
	{
		for (String kk:wordVector.keySet())
		{
			map.put(kk, true);
		}
	}
	
	public static String wordNormalizer(String word)
	{
		String rr = word.toLowerCase();
		rr = rr.replaceAll("[^a-zA-Z ]", "");
		if (rr.length() < 3)
			return rr;

		rr = rr.replaceAll("ing$|ed$", "");
		
		return rr;
	}
	
	public double getTermWeight(String term)
	{
		return wordVector.get(term).weight;
	}

	public boolean isLabel()
	{
		return label;
	}
}
