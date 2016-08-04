package regressionforest.structure;

import regressionforest.context.Feature;
import regressionforest.training.StatisticsRecord;

public class Node {
	// whether the node is a split node or a leaf node  
	private Boolean bIsLeaf;
	private Boolean bIsSplit;
	// the associated statistics to compute mean, covariance, entropy, and etc. 
//	public StatisticsAggregator trainingDataStatistics;

	// fields not null for the split node
	// associated feature 
	public Feature feature;
	// associated threshold of the feature response to split the data
	public float threshold;
	// associated mean and uncertainty
	public StatisticsRecord statisticsRecord;
	
	public Node(){
		// Nodes are created null by default
		bIsLeaf = false;
		bIsSplit = false;
	}	
	
	public void InitializeLeaf(StatisticsRecord statisticsRecord_) {
		bIsLeaf = true;
		bIsSplit = false;
		feature = new Feature();
		threshold = 0.0f;
		statisticsRecord = statisticsRecord_.DeepClone();
	}
	
	public void InitializeSplit(
			Feature feature_,
			float threshold_,
			StatisticsRecord statisticsRecord_) {
      	bIsLeaf = false;
		bIsSplit = true;
		feature = feature_;
		threshold = threshold_;
		statisticsRecord = statisticsRecord_.DeepClone();
    }
	
	public Boolean IsLeaf() {
		return bIsLeaf;
	}
	
	public Boolean IsSplit() {
		return bIsSplit;
	}
	
	public Boolean IsNull()	{
		return (!bIsLeaf && !bIsSplit);
	}
}