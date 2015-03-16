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
	
	private static double CUTOFF = .01;
	private static int MAX_DEPTH = 50;
	
	private class DNode
	{
		public String splitAttr;
		public double splitCutOff;
		
		public int type;
		public double goodPercent;
		
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
		mcfly(root, docs, 0);
		
	}
	
	public void mcfly(DNode node, ArrayList<Document> docs, int depth)
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
			node.type = 4;
			node.goodPercent = .5;
			//System.out.println("wtf happened");
		}
		if (goodNum == 0)//case no docs
		{
			node.type = 2;
			return;
		}
		if (badNum == 0)
		{
			node.type = 1;
			return;
		}
		if (depth > MAX_DEPTH)
		{
			node.type = 4;
			double total = goodNum + badNum;
			node.goodPercent = (double)goodNum / total;
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
			node.type = 4;
			double total = goodNum + badNum;
			node.goodPercent = (double)goodNum / total;
			return;
		}
		
		
		double highInfo = 0;
		double highSplit = 0;
		String highAttr = "";
		for (String ss:attrs.keySet())
		{
			/*ArrayList<Double> weights = new ArrayList<Double>(docs.size());
			for (Document doc : docs)
			{
				if (weights.indexOf(doc.getTermWeight(ss)) == -1)
					weights.add(doc.getTermWeight(ss));
			}
			Collections.sort(weights);
			int wl = weights.size() - 1;*/
			//for (int i = 0; i < wl; i++)
			{
				double scut = CUTOFF;//(weights.get(i) + weights.get(i + 1)) / 2;
				double info = infoGain(ss, docs, scut);
				
				if (info > highInfo)
				{
					highInfo = info;
					highSplit = scut;
					highAttr = ss;
				}
			}
			/*
			double info = infoGain(ss, docs);
			//System.out.println(ss + " : " + info);
			if (info > highInfo)
			{
				highInfo = info;
				highAttr = ss;
			}*/
		}
		if (highInfo < 0.02)
		{
			/*if (goodNum >= badNum)
			{
				node.type = 1;
			}
			else
				node.type = 2;*/
			node.type = 4;
			double total = goodNum + badNum;
			node.goodPercent = (double)goodNum / total;
			return;
		}
		if (highInfo > .8)
		{
			//System.out.println("Too Good" + highAttr + " : " + highInfo);
			node.goodChild = new DNode();
			node.badChild = new DNode();
			node.goodChild.type = 1;
			node.badChild.type = 2;
			return;
		}
		//System.out.println("Choose " + highAttr + " : " + highInfo);
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
		mcfly(node.goodChild, goodDocs, depth + 1);
		mcfly(node.badChild, badDocs, depth + 1);
	}
	
	public double infoGain(String attr, ArrayList<Document> docs, double cut)
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
				if (doc.getTermWeight(attr) > cut)
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
				if (doc.getTermWeight(attr) > cut)
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
	
	public double sortData(ArrayList<Document> docs)
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
				if (curNode.type == 4)
				{
					if (Math.random() <= curNode.goodPercent)
					{
						doc.setSortedLabel(true);
					}
					else
					{
						doc.setSortedLabel(false);
					}
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
		return (double)goodSort / (double)docs.size();
	}
	
}
