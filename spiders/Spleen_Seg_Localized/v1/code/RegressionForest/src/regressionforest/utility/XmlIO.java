package regressionforest.utility;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import regressionforest.context.Feature;
import regressionforest.structure.Forest;
import regressionforest.structure.Node;
import regressionforest.structure.Tree;
import regressionforest.training.StatisticsAggregator;
import regressionforest.training.StatisticsRecord;


public class XmlIO {
	public void SaveForestAsDocuemnt(Forest forest, String path) {
		File f = new File(path);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document doc = builder.newDocument();
			Element forestElement = doc.createElement("Forest");
			CreateForestElement(doc, forestElement,forest);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			FileOutputStream s = new FileOutputStream(f);
			t.transform(new DOMSource(doc), new StreamResult(s));
			s.close();

			System.out.println("File written.");

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Forest LoadForestFromDocument(String path) {
		Forest forest = null;
		try {			
			File file = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			Element forestElement = (Element) doc.getElementsByTagName("Forest").item(0);
			forest = XmlForestParse(forestElement); 

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return forest;
	}


	private void CreateForestElement(Document doc, Element forestElement, Forest forest){
		forestElement.setAttribute("TreeCount", Integer.toString(forest.GetTreeCount()));
		for (int t = 0; t < forest.GetTreeCount(); ++t){
			Tree tree = forest.GetTree(t);
			Element treeElement = doc.createElement("Tree" + Integer.toString(t));
			CreateTreeElement(doc, treeElement, tree);
			forestElement.appendChild(treeElement);
		}
		doc.appendChild(forestElement);
	}

	private void CreateTreeElement(Document doc, Element treeElement, Tree tree){
		int NodeCount = tree.GetNodeCount();
		int Depth = (int)(Math.log(NodeCount+1) / Math.log(2)) - 1;
		treeElement.setAttribute("Depth", Integer.toString(Depth));
		treeElement.setAttribute("NodeCount", Integer.toString(NodeCount));
		for (int n = 0; n < NodeCount; ++n){
			Node node = tree.GetNode(n);
			Element nodeElement = doc.createElement("Node" + Integer.toString(n));
			CreateNodeElement(doc, nodeElement, node);
			treeElement.appendChild(nodeElement);			
		}
	}

	private void CreateNodeElement(Document doc, Element nodeElement, Node node){		
		if (node.IsLeaf()) nodeElement.setAttribute("Flag", "leaf");
		else if (node.IsSplit()) nodeElement.setAttribute("Flag", "split");
		else {
			nodeElement.setAttribute("Flag", "null");
			return;
		}
		Element featureElement = doc.createElement("Feature");
		createFeatureElement(doc, featureElement, node.feature);	
		nodeElement.appendChild(featureElement);

		Element thresholdElement = doc.createElement("Threshold");
		thresholdElement.setAttribute("value", Float.toString(node.threshold));
		nodeElement.appendChild(thresholdElement);

		Element statisticsElement = doc.createElement("Statistics");
		CreateStatisticsElement(doc, statisticsElement, node.statisticsRecord);
		nodeElement.appendChild(statisticsElement);
	}

	private void createFeatureElement(Document doc, Element featureElement, Feature feature) {
		float[] offsets = feature.GetOffsets();
		for (int i = 0; i < offsets.length; ++i)
			featureElement.setAttribute("offset" + Integer.toString(i), Float.toString(offsets[i]));
	}

	private void CreateStatisticsElement(Document doc, Element statisticsElement, StatisticsRecord statistics) {
		int classCount = statistics.GetClassCount();
		int dimCount = statistics.GetDimCount();	
		double[][] mean = statistics.GetMean();
		double[] uncertainty = statistics.GetUncertainty();

		statisticsElement.setAttribute("classCount", Integer.toString(classCount));
		statisticsElement.setAttribute("dimCount", Integer.toString(dimCount));

		Element NodeMean = doc.createElement("mean");
		for (int c = 0; c < classCount; ++c){
			Element NodeMeanClass = doc.createElement("class" + Integer.toString(c));
			for (int d = 0; d < dimCount; ++d) {
				NodeMeanClass.setAttribute("d" + Integer.toString(d), Double.toString(mean[c][d]));			
			}
			NodeMean.appendChild(NodeMeanClass);
		}
		statisticsElement.appendChild(NodeMean);

		Element NodeUncertainty = doc.createElement("uncertainty");
		for (int c = 0; c < classCount; ++c)
			NodeUncertainty.setAttribute("uc" + Integer.toString(c), Double.toString(uncertainty[c]));
		statisticsElement.appendChild(NodeUncertainty);
	}

	private Forest XmlForestParse(Element forestElement) throws Exception{
		Forest forest = new Forest();
		int TreeCount = Integer.parseInt(forestElement.getAttribute("TreeCount"));
		for (int t = 0; t < TreeCount; ++t){
			Element treeElement = (Element) forestElement.getElementsByTagName("Tree" + Integer.toString(t)).item(0);
			Tree tree = XmlTreeParse(treeElement);
			forest.AddTree(tree);
		}
		return forest;
	}

	private Tree XmlTreeParse(Element treeElement) throws Exception{
		int Depth = Integer.parseInt(treeElement.getAttribute("Depth"));
		Tree tree = new Tree(Depth);

		int NodeCount = Integer.parseInt(treeElement.getAttribute("NodeCount"));
		for (int n = 0; n < NodeCount; ++n){
			Element nodeElement = (Element) treeElement.getElementsByTagName("Node" + Integer.toString(n)).item(0);
			Node node = XmlNodeParse(nodeElement);
			tree.SetNode(n, node);
		}
		return tree;
	}

	private Node XmlNodeParse(Element nodeElement){
		Node node = new Node();
		String strFlag = nodeElement.getAttribute("Flag");
		if (strFlag.equals("null")) return node;

		Element statisticsElement = (Element)nodeElement.getElementsByTagName("Statistics").item(0);
		StatisticsRecord statistics = XmlStatisticsParse(statisticsElement);
		if (strFlag.equals("leaf")) node.InitializeLeaf(statistics);
		else {
			Element thresholdElement =  (Element)nodeElement.getElementsByTagName("Threshold").item(0);
			float threshold = Float.parseFloat(thresholdElement.getAttribute("value"));		
			Element featureElement = (Element)nodeElement.getElementsByTagName("Feature").item(0);
			Feature feature = XmlFeatureParse(featureElement);	
			node.InitializeSplit(feature, threshold, statistics);
		}
		return node;
	}

	private Feature XmlFeatureParse(Element featureElement) {
		float[] offsets = new float[12];
		for (int i = 0; i < offsets.length; ++i)
			offsets[i] = Float.parseFloat(featureElement.getAttribute("offset" + Integer.toString(i)));
		return new Feature(offsets);	
	}

	private StatisticsRecord XmlStatisticsParse(Element statisticsElement) {
		int classCount = Integer.parseInt(statisticsElement.getAttribute("classCount"));
		int dimCount = Integer.parseInt(statisticsElement.getAttribute("dimCount"));
		double[][] mean = new double[classCount][dimCount];
		double[] uncertainty = new double[classCount];

		Element NodeMean =(Element) statisticsElement.getElementsByTagName("mean").item(0);
		for (int c = 0; c < classCount; ++c){
			Element NodeXClass = (Element) NodeMean.getElementsByTagName("class" + Integer.toString(c)).item(0);
			for (int d = 0; d < dimCount; ++d) {
				mean[c][d] = Double.parseDouble(NodeXClass.getAttribute("d" + Integer.toString(d)));			
			}
		}

		Element NodeUncertainty = (Element) statisticsElement.getElementsByTagName("uncertainty").item(0);
		for (int c = 0; c < classCount; ++c)
			uncertainty[c] = Double.parseDouble(NodeUncertainty.getAttribute("uc" + Integer.toString(c)));
		
		return new StatisticsRecord(classCount, dimCount, mean, uncertainty);	
	}
}

