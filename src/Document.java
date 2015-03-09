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
	
	private boolean sortedLabel;
	
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
	
	public Document(DecisionTreeClassifier main, String line, boolean train)
	{
		this.main = main;
		this.documentString = line;
		String ll = line.substring(0, 1);
		if (ll.equals("1"))
		{
			label = true;
		}
		else if (ll.equals("0"))
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
			if (wordString.equals(""))
				continue;
			Boolean bb = DecisionTreeClassifier.STOP_WORDS.get(wordString);
			if (bb != null)
				continue;
			Term tt = wordVector.get(wordString);
			if (tt == null)
			{
				Term nw = new Term();
				wordVector.put(wordString, nw);
				if (train)
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

		rr = rr.replaceAll("ing$|ed$|s$|es$", "");
		
		return rr;
	}
	
	public double getTermWeight(String term)
	{
		Term tt = wordVector.get(term);
		if (tt == null)
			return 0;
		return tt.weight;
	}

	public boolean isLabel()
	{
		return label;
	}

	@Override
	public String toString()
	{
		return documentString;
	}

	public boolean isSortedLabel()
	{
		return sortedLabel;
	}

	public void setSortedLabel(boolean sortedLabel)
	{
		this.sortedLabel = sortedLabel;
	}
}
