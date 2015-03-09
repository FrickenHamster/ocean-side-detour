import java.io.*;
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
	
	public DecisionTreeClassifier(String filename)
	{
		InputStream in = null;
		if (!filename.equals(""))
		{
			try
			{
				in = new FileInputStream(filename);
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			in = System.in;
		}

		INIT_STOP_WORDS();

		Scanner reader = new Scanner(in);
		String line = null;

		termFreq = new HashMap<String, Integer>(300);
		termIDF = new HashMap<String, Double>(300);
		documents = new ArrayList<Document>(100);

		int t = 0;
		while (reader.hasNextLine())
		{
			if (t > 10)
				break;
			t++;
			line = reader.nextLine();
			// parse line to get label and data
			//System.out.println(line);
			Document doc = new Document(this, line);
			documents.add(doc);
		}
		
		calcIDF();
		
		for (Document dd:documents)
		{
			dd.calcWeight();
		}
		
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
	
	

	public static void main(String[] args)
	{
		String fn = "";
		if (args.length == 1)
		{
			fn = args[0];
		}
		DecisionTreeClassifier dt = new DecisionTreeClassifier(fn);
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
			System.out.println("term:" + term + " : " + (idfn.intValue() + 1));
			termFreq.put(term, idfn.intValue() + 1);
		}
	}
	
	public void calcIDF()
	{
		for (String kk:termFreq.keySet())
		{
			termIDF.put(kk, 1 + Math.log(documents.size() / termFreq.get(kk)));
			System.out.println(kk + ":" + termFreq.get(kk) + "," + termIDF.get(kk));
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
		STOP_WORDS.put("aren't", true);
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
		STOP_WORDS.put("can't", true);
		STOP_WORDS.put("cannot", true);
		STOP_WORDS.put("could", true);
		STOP_WORDS.put("couldn't", true);
		STOP_WORDS.put("did", true);
		STOP_WORDS.put("didn't", true);
		STOP_WORDS.put("do", true);
		STOP_WORDS.put("does", true);
		STOP_WORDS.put("doesn't", true);
		STOP_WORDS.put("doing", true);
		STOP_WORDS.put("don't", true);
		STOP_WORDS.put("down", true);
		STOP_WORDS.put("during", true);
		STOP_WORDS.put("each", true);
		STOP_WORDS.put("few", true);
		STOP_WORDS.put("for", true);
		STOP_WORDS.put("from", true);
		STOP_WORDS.put("further", true);
		STOP_WORDS.put("had", true);
		STOP_WORDS.put("hadn't", true);
		STOP_WORDS.put("has", true);
		STOP_WORDS.put("hasn't", true);
		STOP_WORDS.put("have", true);
		STOP_WORDS.put("haven't", true);
		STOP_WORDS.put("having", true);
		STOP_WORDS.put("he", true);
		STOP_WORDS.put("he'd", true);
		STOP_WORDS.put("he'll", true);
		STOP_WORDS.put("he's", true);
		STOP_WORDS.put("her", true);
		STOP_WORDS.put("here", true);
		STOP_WORDS.put("here's", true);
		STOP_WORDS.put("hers", true);
		STOP_WORDS.put("herself", true);
		STOP_WORDS.put("him", true);
		STOP_WORDS.put("himself", true);
		STOP_WORDS.put("his", true);
		STOP_WORDS.put("how", true);
		STOP_WORDS.put("how's", true);
		STOP_WORDS.put("i", true);
		STOP_WORDS.put("i'd", true);
		STOP_WORDS.put("i'll", true);
		STOP_WORDS.put("i'm", true);
		STOP_WORDS.put("i've", true);
		STOP_WORDS.put("if", true);
		STOP_WORDS.put("in", true);
		STOP_WORDS.put("into", true);
		STOP_WORDS.put("is", true);
		STOP_WORDS.put("isn't", true);
		STOP_WORDS.put("it", true);
		STOP_WORDS.put("it's", true);
		STOP_WORDS.put("its", true);
		STOP_WORDS.put("itself", true);
		STOP_WORDS.put("let's", true);
		STOP_WORDS.put("me", true);
		STOP_WORDS.put("more", true);
		STOP_WORDS.put("most", true);
		STOP_WORDS.put("mustn't", true);
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
		STOP_WORDS.put("ours 	", true);
		STOP_WORDS.put("ourselves", true);
		STOP_WORDS.put("out", true);
		STOP_WORDS.put("over", true);
		STOP_WORDS.put("own", true);
		STOP_WORDS.put("same", true);
		STOP_WORDS.put("shan't", true);
		STOP_WORDS.put("she", true);
		STOP_WORDS.put("she'd", true);
		STOP_WORDS.put("she'll", true);
		STOP_WORDS.put("she's", true);
		STOP_WORDS.put("should", true);
		STOP_WORDS.put("shouldn't", true);
		STOP_WORDS.put("so", true);
		STOP_WORDS.put("some", true);
		STOP_WORDS.put("such", true);
		STOP_WORDS.put("than", true);
		STOP_WORDS.put("that", true);
		STOP_WORDS.put("that's", true);
		STOP_WORDS.put("the", true);
		STOP_WORDS.put("their", true);
		STOP_WORDS.put("theirs", true);
		STOP_WORDS.put("them", true);
		STOP_WORDS.put("themselves", true);
		STOP_WORDS.put("then", true);
		STOP_WORDS.put("there", true);
		STOP_WORDS.put("there's", true);
		STOP_WORDS.put("these", true);
		STOP_WORDS.put("they", true);
		STOP_WORDS.put("they'd", true);
		STOP_WORDS.put("they'll", true);
		STOP_WORDS.put("they're", true);
		STOP_WORDS.put("they've", true);
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
		STOP_WORDS.put("wasn't", true);
		STOP_WORDS.put("we", true);
		STOP_WORDS.put("we'd", true);
		STOP_WORDS.put("we'll", true);
		STOP_WORDS.put("we're", true);
		STOP_WORDS.put("we've", true);
		STOP_WORDS.put("were", true);
		STOP_WORDS.put("weren't", true);
		STOP_WORDS.put("what", true);
		STOP_WORDS.put("what's", true);
		STOP_WORDS.put("when", true);
		STOP_WORDS.put("when's", true);
		STOP_WORDS.put("where", true);
		STOP_WORDS.put("where's", true);
		STOP_WORDS.put("which", true);
		STOP_WORDS.put("while", true);
		STOP_WORDS.put("who", true);
		STOP_WORDS.put("who's", true);
		STOP_WORDS.put("whom", true);
		STOP_WORDS.put("why", true);
		STOP_WORDS.put("why's", true);
		STOP_WORDS.put("with", true);
		STOP_WORDS.put("won't", true);
		STOP_WORDS.put("would", true);
		STOP_WORDS.put("wouldn't", true);
		STOP_WORDS.put("you", true);
		STOP_WORDS.put("you'd", true);
		STOP_WORDS.put("you'll", true);
		STOP_WORDS.put("you're", true);
		STOP_WORDS.put("you've", true);
		STOP_WORDS.put("your", true);
		STOP_WORDS.put("yours", true);
		STOP_WORDS.put("yourself", true);
		STOP_WORDS.put("yourselves", true);
	}
}
