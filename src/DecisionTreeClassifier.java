import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hamster
 * Date: 3/6/2015
 * Time: 11:16 PM
 */
public class DecisionTreeClassifier
{
	
	public static HashMap<String, Boolean> STOP_WORDS;
	
	public HashMap<String, Integer> termFreq;
	public HashMap<String, Double> termIDF;
	
	private ArrayList<Document> documents;
	private String testOut;
	
	private DTree tree;
	
	private int lowFreqCutOff;
	private int highFreqCutOff;
	
	private long startTime;
	private long treeTime;
	private long endTime;
	
	private double trainAccu;
	private double testAccu;
	
	public DecisionTreeClassifier()
	{
		InputStream in = System.in;

		INIT_STOP_WORDS();

		Scanner reader = new Scanner(in);
		String line = null;
		
		startTime = System.currentTimeMillis();

		termFreq = new HashMap<String, Integer>(300);
		termIDF = new HashMap<String, Double>(300);
		documents = new ArrayList<Document>(100);
		tree = new DTree();
		while (reader.hasNextLine())
		{
			line = reader.nextLine();
			if (line.equals(""))
				continue;
			// parse line to get label and data
			//System.out.println(line);
			Document doc = new Document(this, line, true);
			documents.add(doc);
		}
		lowFreqCutOff = (int)Math.round(documents.size() * 0.004);
		highFreqCutOff = (int)Math.round(documents.size() * 0.3);
		calcIDF();
		
		for (Document dd:documents)
		{
			dd.calcWeight();
		}
		
		tree.makeTree(documents);
		
		treeTime = System.currentTimeMillis() - startTime;
		
		trainAccu = tree.sortData(documents);
		/*BufferedReader fileReader = null;
		try
		{
			fileReader = new BufferedReader(new FileReader(new File(args[0])));
			while ((line = fileReader.readLine()) != null)
			{
				// parse line to get label and data
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}*/
	}
	
	public double sortData(ArrayList<String> docStrings, boolean test)
	{
		long startTime = System.currentTimeMillis();
		ArrayList<Document> sortDocs = new ArrayList<Document>(100);
		for (String ss:docStrings)
		{
			Document doc = new Document(this, ss, false);
			sortDocs.add(doc);
		}

		for (Document dd:sortDocs)
		{
			dd.calcWeight();
		}
		
		double aa = tree.sortData(sortDocs);
		if (test)
		{
			
			endTime = System.currentTimeMillis() - startTime;
			testOut = "";
			for (Document doc:sortDocs)
			{
				if (doc.isLabel())
					testOut += "1\n";
				else
					testOut += "0\n";
			}
		}
		return aa;
	}
	
	public void printStuff()
	{
		System.out.print(testOut);
		System.out.println(Math.round(treeTime * .001) + "seconds");
		System.out.println(Math.round(endTime * .001) + "seconds");
		System.out.println(trainAccu + " training accuracy");
		System.out.println(testAccu + " test accuracy");
	}
	

