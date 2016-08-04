package regressionforest.training;

import java.util.Arrays;

import regressionforest.context.DataCollection;
import regressionforest.context.Feature;
import regressionforest.context.TrainingContext;
import regressionforest.structure.Node;
import regressionforest.structure.Tree;


public class TreeTrainingOperation {
	private DataCollection data_;
	private TrainingContext trainingContext_;
	private TrainingParameters parameters_;

	private int[] indices_;
	private float[] responses_;

	private StatisticsAggregator parentStatistics_, leftChildStatistics_, rightChildStatistics_;
	private StatisticsAggregator[] partitionStatistics_;
//	private StatisticsRecord statisticsRecord_;
	
	private float[] responsesForCandidate;

	public TreeTrainingOperation(TrainingContext trainingContext,
			TrainingParameters parameters, DataCollection data) {
		data_ = data;
		trainingContext_ = trainingContext;
		parameters_ = parameters;

		indices_ = new int[data.GetDataCount()];
		for (int i = 0; i < indices_.length; i++)
			indices_[i] = i;

		responses_ = new float[data.GetDataCount()];

		parentStatistics_ = trainingContext_.GetStatisticsAggregator();
		leftChildStatistics_ = trainingContext_.GetStatisticsAggregator();
		rightChildStatistics_ = trainingContext_.GetStatisticsAggregator();
		
		partitionStatistics_ = new StatisticsAggregator[parameters_.NumberOfCandidateThresholdsPerFeature + 1];
		for (int i = 0; i < parameters_.NumberOfCandidateThresholdsPerFeature + 1; i++)
			partitionStatistics_[i] = trainingContext_.GetStatisticsAggregator();
		
//		statisticsRecord_ = trainingContext_.GetStatisticsRecord();
	}

