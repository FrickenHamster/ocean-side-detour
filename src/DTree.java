import javax.print.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hamster
 * Date: 3/8/2015
 * Time: 8:03 PM
 */
public class DTree
{
	
	private static double CUTOFF = 1;
	
	private class DNode
	{
		public String splitAttr;
		public double splitCutOff;
		
		public int type;
		public DNode goodChild;
		public DNode badChild;
		public DNode parent;
		
	}
	
	private DNode root;
	
	public DTree()
	{
		
	}
	
	
	public void makeTree(ArrayList<Document> docs)
	{
		
		root = new DNode();
		mcfly(root, docs);
		
	}
	
	public void mcfly(DNode node, ArrayList<Document> docs)
	{
		int goodNum = 0;
		int badNum = 0;
		for (Document doc:docs)
		{
			if (doc.isLabel())
				goodNum++;
			else
				badNum++;
		}
		//System.out.println(goodNum + " : RATIO : " + badNum);
		if (goodNum == 0 && badNum == 0)
		{
			System.out.println("wtf happened");
		}
		if (goodNum == 0)//case no docs
		{
			node.type = 2;
			/*System.out.println("bad end");
			for (Document doc:docs)
			{
				System.out.println(doc);
			}*/
			return;
		}
		if (badNum == 0)
		{
			node.type = 1;
			/*System.out.println("good end");
			for (Document doc:docs)
			{
				System.out.println(doc);
			}*/
			return;
		}
		HashMap<String, Boolean> attrs = new HashMap<String, Boolean>(300);
		for (Document doc:docs)
		{
			doc.setAttrs(attrs);
		}
		// no attributes
		if (attrs.size() == 0)
		{
			if (goodNum >= badNum)
			{
				node.type = 1;
			}
			else
				node.type = 2;
			return;
		}
		double highInfo = 0;
		String highAttr = "";
		for (String ss:attrs.keySet())
		{
			double info = infoGain(ss, docs);
			//System.out.println(ss + " : " + info);
			if (info > highInfo)
			{
				highInfo = info;
				highAttr = ss;
			}
		}
		if (highInfo < 0.02)
		{
			if (goodNum >= badNum)
			{
				node.type = 1;
			}
			else
				node.type = 2;
			return;
		}
		System.out.println("Choose " + highAttr + " : " + highInfo);
		ArrayList<Document> goodDocs = new ArrayList<Document>(100);
		ArrayList<Document> badDocs = new ArrayList<Document>(100);
		node.splitAttr = highAttr;
		for (Document doc:docs)
		{
			if (doc.getTermWeight(highAttr) >= CUTOFF)
			{
				goodDocs.add(doc);
			}
			else
			{
				badDocs.add(doc);
			}
		}
		node.goodChild = new DNode();
		node.badChild = new DNode();
		mcfly(node.goodChild, goodDocs);
		mcfly(node.badChild, badDocs);
	}
	
	public double infoGain(String attr, ArrayList<Document> docs)
	{
		int docNum = docs.size();
		int goodDocNum = 0;
		int badDocNum = 0;
		int[] xGoodNums = new int[2];
		int[] xBadNums = new int[2];
		
		for (Document doc:docs)
		{
			if (doc.isLabel())
			{
				goodDocNum++;
				if (doc.getTermWeight(attr) > CUTOFF)
				{
					xGoodNums[0]++;
				}
				else
				{
					xGoodNums[1]++;
				}
			}
			else
			{
				badDocNum++;
				if (doc.getTermWeight(attr) > CUTOFF)
				{
					xBadNums[0]++;
				}
				else
				{
					xBadNums[1]++;
				}
			}
			
		}
		double hy = calcI(goodDocNum, badDocNum);
		double hgiven = 0;
		for (int i = 0; i < 2; i++)
		{
			double total = xGoodNums[i] + xBadNums[i];
			hgiven += total / (double)docNum * calcI(xGoodNums[i], xBadNums[i]);
		}
		return hy - hgiven;
	}
	
	public double calcI(double good, double bad)
	{
		double total = good + bad;
		if (good == 0 || bad == 0)
			return 0;
		return -(good / total) * Math.log(good / total) / Math.log(2) - (bad / total) * (Math.log(bad / total) / Math.log(2));
	}
	
	public void sortData(ArrayList<Document> docs)
	{
		int goodSort = 0;
		for (Document doc:docs)
		{
			DNode curNode = root;
			while(true)
			{
				if (curNode.type == 1)
				{
					doc.setSortedLabel(true);
					break;
				}
				if (curNode.type == 2)
				{
					doc.setSortedLabel(false);
					break;
				}
				double ww = doc.getTermWeight(curNode.splitAttr);
				if (ww >= CUTOFF)
				{
					curNode = curNode.goodChild;
				}
				else
				{
					curNode = curNode.badChild;
				}
			}
			if (doc.isLabel() == doc.isSortedLabel())
				goodSort++;
			//System.out.println("Sorted " + doc.isLabel() + " : " + doc.isSortedLabel());
		}
		System.out.println("Accuracy:" + (double)goodSort / (double)docs.size());
	}
	
}