	public static void main(String[] args)
	{
		String fn = "";
		DecisionTreeClassifier dt = new DecisionTreeClassifier();
		{
			ArrayList<String> docStrings = new ArrayList<String>(100);
			fn = args[0];
			BufferedReader fileReader = null;
			try
			{
				fileReader = new BufferedReader(new FileReader(new File(fn)));
				String line = null;
				while ((line = fileReader.readLine()) != null)
				{
					docStrings.add(line);
					// parse line to get label and data
				}
				
				dt.testAccu = dt.sortData(docStrings, true);
				
				dt.printStuff();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	public void incIDFTerm(String term)
	{
		Integer idfn = termFreq.get(term);
		if (idfn == null)
		{
			termFreq.put(term, 1);
		}
		else
		{
			termFreq.put(term, idfn.intValue() + 1);
		}
	}
	
	public void calcIDF()
	{
		for (String kk:termFreq.keySet())
		{
			int dtf = termFreq.get(kk);
			if (dtf > lowFreqCutOff && dtf < highFreqCutOff)
				termIDF.put(kk, 1 + Math.log(documents.size() / dtf));
			//System.out.println(kk + ":" + termFreq.get(kk) + "," + termIDF.get(kk));
		}
	}
	
	public double getIDF(String term)
	{
		Double dd = termIDF.get(term);
		if (dd == null)
			return 0;
		else return dd.doubleValue();
	}

	public static void INIT_STOP_WORDS()
	{
		STOP_WORDS = new HashMap<String, Boolean>(300);
		STOP_WORDS.put("a", true);
		STOP_WORDS.put("about", true);
		STOP_WORDS.put("above", true);
		STOP_WORDS.put("after", true);
		STOP_WORDS.put("again", true);
		STOP_WORDS.put("against", true);
		STOP_WORDS.put("all", true);
		STOP_WORDS.put("am", true);
		STOP_WORDS.put("an", true);
		STOP_WORDS.put("and", true);
		STOP_WORDS.put("any", true);
		STOP_WORDS.put("are", true);
		STOP_WORDS.put("arent", true);
		STOP_WORDS.put("as", true);
		STOP_WORDS.put("at", true);
		STOP_WORDS.put("be", true);
		STOP_WORDS.put("because", true);
		STOP_WORDS.put("been", true);
		STOP_WORDS.put("before", true);
		STOP_WORDS.put("being", true);
		STOP_WORDS.put("below", true);
		STOP_WORDS.put("between", true);
		STOP_WORDS.put("both", true);
		STOP_WORDS.put("but", true);
		STOP_WORDS.put("by", true);
		STOP_WORDS.put("cant", true);
		STOP_WORDS.put("cannot", true);
		STOP_WORDS.put("could", true);
		STOP_WORDS.put("couldnt", true);
		STOP_WORDS.put("did", true);
		STOP_WORDS.put("didnt", true);
		STOP_WORDS.put("do", true);
		STOP_WORDS.put("does", true);
		STOP_WORDS.put("doesnt", true);
		STOP_WORDS.put("doing", true);
		STOP_WORDS.put("dont", true);
		STOP_WORDS.put("down", true);
		STOP_WORDS.put("during", true);
		STOP_WORDS.put("each", true);
		STOP_WORDS.put("few", true);
		STOP_WORDS.put("for", true);
		STOP_WORDS.put("from", true);
		STOP_WORDS.put("further", true);
		STOP_WORDS.put("had", true);
		STOP_WORDS.put("hadnt", true);
		STOP_WORDS.put("has", true);
		STOP_WORDS.put("hasnt", true);
		STOP_WORDS.put("have", true);
		STOP_WORDS.put("havent", true);
		STOP_WORDS.put("having", true);
		STOP_WORDS.put("he", true);
		STOP_WORDS.put("hed", true);
		STOP_WORDS.put("hell", true);
		STOP_WORDS.put("hes", true);
		STOP_WORDS.put("her", true);
		STOP_WORDS.put("here", true);
		STOP_WORDS.put("heres", true);
		STOP_WORDS.put("hers", true);
		STOP_WORDS.put("herself", true);
		STOP_WORDS.put("him", true);
		STOP_WORDS.put("himself", true);
		STOP_WORDS.put("his", true);
		STOP_WORDS.put("how", true);
		STOP_WORDS.put("hows", true);
		STOP_WORDS.put("i", true);
		STOP_WORDS.put("id", true);
		STOP_WORDS.put("ill", true);
		STOP_WORDS.put("im", true);
		STOP_WORDS.put("ive", true);
		STOP_WORDS.put("if", true);
		STOP_WORDS.put("in", true);
		STOP_WORDS.put("into", true);
		STOP_WORDS.put("is", true);
		STOP_WORDS.put("isnt", true);
		STOP_WORDS.put("it", true);
		STOP_WORDS.put("its", true);
		STOP_WORDS.put("its", true);
		STOP_WORDS.put("itself", true);
		STOP_WORDS.put("lets", true);
		STOP_WORDS.put("me", true);
		STOP_WORDS.put("more", true);
		STOP_WORDS.put("most", true);
		STOP_WORDS.put("mustnt", true);
		STOP_WORDS.put("my", true);
		STOP_WORDS.put("myself", true);
		STOP_WORDS.put("no", true);
		STOP_WORDS.put("nor", true);
		STOP_WORDS.put("not", true);
		STOP_WORDS.put("of", true);
		STOP_WORDS.put("off", true);
		STOP_WORDS.put("on", true);
		STOP_WORDS.put("once", true);
		STOP_WORDS.put("only", true);
		STOP_WORDS.put("or", true);
		STOP_WORDS.put("other", true);
		STOP_WORDS.put("ought", true);
		STOP_WORDS.put("our", true);
		STOP_WORDS.put("ours ", true);
		STOP_WORDS.put("ourselves", true);
		STOP_WORDS.put("out", true);
		STOP_WORDS.put("over", true);
		STOP_WORDS.put("own", true);
		STOP_WORDS.put("same", true);
		STOP_WORDS.put("shant", true);
		STOP_WORDS.put("she", true);
		STOP_WORDS.put("shed", true);
		STOP_WORDS.put("shell", true);
		STOP_WORDS.put("shes", true);
		STOP_WORDS.put("should", true);
		STOP_WORDS.put("shouldnt", true);
		STOP_WORDS.put("so", true);
		STOP_WORDS.put("some", true);
		STOP_WORDS.put("such", true);
		STOP_WORDS.put("than", true);
		STOP_WORDS.put("that", true);
		STOP_WORDS.put("thats", true);
		STOP_WORDS.put("the", true);
		STOP_WORDS.put("their", true);
		STOP_WORDS.put("theirs", true);
		STOP_WORDS.put("them", true);
		STOP_WORDS.put("themselves", true);
		STOP_WORDS.put("then", true);
		STOP_WORDS.put("there", true);
		STOP_WORDS.put("theres", true);
		STOP_WORDS.put("these", true);
		STOP_WORDS.put("they", true);
		STOP_WORDS.put("theyd", true);
		STOP_WORDS.put("theyll", true);
		STOP_WORDS.put("theyre", true);
		STOP_WORDS.put("theyve", true);
		STOP_WORDS.put("this", true);
		STOP_WORDS.put("thing", true);
		STOP_WORDS.put("those", true);
		STOP_WORDS.put("through", true);
		STOP_WORDS.put("to", true);
		STOP_WORDS.put("too", true);
		STOP_WORDS.put("under", true);
		STOP_WORDS.put("until", true);
		STOP_WORDS.put("up", true);
		STOP_WORDS.put("very", true);
		STOP_WORDS.put("was", true);
		STOP_WORDS.put("wasnt", true);
		STOP_WORDS.put("we", true);
		STOP_WORDS.put("wed", true);
		STOP_WORDS.put("well", true);
		STOP_WORDS.put("were", true);
		STOP_WORDS.put("weve", true);
		STOP_WORDS.put("were", true);
		STOP_WORDS.put("werent", true);
		STOP_WORDS.put("what", true);
		STOP_WORDS.put("whats", true);
		STOP_WORDS.put("when", true);
		STOP_WORDS.put("whens", true);
		STOP_WORDS.put("where", true);
		STOP_WORDS.put("wheres", true);
		STOP_WORDS.put("which", true);
		STOP_WORDS.put("while", true);
		STOP_WORDS.put("who", true);
		STOP_WORDS.put("whos", true);
		STOP_WORDS.put("whom", true);
		STOP_WORDS.put("why", true);
		STOP_WORDS.put("whys", true);
		STOP_WORDS.put("with", true);
		STOP_WORDS.put("wont", true);
		STOP_WORDS.put("would", true);
		STOP_WORDS.put("wouldnt", true);
		STOP_WORDS.put("you", true);
		STOP_WORDS.put("youd", true);
		STOP_WORDS.put("youll", true);
		STOP_WORDS.put("youre", true);
		STOP_WORDS.put("youve", true);
		STOP_WORDS.put("your", true);
		STOP_WORDS.put("yours", true);
		STOP_WORDS.put("yourself", true);
		STOP_WORDS.put("yourselves", true);

	}
}