	public void TrainNodesRecurse(Node[] nodes, int nodeIndex, int i0, int i1, int recurseDepth) {
		nodes[nodeIndex] = new Node();

		// First aggregate statistics over the samples at the parent node
		parentStatistics_.Clear();
		for (int i = i0; i < i1; i++)
			parentStatistics_.Aggregate(data_, indices_[i]);
		
		StatisticsRecord statisticsRecord_= new StatisticsRecord(
				parentStatistics_.GetClassCount(),parentStatistics_.GetDimCount(),
				parentStatistics_.GetMean(),parentStatistics_.GetUncertainty());
		
		if (nodeIndex >= nodes.length / 2) { // this is a leaf node, nothing else to do
			nodes[nodeIndex].InitializeLeaf(statisticsRecord_);
			System.out.println("leaf:" + nodeIndex);
			return;
		}
		
		responsesForCandidate = new float[data_.GetDataCount()];

		double maxGain = 0.0;
		Feature bestFeature = new Feature();;
		float bestThreshold = 0.0f;

    	float[] thresholds = new float[parameters_.NumberOfCandidateThresholdsPerFeature + 1];
    	// Iterate over candidate features		
		for (int f = 0; f < parameters_.NumberOfCandidateFeatures; f++) {
			Feature feature;
			if (trainingContext_.GetFeatureBoxSizes()[0] == 0) {
				feature = trainingContext_.GetRandomFeature(data_.GetxRange(), data_.GetyRange(), data_.GetzRange());
			}
			else {
				feature = trainingContext_.GetRandomFeature();
			}
			// reset statistics
			for (int b = 0; b < parameters_.NumberOfCandidateThresholdsPerFeature + 1; b++)
				partitionStatistics_[b].Clear(); 
			// Compute feature response per samples at this node
			for (int i = i0; i < i1; i++){
				responsesForCandidate[i] = feature.GetResponse(data_, indices_[i]);
			}
	    	int nThresholds;
			if ((nThresholds = ChooseCandidateThresholds(indices_, i0, i1,responsesForCandidate, thresholds)) == 0)
				continue;
			// Aggregate statistics over sample partitions
			for (int i = i0; i < i1; i++) {
				int b = 0;
				while (b < nThresholds && responsesForCandidate[i] >= thresholds[b])
					b++;
				partitionStatistics_[b].Aggregate(data_, indices_[i]);
			}
			// Optional thresholds
			for (int t = 0; t < nThresholds; t++) {
				leftChildStatistics_.Clear();
				rightChildStatistics_.Clear();
				for (int p = 0; p < nThresholds + 1 /* i.e. nBins */; p++) {
					if (p <= t)
						leftChildStatistics_.Aggregate(partitionStatistics_[p]);
					else
						rightChildStatistics_.Aggregate(partitionStatistics_[p]);
				}
				// Compute gain over sample partitions
				double gain = trainingContext_.ComputeInformationGain(parentStatistics_, leftChildStatistics_,rightChildStatistics_);
				
				if (gain >= maxGain) {
					maxGain = gain;
					bestFeature = feature;
					bestThreshold = thresholds[t];
				}				
			}
		}

		if (trainingContext_.ShouldTerminate(maxGain,parentStatistics_.GetSampleCount())) {
			nodes[nodeIndex].InitializeLeaf(statisticsRecord_);
			System.out.println("leaf:" + nodeIndex);
			return;
		}
		
		if (maxGain == 0.0) {
			nodes[nodeIndex].InitializeLeaf(statisticsRecord_);
			System.out.println("leaf:" + nodeIndex);
			return;
		}
		
		
		// Otherwise this is a new decision node, recurse for children.
		
		// Now reorder the data point indices using the winning feature and thresholds.
		// Also recompute child node statistics so the client can decide whether
		// to terminate regressionforest.training of this branch.
		leftChildStatistics_.Clear();
		rightChildStatistics_.Clear();

		for (int i = i0; i < i1; i++) {
			responses_[i] = bestFeature.GetResponse(data_, indices_[i]);
			if (responses_[i] < bestThreshold)
				leftChildStatistics_.Aggregate(data_, indices_[i]);
			else
				rightChildStatistics_.Aggregate(data_, indices_[i]);
		}


		nodes[nodeIndex].InitializeSplit(bestFeature, bestThreshold,statisticsRecord_);

		// Now do partition sort - any sample with response greater goes left, otherwise right
		int ii = Tree.Partition(responses_, indices_, i0, i1, bestThreshold);

		System.out.println("split:" + nodeIndex);
		TrainNodesRecurse(nodes, nodeIndex * 2 + 1, i0, ii, recurseDepth + 1);
		TrainNodesRecurse(nodes, nodeIndex * 2 + 2, ii, i1, recurseDepth + 1);
	}

	private int ChooseCandidateThresholds(int[] indices, int i0, int i1,
			float[] responses, float[] thresholds) {
		if (thresholds == null || thresholds.length < parameters_.NumberOfCandidateThresholdsPerFeature + 1)
			thresholds = new float[parameters_.NumberOfCandidateThresholdsPerFeature + 1]; // lazy allocation
		float[] quantiles = thresholds; // reuse same block of memory to avoid allocation

		// Form approximate quantiles by sorting a random draw of response values
		int nThresholds;		
		if (i1 - i0 > parameters_.NumberOfCandidateThresholdsPerFeature) {
			nThresholds = parameters_.NumberOfCandidateThresholdsPerFeature;
			for (int i = 0; i < nThresholds + 1; i++)
				quantiles[i] = responses[(int) (i0 + Math.random() * (i1 - i0))]; // sample randomly from all responses
		} else {
			nThresholds = i1 - i0 - 1;
			quantiles = Arrays.copyOfRange(responses, i0, i1);
		}
		Arrays.sort(quantiles);

		if (quantiles[0] == quantiles[nThresholds])
			return 0; // all sampled response values were the same

		// compute n candidate thresholds by sampling in between n+1 approximate quantiles
		for (int i = 0; i < nThresholds; i++)
			thresholds[i] = quantiles[i] + (float) (Math.random() * (quantiles[i + 1] - quantiles[i]));

		return nThresholds;
	}
}
